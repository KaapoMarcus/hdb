package com.netease.resource;

import java.lang.ref.WeakReference;
import java.util.LinkedList;

import com.netease.util.DList;


public class ResourceRef<T extends Resource> {
	public static final int EXPLICIT_CLOSE = 1;
	public static final int IMPLICIT_CLOSE = 2;
	public static final int HANGING_CLOSE = 3;
	
	protected static final int OPINFO_BUF_SIZE = 5;
	
	
	protected long id;
	
	protected long ctime;
	
	protected long atime;
	
	protected long opCount;
	
	protected LinkedList<String> latestOpInfos = new LinkedList<String>();
	
	protected ResourceRef<?> parent;
	
	@SuppressWarnings("unchecked")
	protected DList<ResourceRef> children;
	
	@SuppressWarnings("unchecked")
	protected DList.DLink<ResourceRef> siblingEntry;
	
	protected WeakReference<T>  referent;
	
	protected ResourceMgr<T> resMgr;
	
	protected DList.DLink<ResourceRef<T>> listEntry;
	
	protected boolean closed;
	
	protected boolean closing;
	
	protected boolean selfIdle;
	
	protected int descendantActiveCount;
	
	protected String info;
	
	protected static long nextId = 1;
	
	@SuppressWarnings("unchecked")
	public ResourceRef(ResourceRef<?> parent, T referent, ResourceMgr<T> resMgr) {
		id = nextId++;
		atime = ctime = System.currentTimeMillis();
		this.parent = parent;
		this.referent = new WeakReference<T>(referent);
		this.resMgr = resMgr;
		this.listEntry = resMgr.addResource(this);
		children = new DList<ResourceRef>();
		closed = false;
		closing = false;
		selfIdle = true;
		if (parent != null)
			siblingEntry = parent.addChild(this);
	}
	
	
	public void setLastOpInfo(String opInfo) {
		setLastOpInfo(opInfo, System.currentTimeMillis(), true);
	}
	
	@SuppressWarnings("unchecked")
	public void close(int closeType) {
		
		synchronized (this) {
			if (closed || closing)
				return;
			closing = true;
		}
		resMgr.removeResource(this, listEntry, closeType);
		while (true) {
			DList.DLink<ResourceRef> child = children.getHeader().getNext();
			if (child == children.getHeader())
				break;
			
			child.get().close(closeType);
		}
		if (parent != null) {
			parent.removeChild(siblingEntry);
			if (!selfIdle)
				parent.decDescendantActiveCount();
		}
		if (closeType == HANGING_CLOSE)
			referent.get().closeHangingResource();
		closed = true;
		closing = false;
	}
	
	public T get() {
		return referent.get();
	}
	
	public long getId() {
		return id;
	}
	
	public long atime() {
		return atime;
	}
	
	public long ctime() {
		return ctime;
	}
	
	public ResourceRef<?> getParent() {
		return parent;
	}
	
	public long getOpCount() {
		return opCount;
	}
	
	public boolean isClosed() {
		return closed;
	}
	
	public String getOpInfos(boolean oneLine) {
		String s = null;
		String delim;
		if (oneLine)
			delim = "; ";
		else
			delim = System.getProperty("line.seperator");
		synchronized(latestOpInfos){
			for (String info : latestOpInfos) {
				if (s == null)
					s = info;
				else
					s += delim + info;
			}
		}
		return s;
	}
	
	
	protected void setLastOpInfo(String opInfo, long currentTime, boolean calledByClient) {
		atime = currentTime;
		opCount++;
		synchronized(latestOpInfos){
			if (latestOpInfos.isEmpty() || !opInfo.equals(latestOpInfos.getLast())) {
				if (latestOpInfos.size() == OPINFO_BUF_SIZE)
					latestOpInfos.removeFirst();
				latestOpInfos.addLast(opInfo);
			}
		}
		if (parent != null) {
			parent.setLastOpInfo(opInfo, atime, false);
			if (selfIdle && calledByClient)
				parent.incDescendantActiveCount();
		}
		if (calledByClient)
			selfIdle = false;
	}
	
	
	public void setIdle() {
		if (selfIdle)
			return;
		if (parent != null)
			parent.decDescendantActiveCount();
		selfIdle = true;
		if (descendantActiveCount == 0)
			touch(System.currentTimeMillis());
	}
	
	public boolean isIdle() {
		return selfIdle && descendantActiveCount == 0;
	}
	
	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String toString() {
		return  resMgr.getName() + "/" + id + ": " + getOpInfos(true);
	}
	
	@SuppressWarnings("unchecked")
	synchronized protected DList.DLink<ResourceRef> addChild(ResourceRef child) {		
		DList.DLink<ResourceRef> e = new DList.DLink<ResourceRef>(child); 
		children.addLast(e);
		return e;
	}
	
	@SuppressWarnings("unchecked")
	synchronized protected void removeChild(DList.DLink<ResourceRef> siblingEntry) {  
		siblingEntry.unLink();
	}
	
	protected void decDescendantActiveCount() {
		descendantActiveCount--;
		
		if (isIdle())
			touch(System.currentTimeMillis());
		if (parent != null)
			parent.decDescendantActiveCount();
	}
	
	protected void incDescendantActiveCount() {
		boolean idle = isIdle();
		descendantActiveCount++;
		
		if (idle)
			touch(System.currentTimeMillis());
		if (parent != null)
			parent.incDescendantActiveCount();
	}
	
	public void touch(long time) {
		atime = time;
		if (parent != null)
			parent.touch(time);
	}
}
