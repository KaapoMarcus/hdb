package com.netease.backend.db.common.utils;

import java.util.ArrayList;
import java.util.List;

import com.netease.backend.db.common.schema.BucketInfo;
import com.netease.backend.db.common.schema.OnlineMigPolicyInfo;
import com.netease.backend.db.common.schema.OnlineMigPolicyInfo.OnlineMigPolicyDbn;


public class OnlineMigPolicyInfoHelper {
	
	public static void generateBuckets(ArrayList<String> sourceUrls,
			ArrayList<String> targetUrls, ArrayList<BucketInfo> buckets,
			OnlineMigPolicyInfo migPolicy) {
		List<OnlineMigPolicyDbn> sourceDbns;
		List<OnlineMigPolicyDbn> targetDbns;

		ArrayList<BucketInfo> sourceBuckets = new ArrayList<BucketInfo>();
		for (BucketInfo bucket : buckets) {
			if (sourceUrls.contains(bucket.getSrcDBURL())) {
				sourceBuckets.add(bucket);
			}
		}

		if (targetUrls.size() == 0) {
			throw new IllegalArgumentException("target dbns can not be empty");
		}

		sourceDbns = new ArrayList<OnlineMigPolicyDbn>();
		for (String url : sourceUrls) {
			sourceDbns
					.add(new OnlineMigPolicyDbn(url, new ArrayList<Integer>()));
		}

		targetDbns = new ArrayList<OnlineMigPolicyDbn>();
		for (String url : targetUrls) {
			targetDbns
					.add(new OnlineMigPolicyDbn(url, new ArrayList<Integer>()));
		}

		int targetIndex = 0;
		for (BucketInfo bucket : sourceBuckets) {
			targetDbns.get(targetIndex++).getBuckets()
					.add(bucket.getBucketNo());
			if (targetIndex >= targetDbns.size())
				targetIndex = 0;
		}

		migPolicy.setSourceDbns(sourceDbns);
		migPolicy.setTargetDbns(targetDbns);
	}

}
