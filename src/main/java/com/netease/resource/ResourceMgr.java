package com.netease.resource;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.netease.cli.FormatUtils;
import com.netease.cli.StringTable;
import com.netease.util.DList;


public class ResourceMgr<T extends Resource> {
	private static Logger logger = Logger.getLogger(ResourceMgr.class);
	
	
	private String name;
	
	private long totalCreate;
	
	private long totalClose;
	
	private long implicitClose;
	
	private DList<ResourceRef<T>> impCloseSamples = new DList<ResourceRef<T>>();
	
	private long hangingClose;
	
	private DList<ResourceRef<T>> hangCloseSamples = new DList<ResourceRef<T>>();
	
	private long totalLifeTime; 
	
	private DList<ResourceRef<T>> activeList = new DList<ResourceRef<T>>();
	
	
	private static long sampleSize = 10;
	
	private static long diagnoseInterval = 120000;
	
	private static long hangingThreshold = 7200000;
	
	
	public ResourceMgr(String name) {
		this.name = name;
		new AsyncDiagnoseThread(this).start();
	}
	
	synchronized DList.DLink<ResourceRef<T>> addResource(ResourceRef<T> r) {
		totalCreate++;
		return activeList.addLast(r);
	}
	
	synchronized void removeResource(ResourceRef<T> r, DList.DLink<ResourceRef<T>> listEntry, int closeType) {
		listEntry.unLink();
		totalClose++;
		totalLifeTime += System.currentTimeMillis() - r.ctime;
		if (closeType == ResourceRef.IMPLICIT_CLOSE) {
			implicitClose++;
			if (impCloseSamples.size() == sampleSize)
				impCloseSamples.removeLast();
			impCloseSamples.addFirst(listEntry);
		} else if (closeType == ResourceRef.HANGING_CLOSE) {
			hangingClose++;
			if (hangCloseSamples.size() == sampleSize)
				hangCloseSamples.removeLast();
			hangCloseSamples.addFirst(listEntry);
		} 
	}

	
	synchronized public Collection<ResourceRef<T>> getActiveResources() {
		Collection<ResourceRef<T>> r = new Vector<ResourceRef<T>>();
		for (DList.DLink<ResourceRef<T>> e = activeList.getHeader().getNext(); e != activeList.getHeader(); e = e.getNext())
			r.add(e.get());
		return r;
	}

	
	synchronized public ResourceRef<T> getActiveResource(int id) {
		for (DList.DLink<ResourceRef<T>> e = activeList.getHeader().getNext(); e != activeList.getHeader(); e = e.getNext())
			if(e.get().getId()==id)
			return e.get();
		return null;
	}
	
	synchronized public Collection<ResourceRef<T>> getImpCloseSamples() {
		Collection<ResourceRef<T>> r = new Vector<ResourceRef<T>>();
		for (DList.DLink<ResourceRef<T>> e = impCloseSamples.getHeader().getNext(); e != impCloseSamples.getHeader(); e = e.getNext())
			r.add(e.get());
		return r;
	}
	
	
	synchronized public Collection<ResourceRef<T>> getHangCloseSamples() {
		Collection<ResourceRef<T>> r = new Vector<ResourceRef<T>>();
		for (DList.DLink<ResourceRef<T>> e = hangCloseSamples.getHeader().getNext(); e != hangCloseSamples.getHeader(); e = e.getNext())
			r.add(e.get());
		return r;
	}

	
	public String getName() {
		return name;
	}
	
	
	public int size() {
		return activeList.size();
	}
	
	
	public long getImplicitClose() {
		return implicitClose;
	}

	
	public long getHangingClose() {
		return hangingClose;
	}
	
	
	public long getTotalClose() {
		return totalClose;
	}

	
	public long getTotalCreate() {
		return totalCreate;
	}

	
	public long getTotalLifeTime() {
		return totalLifeTime;
	}
	
	
	public double getAvgLifeTime() {
		if (totalClose == 0)
			return 0;
		else
			return (double)totalLifeTime / totalClose;
	}
	
	
	public static long getDiagnoseInterval() {
		return diagnoseInterval;
	}

	
	@SuppressWarnings("unchecked")
	public static void setDiagnoseInterval(long diagnoseInterval) {
		ResourceMgr.diagnoseInterval = diagnoseInterval;
	}

	
	public static long getHangingThreshold() {
		return hangingThreshold;
	}

	
	@SuppressWarnings("unchecked")
	public static void setHangingThreshold(long hangingThreshold) {
		ResourceMgr.hangingThreshold = hangingThreshold;
	}

	
	public static long getSampleSize() {
		return sampleSize;
	}

	
	@SuppressWarnings("unchecked")
	public static void setSampleSize(long sampleSize) {
		ResourceMgr.sampleSize = sampleSize;
	}

	
	public StringTable showStatus() {
		StringTable t = new StringTable(name, new String[]{"VARIABLE", "VALUE"});
		t.addRow(new String[]{"TOTAL_CREATE", "" + totalCreate});
		t.addRow(new String[]{"TOTAL_CLOSE", "" + totalClose});
		t.addRow(new String[]{"IMPICIT_CLOSE", "" + implicitClose});
		t.addRow(new String[]{"HANGING_CLOSE", "" + hangingClose});
		if (totalClose == 0)
			t.addRow(new String[]{"AVG_LIFE_TIME", ""});
		else
			t.addRow(new String[]{"AVG_LIFE_TIME", "" + FormatUtils.formatTime((double)totalLifeTime / totalClose)});
		return t;
	}
	
	
	public StringTable showLiveResources() {
		Collection<ResourceRef<T>> resources = getActiveResources();
		StringTable t = showResources(name + "(LIVE)", resources, true);
		return t;
	}
	
	
	public StringTable showImpCloseSamples() {
		Collection<ResourceRef<T>> resources = getImpCloseSamples();
		StringTable t = showResources(name + "(IMPLICIT CLOSE SAMPLES)", resources, false);
		return t;
	}
	
	
	public StringTable showHangCloseSamples() {
		Collection<ResourceRef<T>> resources = getHangCloseSamples();
		StringTable t = showResources(name + "(HANGING CLOSE SAMPLES)", resources, false);
		return t;
	}

	private StringTable showResources(String label, Collection<ResourceRef<T>> resources, boolean includeStatus) {
		long now = System.currentTimeMillis();
		StringTable t;
		if (includeStatus)
			t = new StringTable(name, new String[]{"PID", "ID", "STATUS", "CREATE TIME", "DURATION", "OP COUNT", "EXTRA", "OP INFO"});
		else
			t = new StringTable(name, new String[]{"PID", "ID", "CREATE TIME", "DURATION", "OP COUNT", "EXTRA", "OP INFO"});
		for (ResourceRef<T> r : resources) {
			String pid;
			ResourceRef<?> parent = r.getParent();
			if (parent != null)
				pid = "" + parent.getId();
			else
				pid = "";
			if (includeStatus)
				t.addRow(new String[]{pid,
						"" + r.getId(),
						r.isIdle()? "Idle" : "Active",
						new Date(r.ctime).toString(),
						"" + (now - r.atime) /1000,
						"" + r.getOpCount(),
						r.getInfo(),
						"" + r.getOpInfos(true)
				});
			else
				t.addRow(new String[]{pid,
						"" + r.getId(),
						new Date(r.ctime).toString(),
						"" + (now - r.atime) /1000,
						"" + r.getOpCount(),
						r.getInfo(),
						"" + r.getOpInfos(true)
				});
		}
		return t;
	}
	
	
	private class AsyncDiagnoseThread extends Thread {
		private ResourceMgr<T> resMgr;
		
		AsyncDiagnoseThread(ResourceMgr<T> resMgr) {
			this.resMgr = resMgr;
			setDaemon(true);
		}

		@Override
		public void run() {
			while (true) {
				try {
					sleep(diagnoseInterval);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				LinkedList<ResourceRef<T>> hanging = new LinkedList<ResourceRef<T>>();
				LinkedList<ResourceRef<T>> impClose = new LinkedList<ResourceRef<T>>();
				long now = System.currentTimeMillis();
				synchronized (resMgr) {
					for (DList.DLink<ResourceRef<T>> e = resMgr.activeList.getHeader().getNext(); e != resMgr.activeList.getHeader(); e = e.getNext()) {
						ResourceRef<T> r = e.get();
						
						if (r.get() == null)
							impClose.add(r);
						else {
							
							if (r.isIdle() && now - r.atime() >= hangingThreshold) 
								hanging.add(r);
						}
					}
				}
				try {
					for (ResourceRef<T> r : hanging) {
						logger.info("CLOSE HANGING RESOUCE[" + (now - r.ctime()) / 1000 + "SECONDS IDLE]: " + r.toString());
						r.close(ResourceRef.HANGING_CLOSE);
					}
					for (ResourceRef<T> r : impClose) {
						logger.info("FOUND IMPLICIT CLOSE RESOURCE:" + r.toString());
						r.close(ResourceRef.IMPLICIT_CLOSE);
					}
				} catch (Throwable t) {
					logger.error("ERROR IN RESOURCE DIAGNOSING: " + t.getMessage());
					t.printStackTrace();
				}
			}
		}
	}
}
