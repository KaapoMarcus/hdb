package com.netease.backend.db.common.stat;

import java.util.Collection;

import com.netease.backend.db.common.utils.TimeUtils;
import com.netease.cli.StringTable;


public class StatFormater {
	public static StringTable formatStats(Collection<Stat> topLevelStats) {
		StringTable t = new StringTable("OP STAT", new String[] { "Operation", "Count", "Time(ms)", "Avg(ms)" });
		for (Stat s : topLevelStats)
			addStatusLine(s, t);
		return t;
	}


	private static void addStatusLine(Stat stat, StringTable t) {
		String avgTime;
		String timeStr;
		if (stat.getCount() > 0 && stat.getTime() == 0) {
			timeStr = "N/A";
			avgTime = "";
		} else {
			timeStr = "" + stat.getTime() / 1000000;
			if (stat.getCount() > 0) {
				avgTime = TimeUtils.formatTimeInMS(stat.getAvgTime());
			} else
				avgTime = "";
		}
		t.addRow(new String[] { stat.getIndentName(true, 2), "" + stat.getCount(), timeStr, avgTime });
		Collection<Stat> children = stat.getChildren();
		for (Stat s : children)
			addStatusLine(s, t);
	}
}
