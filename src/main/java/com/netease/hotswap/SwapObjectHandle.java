package com.netease.hotswap;


public class SwapObjectHandle {
	private HotSwapper swapper;
	private SwapObject o;
	
	SwapObjectHandle(HotSwapper swapper, SwapObject o) {
		this.swapper = swapper;
		this.o = o;
	}
	
	public SwapObject get() {
		return o;
	}
	
	public void release() throws HotSwapException {
		if (o != null) {
			swapper.releaseSwapObjectRef(o);
			o = null;
		} else
			throw new HotSwapException("Multiple release.");
	}

	@Override
	protected void finalize() throws Throwable {
		if (o != null)
			swapper.releaseSwapObjectRef(o);
	}
	
}
