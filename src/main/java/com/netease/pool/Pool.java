package com.netease.pool;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeoutException;

import com.netease.util.DList;
import com.netease.util.DList.DLink;


public class Pool<T extends Resource<?>, A> {
	
	public static final long TIMEOUT_INFINITE = Long.MAX_VALUE;
	
	protected Pool<T, A> parent;
	
	protected String name;
	
	protected PoolSetting<T, A> settings;
	
	protected DList<T> busyList = new DList<T>();
	
	protected DList<T> freeList = new DList<T>();
	
	protected volatile int size;
	
	protected int pendingConnections;
	
	protected int numWaiting;
	
	protected List<ResourceStateListener<T>> resourceStateListeners;
	
	protected long numWaits;
	
	protected long waitTime;
	
	protected boolean closed;
	
	protected Map<String, Pool<T, A>> subPools;
	
	protected Object userData;
	
	protected boolean disabled;
	
	protected Object lock;

	@SuppressWarnings("unchecked")
	public Pool(Pool<T, A> parent, String name, PoolSetting<T, A> settings) {
		this.name = name;
		try {
			this.settings = (PoolSetting<T, A>)settings.clone();
		} catch (CloneNotSupportedException e) { }
		resourceStateListeners = new LinkedList<ResourceStateListener<T>>();
		if (settings.schedulePolicy != ReqSchedulePolicy.DEFAULT)
			throw new RuntimeException("��DEFAULT֮���������Ȳ��Ի�û��ʵ��");
		subPools = new HashMap<String, Pool<T, A>>();
		
		if (parent != null) {
			parent.addSubPool(this);
			lock = parent;
		} else
			lock = this;
	}


	
	public T get(long timeout, boolean autoCreate, A createArg, ReqPriority priority) throws TimeoutException, IllegalArgumentException, Exception {
		if (closed)
			throw new IllegalArgumentException("Resource pool '" + name + "' has been closed.");
		if (disabled || (parent != null && parent.disabled))
			return null;
		
		long thisWaitTime = 0;
		boolean firstWait = false;
		boolean makeRoomForMeDone = false;
		while (true) {
			T r = null;
			synchronized (lock) {
				if (freeList.size() > 0) 
					r = freeList.removeFirst().get();
			}
			if (r == null) {
				if (!autoCreate)
					return null;
				
				
				if (atomicTestFreeAndIncSize()) {
					try {
						if (createArg != null)
							r = settings.factory.createResource(createArg, this);
						else
							r = settings.factory.createResource(settings.createArg, this);
						atomicDecPendingConnections();
					} catch (Exception t) {
						atomicDecSize();
						throw t;
					}
				} else if (parent != null && parent.size >= parent.settings.maxSize) {
					
					
					
					
					if (!makeRoomForMeDone) {
						makeRoomForMeDone = true;
						if (parent.makeRoomForMe(this) > 0)
							continue;
					}
				}
				
				if (r != null) {
					synchronized (resourceStateListeners) {
						for (ResourceStateListener<T> l : resourceStateListeners)
							l.onCreate(r);
					}
				}
			}
			
			if (r != null) {
				r.use();
				return r;
			}
			if (!atomicTestAndIncWaiting())
				return null;
			
			try {
				long before = System.currentTimeMillis();
				if (!firstWait) {
					numWaits++;
					firstWait = true;
				}
				
				Thread.sleep(200);
				
				long now = System.currentTimeMillis();
				thisWaitTime += (now - before);
				waitTime += (now - before);
				if (thisWaitTime >= timeout)
					throw new TimeoutException("Get resource from pool '" + name + "' time out");
			} finally {
				atomicDecWaiting();
			}
		}
	}


	
	public String getName() {
		return name;
	}


	
	public Pool<T, A> getSubPoolByName(String name, boolean autoCreate, PoolSetting<T, A> poolSettings) {
		synchronized(lock) {
			Pool<T, A> subPool = subPools.get(name);
			if (subPool == null && autoCreate) {
				if (poolSettings instanceof AutoGCPoolSetting) 
					subPool = new AutoGCPool<T, A>(this, name, (AutoGCPoolSetting<T, A>)poolSettings);
				else
					subPool = new Pool<T, A>(this, name, poolSettings);
				subPools.put(name, subPool);
			}
			return subPool;
		}
	}
	
	public Collection<Pool<T, A>> getSubPools() {
		synchronized(lock) {
			return subPools.values();
		}
	}
	
	
	
	public void addSubPool(Pool<T, A> subPool) throws IllegalArgumentException {
		if (subPool.parent != null)
			throw new IllegalArgumentException("Sub pool '" + subPool.name + "' belongs to '" + parent.name + "', can not add to '" + name + "'");
		synchronized(lock) {
			if (subPools.get(subPool.name) != null)
				throw new IllegalArgumentException("Sub pool '" + subPool.name + "' already exists");
			subPools.put(subPool.name, subPool);
		}
		subPool.parent = this;
		subPool.lock = this;
	}
	
	
	
	public boolean removeSubPool(String name) {
		synchronized(lock) {
			Pool<T, A> subPool = subPools.remove(name);
			if (subPool == null)
				return false;
			else {
				subPool.parent = null;
				subPool.lock = subPool;
				return true;
			}
		}
	}
	
	
	
	@SuppressWarnings("unchecked")
	public void add(T resource) {
		if (resource.getPool() != null)
			throw new IllegalArgumentException("��Դ�Ѿ�����ĳ��Դ�أ���������[��Դ:" + resource.getName()
					+ ",������Դ��:" + resource.getPool().getName() + ",��ǰ��Դ��:" + getName() + "]");
		resource.setPool(this);
		synchronized (lock) {
			if (resource.isBusy()) 
				busyList.addFirst(resource.getPoolEntry());
			else 
				freeList.addFirst(resource.getPoolEntry());
			size++;
			if (parent != null)
				parent.size++;
		}
	}


	
	public void remove(T resource) {
		if (resource.getPool() != this)
			throw new IllegalArgumentException("��Դ��������ָ������Դ�أ������ͷ�[��Դ:" + resource.getName()
					+ ",������Դ��:" + resource.getPool().getName() + ",��ǰ��Դ��:" + getName() + "]");
		synchronized (lock) {
			resource.getPoolEntry().unLink();
			resource.setPool(null);
			size--;
			if (parent != null)
				parent.size--;
		}
	}


	public boolean isFull() {
		return size >= settings.maxSize;
	}


	public int getMaxSize() {
		return settings.maxSize;
	}


	public void setMaxSize(int size) {
		settings.maxSize = size;
	}
	
	public int getMaxPendingCreateRequest() {
		return settings.maxPendingCreateRequest;
	}
	
	public void setMaxPendingCreateRequest(int value) {
		settings.maxPendingCreateRequest = value;
	}

	public int size() {
		return size;
	}


	public long getNumWaits() {
		return numWaits;
	}
	
	
	public long getWaitTime() {
		return waitTime;
	}
	
	
	public int getNumActiveResources() {
		return busyList.size(); 
	}
	
	
	public int getNumIdleResources() {
		return freeList.size();
	}
	
	
	public Collection<T> enumrateResources(boolean includeBusy, boolean includeFree) {
		Collection<T> c = new Vector<T>();
		synchronized (lock) {
			if (includeBusy)
				busyList.addToColleciton(c);
			if (includeFree)
				freeList.addToColleciton(c);
		}
		return c;
	}

	
	
	public void addResourceStateListener(ResourceStateListener<T> l) {
		synchronized (resourceStateListeners) {
			resourceStateListeners.add(l);
		}
	}

	
	
	public void removeResourceStateListener(ResourceStateListener<T> l) {
		synchronized (resourceStateListeners) {
			resourceStateListeners.remove(l);
		}
	}


	
	public void dropAllFrees() {
		DList<T> oldFreeList = freeList;
		synchronized (lock) {
			freeList = new DList<T>();
		}
		DLink<T> e, n;
		for (e = oldFreeList.getHeader().getNext(), n = e.getNext(); e != oldFreeList.getHeader(); e = n, n = n
				.getNext())
			e.get().dispose();
	}

	
	
	public void close() {
		closed = true;
		dropAllFrees();
		for (Pool<T, A> subPool: subPools.values())
			subPool.close();
	}

	
	public Object getUserData() {
		return userData;
	}


	public void setUserData(Object userData) {
		this.userData = userData;
	}

	public boolean isDisabled() {
		return disabled;
	}
	
	
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}
	
	
	
	public A getArg() {
		return settings.getArg();
	}
	
	
	public boolean walk(boolean includeBusy, boolean includeFree, Walker walker) {
		synchronized (lock) {
			if (includeBusy)
				for (DLink<T> e = busyList.getHeader().getNext(); e != busyList.getHeader(); e = e.getNext())
					if (!walker.walk(e.get()))
						return false;
			if (includeFree)
				for (DLink<T> e = freeList.getHeader().getNext(); e != freeList.getHeader(); e = e.getNext())
					if (!walker.walk(e.get()))
						return false;
			for (Pool<T, A> subPool: subPools.values())
				if (!subPool.walk(includeBusy, includeFree, walker))
					return false;
		}
		return true;
	}
	
	
	@SuppressWarnings("unchecked")
	void onUse(T resource) throws IllegalArgumentException {
		if (closed)
			throw new IllegalArgumentException("Resource pool '" + name + "' has been closed");
		synchronized (lock) {
			if (resource.getPool() != this)
				throw new IllegalArgumentException("Resource '" + resource.getName() + "' doesn't belong to pool '" + name + "', it belongs to pool '" + resource.getPool().getName() + "'");
			if (resource.getPoolEntry().getList() == freeList)
				resource.getPoolEntry().unLink();
			if (resource.getPoolEntry().getList() != null)
				throw new IllegalArgumentException("Can not use busy resource [resource:" + resource.getName() + ", pool:"
						+ resource.getPool().getName() + "]");
			busyList.addFirst(resource.getPoolEntry());
		}
		synchronized (resourceStateListeners) {
			for (ResourceStateListener<T> l : resourceStateListeners)
				l.onUse(resource);
		}
	}


	@SuppressWarnings("unchecked")
	void onFree(T resource) {
		if (!closed) {
			synchronized (lock) {
				resource.getPoolEntry().unLink();
				freeList.addFirst(resource.getPoolEntry());
			}
			synchronized (resourceStateListeners) {
				for (ResourceStateListener<T> l : resourceStateListeners)
					l.onRelease(resource);
			}
		} else {
			resource.dispose();
		}
	}
	
	void onDispose(T resource) {
		remove(resource);
		synchronized (resourceStateListeners) {
			for (ResourceStateListener<T> l : resourceStateListeners)
				l.onDispose(resource);
		}
	}
	
	int makeRoomForMe(Pool<T, A> me) {
		Collection<Pool<T, A>> subPoolsCopy;
		synchronized (lock) {
			subPoolsCopy = subPools.values();  
		}
		
		int freeRemain = 5;
		for (Pool<T, A> subPool : subPoolsCopy) {
			if (subPool == me)
				continue;
			freeRemain -= subPool.dropSomeFrees(freeRemain);
			if (freeRemain <= 0)
				return 5 - freeRemain;
		}
		return 5 - freeRemain;
	}

	
	private int dropSomeFrees(int max) {
		Vector<T> toBeFree = new Vector<T>(max);
		synchronized (lock) {
			int i = 0;
			while (i < max && freeList.size() > 0) {
				toBeFree.add(freeList.removeLast().get());
				i++;
			}
		}
		for (T r : toBeFree)
			r.dispose();
		return toBeFree.size();
	}
	
	
	private boolean atomicTestFreeAndIncSize() {
		synchronized (lock) {
			if (pendingConnections >= settings.maxPendingCreateRequest
				|| (parent != null 
					&& parent.pendingConnections >= parent.settings.maxPendingCreateRequest))
				return false;
			if (size >= settings.maxSize
				|| (parent != null && parent.size >= parent.settings.maxSize))
				return false;
			size++;
			pendingConnections++;
			if (parent != null) {
				parent.size++;
				parent.pendingConnections++;
			}
			return true;
		}
	}
	
	private void atomicDecSize() {
		synchronized (lock) {
			size--;
			pendingConnections--;
			if (parent != null) {
				parent.size--;
				parent.pendingConnections--;
			}
		}
	}
	
	private void atomicDecPendingConnections() {
		synchronized (lock) {
			pendingConnections--;
			if (parent != null) 
				parent.pendingConnections--;
		}
	}
	
	private boolean atomicTestAndIncWaiting() {
		synchronized (lock) {
			if (numWaiting >= settings.maxSize
				|| (parent != null && parent.numWaiting >= parent.settings.maxSize))
				return false;
			numWaiting++;
			if (parent != null)
				parent.numWaiting++;
			return true;
		}
	}
	
	private void atomicDecWaiting() {
		synchronized (lock) {
			numWaiting--;
			if (parent != null)
				parent.numWaiting--;
		}
	}
}
