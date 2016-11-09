package com.netease.backend.db.common.validate.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.netease.backend.db.common.exceptions.SQLExceptionWithCause;
import com.netease.backend.db.common.validate.AFuture;
import com.netease.backend.db.common.validate.CompletionHandler;
import com.netease.backend.db.common.validate.DataValidator;
import com.netease.backend.db.common.validate.SQLDumper;
import com.netease.backend.db.common.validate.ValidateException;
import com.netease.backend.db.common.validate.ValidateResult;
import com.netease.backend.db.common.validate.impl.policy.FetchIter;
import com.netease.backend.db.common.validate.impl.policy.FetchOptions;
import com.netease.backend.db.common.validate.impl.policy.FetchPolicy;
import com.netease.backend.db.common.validate.impl.policy.impl.EvenFetchPolicy;
import com.netease.cli.StringTable;


public class DataValidatorImpl implements DataValidator {
	
	private static final String	QUERY_TEMPLATE			= "SELECT * FROM `$TABLE_NAME` WHERE `$UNIQUE_KEY`><startKeyValue> ORDER BY `$UNIQUE_KEY` LIMIT <fetchLimit>";
	private static final String	COUNT_QUERY_TEMPLATE	= "SELECT COUNT(*) FROM `$TABLE_NAME` LIMIT 1";
	private static final String	MIN_QUERY_TEMPLATE		= "SELECT MIN(`$UNIQUE_KEY`) FROM `$TABLE_NAME` LIMIT 1";

	
	private SQLDumper			sourceDumper, destDumper;

	public AFuture<ValidateResult> validate(String sourceTable,
			String destTable, String uniqueKey, int chunkSize, int type) {
		return this.validate(sourceTable, destTable, uniqueKey, null, 0, -1,
				chunkSize, type, null, null);
	}

	
	public <A> AFuture<ValidateResult> validate(String sourceTable,
			String destTable, String uniqueKey, String startRowKeyValue,
			long rowsLimit, long rowsEstimated, int chunkSize, int type,
			CompletionHandler<CompareResult, A> handler, A attached) {
		
		try {
			if (startRowKeyValue == null) {
				startRowKeyValue = this.getMinKeyValue(sourceTable, uniqueKey);
			}
			if (rowsEstimated < rowsLimit) {
				rowsEstimated = this.countRows(sourceTable);
			}
		} catch (final ValidateException ex) {
			return onError(ex);
		}

		
		if (rowsEstimated == 0) {
			return onSkip();
		}

		
		final String st = sourceTable;
		final String dt = destTable;
		final String k = uniqueKey;
		final int t = type;
		final String v = startRowKeyValue;
		final CompletionHandler<CompareResult, A> h = handler;
		final A a = attached;

		
		final FetchPolicy p = createFetchPolicy(v, rowsLimit, rowsEstimated,
				chunkSize);

		
		final Control ctl = new Control();

		
		final FutureTask2 future = new FutureTask2(
				new Callable<ValidateResult>() {
					public ValidateResult call() {
						return DataValidatorImpl.this.doValidate(st, dt, k, t,
								p, ctl, h, a);
					}
				});
		future.setControl(ctl);

		
		final Thread thread = new Thread(future);
		thread.setDaemon(true);
		thread.start();
		return future;
	}

	private static final class Control {
		volatile boolean	cont		= true;
		volatile int		interval	= 0;
	}

	private static final class FutureTask2 extends FutureTask<ValidateResult>
			implements AFuture<ValidateResult> {

		private Control	control	= null;

		public FutureTask2(Callable<ValidateResult> callable) {
			super(callable);
		}

		
		public Control getControl() {
			return control;
		}

		
		public void setControl(Control control) {
			this.control = control;
		}

		public void adjustDelay(int millis) {
			control.interval = millis;
		}

		@Override
		public boolean cancel(boolean interrupt) {
			
			control.cont = false;
			synchronized (control) {
				control.notifyAll();
			}
			if (!interrupt) {
				
				final ValidateResult dummy = new ValidateResult();
				if (dummy.equals(checkProc(this, dummy, 100)))
					return true;
			} else {
				return super.cancel(interrupt );
			}

			
			return false;
		}
	}

	
	private <A> ValidateResult doValidate(String sourceTable, String destTable,
			String uniqueKey, int type, FetchPolicy fetchPolicy, Control ctl,
			CompletionHandler<CompareResult, A> handler, A attached) {
		final ValidateResult ret = new ValidateResult();
		final FetchIter i = fetchPolicy.iterator();
		while (ctl.cont && i.hasNext()) {
			final FetchOptions options = i.next();
			StringTable st = null, st2 = null;
			CompareResult compareResult = null;
			try {
				st = this.dumpSourceRows(sourceTable, uniqueKey, options);
				st2 = this.dumpDestRows(destTable, uniqueKey, options);
				compareResult = RSFormatter.compareStringTable(st, st2, type,
						uniqueKey);
				ret.setLastCompareResult(compareResult);
			} catch (final SQLException ex) {
				ex.printStackTrace();
				ret.setError(ex);
				break;
			} finally {
				
				if (st != null) {
					RSFormatter.disposeStringTable(st);
				}
				if (st2 != null) {
					RSFormatter.disposeStringTable(st2);
				}

				
				if (compareResult != null) {
					handler.onCompleted(compareResult, attached);
				}
			}
			if (!ret.isLastMatched() || (ret.getLastRow() < 0)) {
				
				break;
			}
			i.updateLastRowKey(ret.getLastRowKey());

			
			final int delay = ctl.interval;
			if (delay > 0) {
				synchronized (ctl) {
					try {
						ctl.wait(delay);
					} catch (InterruptedException ex) {
						ret.setError(new SQLExceptionWithCause("Interrupted", ex));
						break;
					}
				}
			}
		}
		
		ret.setRows(i.getRowsFetched());
		return ret;
	}

	
	private static AFuture<ValidateResult> onError(final ValidateException ex) {
		final FutureTask2 future = new FutureTask2(
				new Callable<ValidateResult>() {
					public ValidateResult call() {
						return new ValidateResult(ex);
					}
				});
		future.run();
		return future;
	}

	private static AFuture<ValidateResult> onSkip() {
		final FutureTask2 future = new FutureTask2(
				new Callable<ValidateResult>() {
					public ValidateResult call() {
						return new ValidateResult();
					}
				});
		future.run();
		return future;
	}

	
	private static FetchPolicy createFetchPolicy(String startRowKey,
			long rowsLimit, long rowsEstimated, int chunkSize) {
		return new EvenFetchPolicy(Long.valueOf(startRowKey), rowsEstimated,
				rowsLimit, chunkSize);
		
		
		
		
		
		
		
		
		
	}

	
	
	
	
	

	
	private StringTable dumpSourceRows(String table, String uniqueKey,
			FetchOptions options) throws SQLException {
		final String sql = this.genPlainQuerySQL(table, uniqueKey, options
				.getStartRowKey(), options.getFetchLimit());
		return this.getSourceDumper().dumpByQuery(sql,
				options.getCursorOffset(), options.getRowslimit());
	}

	
	private StringTable dumpDestRows(String table, String uniqueKey,
			FetchOptions options) throws SQLException {
		final String sql = this.genPlainQuerySQL(table, uniqueKey, options
				.getStartRowKey(), options.getFetchLimit());
		return this.getDestDumper().dumpByQuery(sql, options.getCursorOffset(),
				options.getRowslimit());
	}

	
	private int countRows(String table) throws ValidateException {
		int rows = 0;
		try {
			final StringTable st = this.getSourceDumper().dumpByQuery(
					genCountQueryStr(table), 0, 1);
			final List<String[]> data;
			final String[] r;
			if (((data = st.getData()) != null) && (data.size() > 0)
					&& ((r = data.get(0)).length > 0)) {
				rows = Integer.valueOf(r[0]);
			}
		} catch (final SQLException ex) {
			throw new ValidateException("Rows counting error", ex);
		}
		return rows;
	}

	
	private String getMinKeyValue(
		String table, String key)
		throws ValidateException
	{
		String v = null;
		try {
			final StringTable st = this.getSourceDumper().dumpByQuery(
					genMinKeyQueryStr(table, key), 0, 1);
			final List<String[]> data;
			final String[] r;
			if (((data = st.getData()) != null) && (data.size() > 0)
					&& ((r = data.get(0)).length > 0)) {
				v = r[0];
			}
		} catch (final SQLException ex) {
			throw new ValidateException("Minimal key value retrieving error",
					ex);
		}

		if (v != null && v.equals("\\N")) {
			v = null;
		}
		return v;
	}

	
	private static String genQueryStr(String table, String uniqueKey) {
		String s = QUERY_TEMPLATE;
		s = s.replace("$TABLE_NAME", table);
		s = s.replace("$UNIQUE_KEY", uniqueKey);
		return s;
	}

	
	private static String genCountQueryStr(String table) {
		String s = COUNT_QUERY_TEMPLATE;
		s = s.replace("$TABLE_NAME", table);
		return s;
	}

	
	private static String genMinKeyQueryStr(String table, String key) {
		String s = MIN_QUERY_TEMPLATE;
		s = s.replace("$TABLE_NAME", table);
		s = s.replace("$UNIQUE_KEY", key);
		return s;
	}

	
	private String genPlainQuerySQL(String table, String uniqueKey,
			String startKeyValue, int limit) {
		String sql = genQueryStr(table, uniqueKey);
		sql = sql.replace("<startKeyValue>", startKeyValue).replace(
				"<fetchLimit>", String.valueOf(limit));
		return sql;
	}

	
	private static <V> V checkProc(Future<V> future, V defaultValue, int millis) {
		if (future.isDone()) {
			try {
				return future.get();
			} catch (final InterruptedException ex) {
			} catch (final ExecutionException ex ) {
				return defaultValue;
			}
		} else if (future.isCancelled()) {
			return defaultValue;
		} else {
			try {
				return future.get(millis, TimeUnit.MILLISECONDS);
			} catch (final InterruptedException ex ) {
			} catch (final TimeoutException ex ) {
			} catch (final CancellationException ex ) {
				return defaultValue;
			} catch (final ExecutionException ex ) {
				return defaultValue;
			}
		}
		return null;
	}

	
	public void setSourceDumper(SQLDumper sourceDumper) {
		this.sourceDumper = sourceDumper;
	}

	
	public void setDestDumper(SQLDumper destDumper) {
		this.destDumper = destDumper;
	}

	
	public SQLDumper getSourceDumper() {
		return sourceDumper;
	}

	
	public SQLDumper getDestDumper() {
		return destDumper;
	}
	
	
	private static void loadMysqlDriver()
		throws ClassNotFoundException
	{
		Class.forName("com.mysql.jdbc.Driver");
	}

	public static void main(String[] args) throws Exception {
		loadMysqlDriver();
		
		DataValidatorImpl dv = new DataValidatorImpl();
		dv.setSourceDumper(new JDBCDumperImpl(
				"jdbc:mysql:
		dv.setDestDumper(new JDBCDumperImpl(
				"jdbc:mysql:
		final CompletionHandler<CompareResult, Boolean> handler = new CompletionHandler<CompareResult, Boolean>() {
			public void onCompleted(CompareResult result, Boolean attached) {
				System.out.println(result);
			}
		};

		final Future<?> future = dv.validate("test_table1", "__oak_test_table1", "column1", "220565675", 333,
				1000, 20, 0x11, handler, (Boolean) null);
		future.get();
	}
}
