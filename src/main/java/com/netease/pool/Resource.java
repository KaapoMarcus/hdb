package com.netease.pool;

import com.netease.util.DList.DLink;


public class Resource<T extends Disposable> {
	protected String name;
	
	protected T v;

	protected boolean busy;

	protected long atime;

	@SuppressWarnings("unchecked")
	protected Pool pool;
	
	protected boolean valid;
	
	protected boolean isClosing;
	
	protected DLink<?> poolEntry;

	@SuppressWarnings("unchecked")
	public Resource(String name, T v, Pool pool) {
		this.name = name;
		this.v = v;
		this.pool = pool;
		valid = true;
		poolEntry = new DLink(this);
	}

	
	public String getName() {
		return name;
	}
	
	public T get() {
		return v;
	}

	
	@SuppressWarnings("unchecked")
	public void use() {
		synchronized(this) {
			if (busy)
				return;
			busy = true;
		}
		touch();
		pool.onUse(this);
	}

	
	@SuppressWarnings("unchecked")
	public void release() {
		synchronized(this) {
			if (!valid || isClosing || !busy)
				return;
			busy = false;
		}
		touch();
		pool.onFree(this);
	}

	
	public boolean isBusy() {
		return busy;
	}

	
	public void touch() {
		atime = System.currentTimeMillis();
	}

	
	public long getATime() {
		return atime;
	}

	
	@SuppressWarnings("unchecked")
	public Pool getPool() {
		return pool;
	}
	
	
	@SuppressWarnings("unchecked")
	public void dispose() {
		synchronized(this) {
			if (!valid || isClosing)
				return;
			isClosing = true;
		}
		try {
			v.dispose();
		} finally {
			synchronized(this) {
				valid = false;
				busy = false;
				isClosing = false;
			}
			if (pool != null)
				pool.onDispose(this);
		}
	}
	
	
	public boolean isValid() {
		return valid;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((v == null) ? 0 : v.hashCode());
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Resource other = (Resource) obj;
		if (v == null) {
			if (other.v != null)
				return false;
		} else if (!v.equals(other.v))
			return false;
		return true;
	}

	
	@SuppressWarnings("unchecked")
	DLink getPoolEntry() {
		return poolEntry;
	}

	
	@SuppressWarnings("unchecked")
	void setPoolEntry(DLink entry) {
		poolEntry = entry;		
	}
	
	
	@SuppressWarnings("unchecked")
	void setPool(Pool pool) {
		this.pool = pool;
	}
}
