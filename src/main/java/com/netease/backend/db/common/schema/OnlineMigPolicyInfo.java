package com.netease.backend.db.common.schema;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.netease.backend.db.common.management.Cluster;


public class OnlineMigPolicyInfo implements Cloneable, Serializable {
	private static final long serialVersionUID = 1L;

	
	public static class OnlineMigPolicyDbn implements Cloneable, Serializable {
		private static final long serialVersionUID = 1L;

		private String db;

		private ArrayList<Integer> buckets;

		public ArrayList<Integer> getBuckets() {
			return buckets;
		}

		public void setBuckets(ArrayList<Integer> buckets) {
			this.buckets = buckets;
		}

		public String getDb() {
			return db;
		}

		public void setDb(String db) {
			this.db = db;
		}

		public OnlineMigPolicyDbn(String db, ArrayList<Integer> buckets) {
			super();
			this.db = db;
			this.buckets = buckets;
		}

		public String getDesc(Cluster cluster) {
			StringBuilder sb = new StringBuilder();
			Database database = cluster.getDbInfo(db);
			if (database == null) {
				throw new RuntimeException("������Ϣ���Ҳ�����Ӧ�����ݿ�ڵ㣺" + db);
			}
			sb.append(database.getName());
			sb.append(":[");
			if (buckets.size() == 0) {
				sb.append("��");
			} else {
				for (int i = 0; i < buckets.size(); i++) {
					sb.append(buckets.get(i));
					if (i != buckets.size() - 1)
						sb.append(",");
				}
			}
			sb.append("]");
			return sb.toString();
		}

		
		@SuppressWarnings("unchecked")
		public Object clone() {
			try {
				OnlineMigPolicyDbn cloned = (OnlineMigPolicyDbn) super.clone();
				cloned.buckets = (ArrayList<Integer>) buckets.clone();
				return cloned;
			} catch (CloneNotSupportedException e) {
				return null;
			}
		}

	}

	
	private String policyName;

	
	private List<OnlineMigPolicyDbn> sourceDbns;

	
	private List<OnlineMigPolicyDbn> targetDbns;

	public String getPolicyName() {
		return policyName;
	}

	public void setPolicyName(String policyName) {
		this.policyName = policyName;
	}

	public List<OnlineMigPolicyDbn> getSourceDbns() {
		return sourceDbns;
	}

	public void setSourceDbns(List<OnlineMigPolicyDbn> sourceDbns) {
		this.sourceDbns = sourceDbns;
	}

	public List<OnlineMigPolicyDbn> getTargetDbns() {
		return targetDbns;
	}

	public void setTargetDbns(List<OnlineMigPolicyDbn> targetDbns) {
		this.targetDbns = targetDbns;
	}

	public boolean equals(Object other) {
		if (other == this)
			return true;
		if (!(other instanceof OnlineMigPolicyInfo))
			return false;
		OnlineMigPolicyInfo o = (OnlineMigPolicyInfo) other;
		return policyName.equals(o.policyName)
				&& sourceDbns.equals(o.sourceDbns)
				&& targetDbns.equals(o.targetDbns);
	}

	public String getBucketsDesc(Cluster cluster) {
		StringBuilder sb = new StringBuilder();
		for (OnlineMigPolicyDbn dbn : sourceDbns) {
			sb.append(dbn.getDesc(cluster));
			sb.append("; ");
		}

		for (OnlineMigPolicyDbn dbn : targetDbns) {
			sb.append(dbn.getDesc(cluster));
			sb.append("; ");
		}

		return sb.toString();
	}

	public OnlineMigPolicyInfo(String policyName,
			List<OnlineMigPolicyDbn> sourceDbns,
			List<OnlineMigPolicyDbn> targetDbns) {
		super();
		this.policyName = policyName;
		this.sourceDbns = sourceDbns;
		this.targetDbns = targetDbns;
	}

	public OnlineMigPolicyInfo() {
		super();
	}

	
	public Object clone() {
		try {
			OnlineMigPolicyInfo cloned = (OnlineMigPolicyInfo) super.clone();
			List<OnlineMigPolicyDbn> newDbns = new ArrayList<OnlineMigPolicyDbn>();
			for (OnlineMigPolicyDbn dbn : sourceDbns) {
				newDbns.add((OnlineMigPolicyDbn) dbn.clone());
			}
			cloned.sourceDbns = newDbns;

			newDbns = new ArrayList<OnlineMigPolicyDbn>();
			for (OnlineMigPolicyDbn dbn : targetDbns) {
				newDbns.add((OnlineMigPolicyDbn) dbn.clone());
			}
			cloned.targetDbns = newDbns;
			return cloned;
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	public OnlineMigPolicyDbn getSourceDbn(String url) {
		for (OnlineMigPolicyDbn dbn : sourceDbns) {
			if (dbn.getDb().equalsIgnoreCase(url))
				return dbn;
		}
		return null;
	}

	public String getDbUrl(int bucketno) {
		for (OnlineMigPolicyDbn dbn : sourceDbns) {
			for (Integer b : dbn.getBuckets()) {
				if (b == bucketno)
					return dbn.getDb();
			}
		}
		for (OnlineMigPolicyDbn dbn : targetDbns) {
			for (Integer b : dbn.getBuckets()) {
				if (b == bucketno)
					return dbn.getDb();
			}
		}
		return null;
	}
}
