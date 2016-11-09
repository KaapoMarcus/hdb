
package com.netease.backend.db.common.management.socket;

import com.netease.pool.Pool;
import com.netease.pool.Resource;


public class DbaSocketRes extends Resource<DbaSocket> {

	public DbaSocketRes(String name, DbaSocket s, Pool<?, ?> pool) {
		super(name, s, pool);
	}
}
