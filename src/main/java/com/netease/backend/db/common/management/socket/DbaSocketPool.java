
package com.netease.backend.db.common.management.socket;

import com.netease.pool.AutoGCPool;
import com.netease.pool.AutoGCPoolSetting;
import com.netease.pool.Pool;


public class DbaSocketPool extends AutoGCPool<DbaSocketRes, DbaSocketCreateArg> {

	private String masterID;
	
	public DbaSocketPool(Pool<DbaSocketRes, DbaSocketCreateArg> parent, String masterID, 
			String name,
			AutoGCPoolSetting<DbaSocketRes, DbaSocketCreateArg> settings) {
		super(parent, name, settings);
		this.masterID = masterID;
	}

	public String getMasterID() {
		return this.masterID;
	}
}
