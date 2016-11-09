package com.netease.backend.db.common.validate;

import java.util.concurrent.Future;

import com.netease.backend.db.common.validate.impl.CompareResult;


public interface DataValidator {

	
	<A> AFuture<ValidateResult> validate(String sourceTable, String destTable,
			String uniqueKey, String startRowKeyValue, long rowsLimit,
			long rowsEstimated, int chunkSize, int type,
			CompletionHandler<CompareResult, A> handler, A attached);

	
	Future<ValidateResult> validate(String sourceTable, String destTable,
			String uniqueKey, int chunkSize, int type);

}
