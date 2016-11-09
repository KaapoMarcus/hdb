package com.netease.backend.db.common.mx;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.List;

import com.netease.backend.db.common.stat.Stat;
import com.netease.backend.db.common.utils.ArrayUtils;


public class SimpleStat
	extends RelatedStringMatrix
{
	private static final long		serialVersionUID	= 622497321171150982L;
	private static final String[]	HEADER;
	private static final String[]	DATA0;

	static {
		HEADER = new String[] { "Operation", "Count", "Time(ms)", "Avg(ms)" };
		DATA0 = new String[] { "", "0", "0", "0" };
	}

	public static SimpleStat fromStat(
		Stat stat)
	{
		final SimpleStat s = new SimpleStat(stat.getName());
		final Collection<Stat> chren = stat.getChildren();
		if ((chren != null) && (chren.size() > 0)) {
			for (final Stat child : chren) {
				s.addChild(fromStat(child));
			}
		} else {
			s.incStat(null, stat.getCount(), stat.getTime());

		}
		return s;
	}

	public SimpleStat(String name) {
		super(name, ArrayUtils.copyOf(HEADER, HEADER.length));

		this.init();
	}

	@Override
	protected SimpleStat clone()
	{
		return (SimpleStat) super.clone();
	}

	
	private void init()
	{
		super.addRow(ArrayUtils.copyOf(DATA0, DATA0.length));
	}

	public void incStat(
		String opt, long count, long timeCost)
	{
		
		
		if (count * timeCost < 0)
			throw new IllegalArgumentException();

		final String[] oldStat = this.getStat(opt);
		final long c = Long.valueOf(oldStat[1]) + count;
		final long t = Long.valueOf(oldStat[2]) + timeCost;
		final String[] newStat = ArrayUtils.copyOf(oldStat, oldStat.length);
		newStat[1] = String.valueOf(c);
		newStat[2] = String.valueOf(t);
		newStat[3] = String.valueOf(this.getAvgTimeStr(t, c));

		super.setRow(0, newStat);
	}

	private String getAvgTimeStr(
		long time, long count)
	{
		return count > 0 ? ((new DecimalFormat("#.##")).format((double) time
				/ count)) : "0";
	}

	public void clear()
	{
		super.truncate();
		this.init();
	}

	
	private String[] getStat(
		String opt)
	{
		return this.data().get(0);
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	@Override
	protected void onChildRowAdded(
		String name, String[] header, String[] row)
	{
		this.incStat(null, Long.valueOf(row[1]), Long.valueOf(row[2]));
	}

	@Override
	protected void onChildRowRemoved(
		String name, String[] header, String[] row)
	{
		this.incStat(null, -Long.valueOf(row[1]), -Long.valueOf(row[2]));
	}

	@Override
	protected void onChildRowChanged(
		String name, String[] header, String[] oldRow, String[] newRow)
	{
		this.incStat(null, Long.valueOf(newRow[1]) - Long.valueOf(oldRow[1]),
				Long.valueOf(newRow[2]) - Long.valueOf(oldRow[2]));
	}

	@Override
	protected void onChildDataCleared(
		String name, String[] header, List<String[]> rows)
	{
		for (final String[] row : rows) {
			this.onChildRowRemoved(name, header, row);
		}
	}

}
