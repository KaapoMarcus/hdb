package com.netease.backend.db.common.schema;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.netease.backend.db.common.utils.StringUtils;



public class MigTaskInfo implements Serializable
{
	private static final long serialVersionUID = -1271753389494453726L;
	
	
	public static final int MIG_TYPE_ONLINE = 1;
	
	
	public static final int MIG_TYPE_OFFLINE = 2;
	
	
	public static final int MIG_TYPE_CONOFFLINE =3;
	
	
	public static final int MIG_TYPE_TMP_TABLE = 4;
	
	
	
	public static final int OPT_TYPE_MIG_BUCKET = 1;
	
	
	public static final int OPT_TYPE_DELETE_BUCKET = 2;
	
	
	
	public static final int STATUS_INIT = 0;
	
	public static final int STATUS_SUCCESS_FINISHED = 1;
	
	
	public static final int STATUS_FAIL_FINISHED = 2;
	
	
	public static final int STATUS_REQUEST = 3;
	
	
	public static final int STATUS_REQUEST_HANGING =4;
	
	
	public static final int STATUS_SCHEDULING = 5;
	
	
	public static final int STATUS_TRANSFERING = 6;
	
	
	public static final int STATUS_TRANSFER_HANGING = 7;
	
	
	public static final int STATUS_TRANSFERED = 8;
	
	
	public static final int STATUS_FEEDBACK = 9;
	
	
	public static final int STATUS_FEEDBACK_HANGING = 10;
	
	
	public static final int STATUS_MENUAL_INTERRUPTED = 11;
	
	
	
	public static final int STATUS_BUCKET_DUMP = 12;
	
	
	public static final int STATUS_BUCKET_DUMPED = 13;
	
	
	public static final int STATUS_BUCKET_DUMP_FAILED = 14;
	
	
	public static final int STATUS_BUCKET_RELOAD = 15;
	
	
	public static final int STATUS_BUCKET_RELOADED = 16;
	
	
	public static final int STATUS_BUCKET_RELOAD_FAILED = 17;
	
	
	public static final int STATUS_DEL_DIRTY = 18;
	
	
	public static final int STATUS_DEL_DIRTY_FAILED = 19;
	
	
	public static final int STATUS_DIRTY_DELETED = 20;
	
	
	public static final int MIG_SELECT_NO_ORDER = 0;
	
	
	public static final int MIG_SELECT_ASCEND_ORDER = 1;
	
	
	public static final int MIG_SELECT_DESCEND_ORDER = 2;
	
	
	private int id = 0;
	
	
	private int type = MIG_TYPE_ONLINE;
	
	
	private String policy = "";
	
	
	private int[] bucketNos = null;
	
	
	private String srcDBUrl = "";
	
	
	private String desDBUrl = "";
	
	
	boolean resetTable = false;
	
	
	int opt = OPT_TYPE_MIG_BUCKET;
	
	
	boolean useStartId = true;
	
	
	boolean inBinLog = false;
	
	
	private int totalRecord = 0;
	
	
	private int finishedRecord = 0;
	
	
	private int status = STATUS_INIT;
	
	
	private Date startTime =  new Date(0);
	
	
	private Date finishTime = new Date(0);
	
	
	private int selectOrder;
	
	
	private transient List<TableMigResult> tableMigResults = new LinkedList<TableMigResult>();
	
	private boolean rebuildBucketno = false;
	
	
	public MigTaskInfo(int id, int type, String ply, int[] bucketNos, String srcDB, String desDB, int order)
	{
		this.id = id;
		this.type = type;
		this.policy = ply;
		Arrays.sort(bucketNos);
		this.bucketNos = bucketNos;
		this.srcDBUrl = srcDB;
		this.desDBUrl = desDB;
		this.selectOrder = order;
	}
	
	
	public boolean equals(Object otherObject)
	{
		if(this==otherObject) return true;
		
		if(otherObject==null) return false;
		
		if(this.getClass()!=otherObject.getClass()) return false;
		
		MigTaskInfo other = (MigTaskInfo)otherObject;
		
		if(bucketNos.length != other.bucketNos.length)
			return false;
		for(int i=0;i<bucketNos.length;i++)
			if(bucketNos[i] != other.bucketNos[i])
				return false;
		
		return (this.id == other.id 
				&& this.type == other.type && this.policy.equals(other.policy) 
				&& this.srcDBUrl.equals(other.srcDBUrl) && this.desDBUrl.equals(other.desDBUrl));
	}
	
	
	public static String getStatusDesc(int status)
	{
		switch (status)
		{
			case STATUS_INIT:
				return "δ��ʼ";
			case STATUS_SUCCESS_FINISHED:
				return "�ɹ����";
			case STATUS_FAIL_FINISHED:
				return "ʧ�ܽ���";
			case STATUS_REQUEST:
				return "����ClientǨ��";
			case STATUS_REQUEST_HANGING:
				return "����Clientʧ��";
			case STATUS_SCHEDULING:
				return "������";
			case STATUS_TRANSFERING:
				return "���ݴ�����";
			case STATUS_TRANSFER_HANGING:
				return "���ݴ���ʧ��";
			case STATUS_TRANSFERED:
				return "��ִ�е�δ֪ͨClient";
			case STATUS_FEEDBACK:
				return "�ȴ�Clientȷ��";
			case STATUS_FEEDBACK_HANGING:
				return "Clientȷ��ʧ��";
			case STATUS_MENUAL_INTERRUPTED:
				return "����Ϊ�ж�";
			case STATUS_BUCKET_DUMP:
				return "���ڵ���";
			case STATUS_BUCKET_DUMPED:
				return "�������";
			case STATUS_BUCKET_DUMP_FAILED:
				return "����ʧ��";
			case STATUS_BUCKET_RELOAD:
				return "���ڵ���";
			case STATUS_BUCKET_RELOADED:
				return "�������";
			case STATUS_BUCKET_RELOAD_FAILED:
				return "����ʧ��";
			case STATUS_DEL_DIRTY:
				return "����ɾ��������";
			case STATUS_DEL_DIRTY_FAILED:
				return "ɾ��������ʧ��";
			case STATUS_DIRTY_DELETED:
				return "ɾ�������ݳɹ�";
			default:
				return "δ֪״̬";
		}
	}

	public int[] getBucketNos() {
		return bucketNos;
	}

	public void setBucketNos(int[] bucketNos) {
		Arrays.sort(bucketNos);
		this.bucketNos = bucketNos;
	}

	public String getDesDBUrl() {
		return desDBUrl;
	}

	public void setDesDBUrl(String desDBUrl) {
		this.desDBUrl = desDBUrl;
	}

	public int getFinishedRecord() {
		return finishedRecord;
	}

	public void setFinishedRecord(int finishedRecord) {
		this.finishedRecord = finishedRecord;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPolicy() {
		return policy;
	}

	public void setPolicy(String policy) {
		this.policy = policy;
	}

	public String getSrcDBUrl() {
		return srcDBUrl;
	}

	public void setSrcDBUrl(String srcDBUrl) {
		this.srcDBUrl = srcDBUrl;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getTotalRecord() {
		return totalRecord;
	}

	public void setTotalRecord(int totalRecord) {
		this.totalRecord = totalRecord;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Date getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(Date finishTime) {
		this.finishTime = finishTime;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	


	public int getSelectOrder() {
		return selectOrder;
	}

	public void setSelectOrder(int selectOrder) {
		this.selectOrder = selectOrder;
	}
	
	public String toString()
	{
		String typeName = "";
		if (opt == OPT_TYPE_MIG_BUCKET)
		{
			if(type == MIG_TYPE_ONLINE)
				typeName = "����Ǩ��";
			else if(type == MIG_TYPE_OFFLINE)
				typeName = "����Ǩ��";
			else if(type == MIG_TYPE_TMP_TABLE)
				typeName = "������ʱ���Ǩ��";
		}
		else
			typeName = "�ֹ��������";
		return "[Ǩ������id-"+id+"������-"+typeName+"��ResetTable-"+this.resetTable+", ����-"+policy+"��Ͱ��-"+
			StringUtils.convertArraytoString(bucketNos,",")+"��Դ-"+srcDBUrl+"��Ŀ��-"+desDBUrl+"]";
	}
	
	
	public boolean isInTask(int bucketNo)
	{
		if(this.bucketNos == null)
			return false;
		for(int i=0;i<bucketNos.length;i++)
			if(bucketNo == bucketNos[i])
				return true;
		return false;
	}

	public List<TableMigResult> getTableMigResults() {
		return tableMigResults;
	}

	public void setTableMigResults(List<TableMigResult> tableMigResults) {
		this.tableMigResults = tableMigResults;
	}
	
	
	public String getTaskDesc()
	{
		String desc = "Task["+id+"] Bucketno["+StringUtils.convertArraytoString(this.bucketNos, ",")+
				"] Policy["+this.policy+"] ResetTable["+this.resetTable+"] :\n";
		if(this.tableMigResults != null)
			for(TableMigResult result : tableMigResults)
			{
				desc += "Table["+result.getTableName()+"] select_count="+result.getSelectCount()+
				", insert_count="+result.getInsertCount()+", delete_count="+result.getDeleteCount();
				if(result.getErrMsg()!=null &&! result.getErrMsg().equals(""))
					desc += ", Exception: "+result.getErrMsg();
				desc += "\n";
			}
		return desc;
	}

	public boolean isResetTable() {
		return resetTable;
	}

	public void setResetTable(boolean resetTable) {
		this.resetTable = resetTable;
	}

	public boolean isInBinLog() {
		return inBinLog;
	}

	public void setInBinLog(boolean inBinLog) {
		this.inBinLog = inBinLog;
	}

	public int getOpt() {
		return opt;
	}

	public void setOpt(int opt) {
		this.opt = opt;
	}

	public boolean isUseStartId() {
		return useStartId;
	}

	public void setUseStartId(boolean useStartId) {
		this.useStartId = useStartId;
	}

	public boolean isRebuildBucketno() {
		return rebuildBucketno;
	}

	public void setRebuildBucketno(boolean rebuildBucketno) {
		this.rebuildBucketno = rebuildBucketno;
	}
}
