package com.netease.backend.db.common.validate;


public interface CompletionHandler<V, A> {

	
	void onCompleted(V result, A attached);

}
