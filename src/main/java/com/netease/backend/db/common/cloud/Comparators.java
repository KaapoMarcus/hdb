package com.netease.backend.db.common.cloud;

import java.util.Comparator;

import com.netease.backend.db.common.schema.TableInfo;
import com.netease.backend.db.common.schema.cloud.Table.TableSortField;


public class Comparators {
	public static Comparator<TableInfo> getTableInfoComparator(TableSortField sortField) {
		return new Comparator<TableInfo>() {

			public int compare(TableInfo o1, TableInfo o2) {
				
				return 0;
			}
			
		};
	}
}
