package com.jhh.hdb.execplan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DmlSql {

	/*
	 * 每条记录单独插入 每个节点批量插入 每个节点批量导入
	 */
	public static void insert(TableInfo tableinfo, List<Record> record_list)
			throws Exception {

	}

	/*
	 * 按照sql来删除，直接到对应的节点上执行
	 * 
	 * 提供N条记录
	 */
	public static void delete(TableInfo tableinfo, List<Record> record_list)
			throws Exception {

	}

	/*
	 * 按照sql来更新，需要检查节点是否发生变化
	 * 
	 * 提供N条记录，分别有新的记录和老的记录
	 */
	public static void update(TableInfo tableinfo, List<Record> record_list)
			throws Exception {

	}

	public static void insert(String sql) throws Exception {

		TableInfo ti = new TableInfo();
		Map<Integer, String> sql_map = new HashMap<Integer, String>();
		Map<Integer, List<Record>> record_map = new HashMap<Integer, List<Record>>();
		List<Record> record_list;

		/*
		 * 到每个节点上执行对应的sql就可以
		 */

	}

	public static void update(String sql) throws Exception {

		TableInfo ti = new TableInfo();

		Map<String, String> set_map = new HashMap<String, String>();

		boolean has_balance_set = false;
		if (has_balance_set) {
			/*
			 * 有均衡字段的设置
			 */

			boolean is_change = true;
			if (is_change) {
				/*
				 * 均衡字段有变化 , 对每一条记录做变更处理 每个节点一个线程，并发处理本节点上的记录
				 */

				List<Record> record_list = new ArrayList<Record>();

				for (int i = 0; i < record_list.size(); i++) {
					Record r = record_list.get(i);
					int old_nodeid;
					int new_nodeid;
					/*
					 * 在老节点上删除 , 在新节点上插入
					 */

				}
			} else {

				/*
				 * 均衡字段无变化
				 */

			}

		} else {
			/*
			 * 无均衡字段的设置
			 */
		}
	}

	public static void delete(String sql) throws Exception {

		TableInfo ti = new TableInfo();
		Map<Integer, String> sql_map = new HashMap<Integer, String>();
		Map<Integer, List<Record>> record_map = new HashMap<Integer, List<Record>>();
		List<Record> record_list;

		/*
		 * 到每个节点上执行对应的sql就可以
		 */

	}

}
