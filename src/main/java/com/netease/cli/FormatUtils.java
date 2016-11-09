package com.netease.cli;

import java.text.NumberFormat;


public class FormatUtils {
	
	public static String formatTime(double nanoTime) {
		if (nanoTime > 100000000000L)
			return "" + (long) nanoTime / 1000000000 + "s";
		else if (nanoTime > 10000000000L) {
			NumberFormat nf = NumberFormat.getNumberInstance();
			nf.setMaximumFractionDigits(1);
			return nf.format(nanoTime / 1000000000) + "s";
		} else if (nanoTime > 100000000L) {
			return "" + (long) nanoTime / 1000000 + "ms";
		} else if (nanoTime > 10000000) {
			NumberFormat nf = NumberFormat.getNumberInstance();
			nf.setMaximumFractionDigits(1);
			return nf.format(nanoTime / 1000000) + "ms";
		} else {
			NumberFormat nf = NumberFormat.getNumberInstance();
			nf.setMaximumFractionDigits(2);
			return nf.format(nanoTime / 1000000) + "ms";
		}
	}
}
