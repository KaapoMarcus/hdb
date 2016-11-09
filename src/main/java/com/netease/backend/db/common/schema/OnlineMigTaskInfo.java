package com.netease.backend.db.common.schema;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.netease.backend.db.common.exceptions.OnlineMigrateException;
import com.netease.backend.db.common.management.Cluster;
import com.netease.backend.db.common.schema.OnlineMigPolicyInfo.OnlineMigPolicyDbn;


public class OnlineMigTaskInfo implements Serializable, Cloneable {
	private static final long serialVersionUID = -4628987218523184600L;

	
	public static final int MIG_STATUS_BEGIN = 0;

	
	public static final int MIG_STATUS_PROGRESS = 1;

	
	public static final int MIG_STATUS_FINISHED = 2;

	
	public static final int MIG_STATUS_FAILED = 3;

	
	public static final int MIG_STATUS_STOPPED = 4;

	
	public static final int MIG_STATUS_SYNCHRONIZING = 5;

	
	public static final int MIG_STATUS_SYNCHRONIZED = 6;

	
	public static final int STEP_STATUS_BEGIN = 0;

	
	public static final int STEP_STATUS_PREPARE = 1;

	
	public static final int STEP_STATUS_FINISHED = 2;

	public static String getStatusInformation(int status) {
		switch (status) {
		case MIG_STATUS_BEGIN:
			return "����δ��ʼ";
		case MIG_STATUS_PROGRESS:
			return "����ִ����";
		case MIG_STATUS_FINISHED:
			return "��ǰ�׶����";
		case MIG_STATUS_FAILED:
			return "����ִ��ʧ��";
		case MIG_STATUS_STOPPED:
			return "����ִ�б���ֹ";
		case MIG_STATUS_SYNCHRONIZED:
			return "�ӽڵ���ͬ��";
		case MIG_STATUS_SYNCHRONIZING:
			return "�ӽڵ�ͬ����";
		default:
			throw new IllegalArgumentException("�Ƿ���Ǩ������״̬" + status);
		}
	}

	public static String getStepInformation(int step) {
		switch (step) {
		case STEP_STATUS_BEGIN:
			return "δ��ʼ";
		case STEP_STATUS_PREPARE:
			return "׼���׶����";
		case STEP_STATUS_FINISHED:
			return "�������";
		default:
			throw new IllegalArgumentException("�Ƿ���״̬:" + step);
		}
	}

	
	private long id = -1;

	
	private ArrayList<OnlineMigDBInfo> sourceDbns;

	
	private ArrayList<OnlineMigDBInfo> targetDbns;

	
	private int migStatus = MIG_STATUS_BEGIN;

	private int stepStatus = STEP_STATUS_BEGIN;

	
	private int waitRepTimeout = 1000;

	
	private boolean ignoreConnErr = true;

	
	private boolean ignoreClientErr = true;

	
	private List<OnlineMigPolicyInfo> migPolicyList;

	
	private int deleteCount = 1000;

	
	private double deleteSleep = 1.0;

	
	private int switchInterval = 30;

	
	private boolean ignoreDuplicateKey = false;

	public OnlineMigTaskInfo(ArrayList<OnlineMigDBInfo> sourceDbns,
			ArrayList<OnlineMigDBInfo> targetDbns, int waitRepTimeout,
			boolean ignoreConnErr, boolean ignoreClientErr,
			List<OnlineMigPolicyInfo> migPolicyList, int deleteCount,
			double deleteSleep, int switchInterval, boolean ignoreDuplicateKey) {
		super();
		this.sourceDbns = sourceDbns;
		this.targetDbns = targetDbns;
		this.migStatus = MIG_STATUS_BEGIN;
		this.waitRepTimeout = waitRepTimeout;
		this.ignoreConnErr = ignoreConnErr;
		this.ignoreClientErr = ignoreClientErr;
		this.migPolicyList = migPolicyList;
		this.deleteCount = deleteCount;
		this.deleteSleep = deleteSleep;
		this.switchInterval = switchInterval;
		this.ignoreDuplicateKey = ignoreDuplicateKey;
	}

	public OnlineMigTaskInfo() {

	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getMigStatus() {
		return migStatus;
	}

	public void setMigStatus(int migStatus) {
		this.migStatus = migStatus;
	}

	public ArrayList<OnlineMigDBInfo> getTargetDbns() {
		return targetDbns;
	}

	public void setTargetDbns(ArrayList<OnlineMigDBInfo> targetDbns) {
		this.targetDbns = targetDbns;
	}

	public ArrayList<OnlineMigDBInfo> getSourceDbns() {
		return sourceDbns;
	}

	public void setSourceDbns(ArrayList<OnlineMigDBInfo> sourceDbns) {
		this.sourceDbns = sourceDbns;
	}

	public int getWaitRepTimeout() {
		return waitRepTimeout;
	}

	public void setWaitRepTimeout(int waitRepTimeout) {
		this.waitRepTimeout = waitRepTimeout;
	}

	public boolean isIgnoreClientErr() {
		return ignoreClientErr;
	}

	public void setIgnoreClientErr(boolean ignoreClientErr) {
		this.ignoreClientErr = ignoreClientErr;
	}

	public boolean isIgnoreConnErr() {
		return ignoreConnErr;
	}

	public void setIgnoreConnErr(boolean ignoreConnErr) {
		this.ignoreConnErr = ignoreConnErr;
	}

	public List<OnlineMigPolicyInfo> getMigPolicyList() {
		return migPolicyList;
	}

	public OnlineMigPolicyInfo getMigPolicy(String policyname) {
		if (migPolicyList == null)
			return null;
		for (OnlineMigPolicyInfo p : migPolicyList) {
			if (p.getPolicyName().equalsIgnoreCase(policyname))
				return p;
		}
		return null;
	}

	public void setMigPolicyList(List<OnlineMigPolicyInfo> migPolicyList) {
		this.migPolicyList = migPolicyList;
	}

	public OnlineMigDBInfo getMigDB(String url) {
		for (OnlineMigDBInfo db : sourceDbns) {
			if (db.getUrl().equalsIgnoreCase(url)) {
				return db;
			}
		}

		for (OnlineMigDBInfo db : targetDbns) {
			if (db.getUrl().equalsIgnoreCase(url)) {
				return db;
			}
		}
		return null;
	}

	public OnlineMigDBInfo getTargetMigDB(String url) {
		for (OnlineMigDBInfo db : targetDbns) {
			if (db.getUrl().equalsIgnoreCase(url)) {
				return db;
			}
		}
		return null;
	}

	public OnlineMigDBInfo getSourceMigDB(String url) {
		for (OnlineMigDBInfo db : sourceDbns) {
			if (db.getUrl().equalsIgnoreCase(url)) {
				return db;
			}
		}
		return null;
	}

	
	public void checkTaskInfo(Cluster cluster) throws OnlineMigrateException {
		OnlineMigTaskInfo migTask = this;

		if (migTask.getSourceDbns().size() < 1) {
			throw new OnlineMigrateException("����ָ��Դ���ݿ�ڵ�");
		}
		if (migTask.getTargetDbns().size() < 1) {
			throw new OnlineMigrateException("����ָ��Ŀ�����ݿ�ڵ�");
		}
		if(migTask.getSourceDbns().size() > 1 && migTask.getTargetDbns().size()>1){
			throw new OnlineMigrateException("Դ���ݿ�ڵ��Ŀ�����ݿ�ڵ㲻��ͬʱ����һ��");
		}
		for (OnlineMigDBInfo dbn : targetDbns) {
			if (sourceDbns.contains(dbn)) {
				throw new OnlineMigrateException("Ŀ�Ľڵ��б�Ӧ����Դ�ڵ��б�");
			}
		}
		if (migTask.getWaitRepTimeout() <= 0) {
			throw new OnlineMigrateException("Ǩ�������еĵȴ���ʱʱ��������0");
		}
		if (migTask.getMigPolicyList().size() <= 0) {
			throw new OnlineMigrateException("Ǩ�������е�Ǩ�Ʋ�����������ڵ���1");
		}
		for (OnlineMigDBInfo migdb : migTask.getSourceDbns()) {
			if (cluster.getDbInfo(migdb.getUrl()) == null) {
				throw new OnlineMigrateException("Ǩ�������е����ݿ�ڵ�url�����ڣ�url��"
						+ migdb.getUrl());
			}
		}
		for (OnlineMigDBInfo migdb : migTask.getTargetDbns()) {
			if (cluster.getDbInfo(migdb.getUrl()) == null) {
				throw new OnlineMigrateException("Ǩ�������е����ݿ�ڵ�url�����ڣ�url��"
						+ migdb.getUrl());
			}
		}
		for (OnlineMigPolicyInfo migPolicy : migTask.getMigPolicyList()) {
			Policy policy = cluster.getPolicy(migPolicy.getPolicyName());
			if (policy == null) {
				throw new OnlineMigrateException("Ǩ�������еĲ��Բ����ڣ�name��" + migPolicy.getPolicyName());
			}

			int migbucketsize = 0;
			int originalbucketsize = 0;
			for (OnlineMigPolicyDbn dbn : migPolicy.getSourceDbns()) {
				migbucketsize += dbn.getBuckets().size();
				originalbucketsize += policy.getDbnBucketNos(cluster
						.getDbInfo(dbn.getDb())).length;
			}
			for (OnlineMigPolicyDbn dbn : migPolicy.getTargetDbns()) {
				migbucketsize += dbn.getBuckets().size();
				originalbucketsize += policy.getDbnBucketNos(cluster
						.getDbInfo(dbn.getDb())).length;
			}
			if (migbucketsize != originalbucketsize) {
				throw new OnlineMigrateException("Ǩ����������л���Ͱδ������");
			}
		}

		for (OnlineMigDBInfo db : migTask.getTargetDbns()) {
			if (db.getDatabaseDir() == null
					|| db.getDatabaseDir().length() == 0) {
				throw new OnlineMigrateException("Ǩ�������Ŀ��ڵ��е����ݿ�Ŀ¼����Ϊ��");
			}
		}
		checkDbnInUse(cluster);

		List<Database> sourcedbs = new ArrayList<Database>();
		for (OnlineMigDBInfo dbn : sourceDbns) {
			sourcedbs.add(cluster.getDbInfo(dbn.getUrl()));
		}
		for (Policy policy : cluster.getPolicyMap().values()) {
			boolean containSource = false;
			for (Database db : sourcedbs) {
				if (policy.getDbList().contains(db)) {
					containSource = true;
					break;
				}
			}
			if (!containSource)
				continue;
			for (TableInfo table : policy.getTableList()) {
				if (table.getBaseIndex() == null)
					throw new OnlineMigrateException("Ǩ�ƹ��̵��������²������޷���ȡ���ʺϵ�������" + table.getName());
			}
		}
	}

	
	private boolean checkDbnInUse(OnlineMigDBInfo dbn, Cluster cluster)
			throws OnlineMigrateException {
		Database newdb = cluster.getDbInfo(dbn.getUrl());

		for (Policy policy : cluster.getPolicyMap().values()) {
			for (Database db : policy.getDbList()) {
				if (db.equals(newdb)) {
					return true;
				}
			}
		}
		return false;
	}

	
	protected void checkDbnInUse(Cluster cluster) throws OnlineMigrateException {
		for (OnlineMigDBInfo dbn : targetDbns) {
			if (checkDbnInUse(dbn, cluster)) {
				throw new OnlineMigrateException("�½ڵ�[" + dbn.getUrl()
						+ "]�Ѿ���ʹ����");
			}
		}
	}

	public String getDbnNamesFromUrls(Collection<OnlineMigDBInfo> urls,
			Cluster cluster) {
		StringBuilder sb = new StringBuilder();
		int i = 0;
		for (OnlineMigDBInfo url : urls) {
			Database db = cluster.getDbInfo(url.getUrl());
			if (null != db) {
				if (i > 0)
					sb.append(", ");
				sb.append(db.getName());
				i++;
			}
		}
		return sb.toString();
	}

	public boolean equals(Object other) {
		if (other == this)
			return true;
		if (!(other instanceof OnlineMigTaskInfo))
			return false;
		OnlineMigTaskInfo o = (OnlineMigTaskInfo) other;
		return sourceDbns.equals(o.sourceDbns)
				&& targetDbns.equals(o.targetDbns)
				&& waitRepTimeout == o.waitRepTimeout
				&& migPolicyList.equals(o.migPolicyList)
				&& ignoreConnErr == o.ignoreConnErr
				&& ignoreClientErr == o.ignoreClientErr;
	}

	public int getStepStatus() {
		return stepStatus;
	}

	public void setStepStatus(int stepStatus) {
		this.stepStatus = stepStatus;
	}

	
	public Object clone() {
		try {
			OnlineMigTaskInfo cloned = (OnlineMigTaskInfo) super.clone();
			return cloned;
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	public int getDeleteCount() {
		return deleteCount;
	}

	public void setDeleteCount(int deleteCount) {
		this.deleteCount = deleteCount;
	}

	public double getDeleteSleep() {
		return deleteSleep;
	}

	public void setDeleteSleep(double deleteSleep) {
		this.deleteSleep = deleteSleep;
	}

	public int getSwitchInterval() {
		return switchInterval;
	}

	public void setSwitchInterval(int switchInterval) {
		this.switchInterval = switchInterval;
	}

	public boolean isIgnoreDuplicateKey() {
		return ignoreDuplicateKey;
	}

	public void setIgnoreDuplicateKey(boolean ignoreDuplicateKey) {
		this.ignoreDuplicateKey = ignoreDuplicateKey;
	}

	public boolean containPolicy(String policy) {
		for (OnlineMigPolicyInfo p : migPolicyList) {
			if (p.getPolicyName().equalsIgnoreCase(policy)) {
				return true;
			}
		}
		return false;
	}
}
