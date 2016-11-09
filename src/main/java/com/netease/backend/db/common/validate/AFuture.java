package com.netease.backend.db.common.validate;

import java.util.concurrent.Future;


public interface AFuture<V> extends Future<V> {

	
	void adjustDelay(int millis);

}
