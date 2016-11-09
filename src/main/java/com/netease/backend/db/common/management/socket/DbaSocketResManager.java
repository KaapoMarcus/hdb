
package com.netease.backend.db.common.management.socket;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.netease.pool.AutoGCPoolSetting;
import com.netease.pool.ReqPriority;
import com.netease.pool.ReqSchedulePolicy;
import com.netease.pool.Walker;


public class DbaSocketResManager {
	
	
	private static final int MAX_DBA_SOCKETS = 200;
	
	private static final int MAX_DBA_PENDING = 20;
	
	private static final int CONNECT_TIMEOUT = 15000;
	
	private static final int SO_TIMEOUT = -1;
	
	private static final int CONNECT_RETRY_TIMES = 3;
	
	private static final int FREE_TIMEOUT = 600000;
	
	private static final int GC_INTERVAL = 300000;
	
	private static final int WAIT_TIMEOUT = 10000;
	
	private static final String MASTERID_FORMAT = "%1$s_%2$d";

	
	private HashMap<String, DbaSocketPool> pools = new HashMap<String, DbaSocketPool>();
	
	private AutoGCPoolSetting<DbaSocketRes, DbaSocketCreateArg> poolSetting = 
		new AutoGCPoolSetting<DbaSocketRes, DbaSocketCreateArg>(
				null, null, MAX_DBA_SOCKETS, MAX_DBA_PENDING, ReqSchedulePolicy.DEFAULT, 0, 
				Integer.MAX_VALUE, Integer.MAX_VALUE);
	
	private static DbaSocketResManager inst = new DbaSocketResManager();
	
	
	private DbaSocketResManager() {
	}
	
	public static DbaSocketResManager getInstance() {
		return inst;
	}
	
	public void clear() {
		synchronized (pools) {
			Iterator<Entry<String, DbaSocketPool>> iter = pools.entrySet().iterator();
			while (iter.hasNext()) {
				Entry<String, DbaSocketPool> entry = iter.next();
				entry.getValue().close();
			}
			pools.clear();
		}
	}
	
	public DbaSocketRes getDbaSocketRes(String master, int port, String user) throws Exception {
		DbaSocketPool pool = getTopPool(master, port, true);
		DbaSocketPool subPool = getSubPool(pool, master, port, user, true);
		DbaSocketRes res = subPool.get(WAIT_TIMEOUT, true, null, ReqPriority.NORMAL);
		if (res == null)
			throw new Exception("Get null from pool, " + String.format(MASTERID_FORMAT, new Object[] {master, port}));
		return res;
	}
	
	
	
	public Collection<DbaSocketPool> getTopPools() {
		synchronized (pools) {
			return new ArrayList<DbaSocketPool>(pools.values());
		}
	}
	
	
	public void resetMaxConnections(int maxConns) {
		Collection<DbaSocketPool> topPools = getTopPools();
		for (DbaSocketPool pool: topPools)
			pool.setMaxSize(maxConns);
	}
	
	public void resetMaxPendingConnections(int maxPendingConns) {
		Collection<DbaSocketPool> topPools = getTopPools();
		for (DbaSocketPool pool: topPools)
			pool.setMaxPendingCreateRequest(maxPendingConns);
	}
	
	
	public void resetGcThreshold(int threshold) {
		Collection<DbaSocketPool> topPools = getTopPools();
		for (DbaSocketPool pool: topPools)
			pool.setGcThreshold(threshold);
	}
	
	
	public void resetGcInterval(int interval) {
		Collection<DbaSocketPool> topPools = getTopPools();
		for (DbaSocketPool pool: topPools)
			pool.setGcInterval(interval);
	}
	
	public boolean walk(boolean includeBusy, boolean includeFree, Walker walker) {
		Collection<DbaSocketPool> topPools = getTopPools();
		for (DbaSocketPool pool: topPools)
			if (!pool.walk(includeBusy, includeFree, walker))
				return false;
		return true;
	}
	
	public DbaSocketPool getTopPool(String master, int port, boolean autoCreate) {
		String masterId = String.format(MASTERID_FORMAT, new Object[] {master, port});
		DbaSocketPool pool = pools.get(masterId);
		if (pool != null || !autoCreate)
			return pool;
		synchronized (pools) {
			pool = pools.get(masterId);
			if (pool == null) {
				pool = new DbaSocketPool(null, masterId, masterId, poolSetting);
				pools.put(masterId, pool);
			}
		}
		return pool;
	}
	
	public DbaSocketPool getSubPool(DbaSocketPool pool, String master, int port, String user, boolean autoCreate) {
		DbaSocketPool subPool = (DbaSocketPool)pool.getSubPoolByName(user, false, null);
		if (subPool != null || !autoCreate)
			return subPool;
		
		AutoGCPoolSetting<DbaSocketRes, DbaSocketCreateArg> setting = 
			new AutoGCPoolSetting<DbaSocketRes, DbaSocketCreateArg>(
					new DbaSocketCreateArg(master, port, CONNECT_TIMEOUT, SO_TIMEOUT, CONNECT_RETRY_TIMES), 
					new DbaSocketResFactory(), Integer.MAX_VALUE, 
					ReqSchedulePolicy.DEFAULT, 0, FREE_TIMEOUT, GC_INTERVAL);
		subPool = new DbaSocketPool(null, String.format(MASTERID_FORMAT, new Object[]{master, port}), user, setting);
		try {
			pool.addSubPool(subPool);
		} catch (Exception e) {}
		
		return (DbaSocketPool)pool.getSubPoolByName(user, false, null);
	}
}
