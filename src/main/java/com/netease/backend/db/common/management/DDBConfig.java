package com.netease.backend.db.common.management;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.netease.backend.db.common.definition.DbnType;
import com.netease.backend.db.common.exceptions.CryptException;
import com.netease.backend.db.common.utils.CryptUtils;



public class DDBConfig implements Serializable, Cloneable {

	private static final int DEFAULT_DBA_PORT = 7777;

	public static final int DEFAULT_DBI_PORT = 8888;

	private static final long serialVersionUID = 6445181039874071967L;

	
	public static final int DB_CONNECT_TIMEOUT = 10000;
	
	public static final int DB_SOCKET_TIMEOUT = 20000;
	
	
	private String				name;
	
	private String				host				= "";
	
	private int					port				= DEFAULT_DBI_PORT;
	
	private int					dbaPort				= DEFAULT_DBA_PORT;

	
	public String getName() {
		return name;
	}

	
	public void setName(
		String name)
	{
		this.name = name;
	}

	
	public String getHost() {
		return host;
	}

	
	public void setHost(
		String host)
	{
		this.host = host;
	}

	
	public int getPort() {
		return port;
	}

	
	public void setPort(
		int port)
	{
		this.port = port;
	}

	
	public int getDbaPort() {
		return dbaPort;
	}

	
	public void setDbaPort(
		int dbaPort)
	{
		this.dbaPort = dbaPort;
	}

	
	transient private String sysDBURL = "";

	
	transient private String sysDBUser = "";

	
	transient private String sysDBPass = "";

	
	private String sshIdFile = "";

	
	private String pidPath = "master.pid";

	
	private String zkAddr = "";

	

	
	private boolean checkHB = false;

	
	private boolean checkDbn = true;

	
	private boolean checkDbnProcess = true;

	
	private boolean checkSuspendXA = true;

	
	private boolean checkSysDB = true;

	
	private long checkDbnInterval = 60;

	
	private int dbnFailTimes = 2;

	
	private long checkDbnProcessInterval = 90;

	
	private int dbnQueryThreshold = 120;

	
	private List<String> dbnQueryCond = new ArrayList<String>();

	
	private int dbnConnThreshold = 700;

	
	private int dbnConnSameTimeLimit = 30;

	
	private int dbnConnClientPoolLimit = 5;

	
	private int dbnSimilarSqlThreshold = 25;

	
	private List<String> disabledProcessHosts = new ArrayList<String>();

	
	private List<DBNQueryDisabledCondition> disabledProcessSqls = new ArrayList<DBNQueryDisabledCondition>();

	
	private long dbnHBInterval = Long.MAX_VALUE;

	
	private long dbnReportInterval = 300000;

	
	private long deadCheckInterval = Long.MAX_VALUE;

	
	private long deadAssureInterval = Long.MAX_VALUE;

	
	private Date dbnRebootBegin = null;

	
	private long dbnRebootDuration = 600;

	
	private DbnType defaultDataBaseType = DbnType.MySQL;

	

	
	private long xabCheckInterval = 30000;

	
	private long xabTimeout = 60000;

	
	private int xabRetryTimes = 3;

	
	private int xabCommitInterval = 2000;

	
	private boolean alarmSwitch = true;

	
	private int socketTimeout = 10000;

	
	private int connectTimeout = 10000;

	
	private int migUnit = 1000;

	
	private String[] mobileList = null;

	
	private String smsUrl = "";

	
	private String[] toList = null;

	
	private String[] ccList = null;;

	
	private String smtpServer = "";

	
	private String emailAddress = "";

	
	private String emailPass = "";

	
	private String dumpPath = "";

	
	private int dropPartLimit = 3;

	
	private boolean useZookeeper = false;

	
	private int alarmInterval = 60;

	
	private long wLockTimeout = 60000;

	
	private long checkRepInterval = 10;

	
	private int repFailTimes = 2;

	
	private int repWarningDelay = 8;

	
	private String execDir = "blog-db";

	
	private String tempDir = "";
	
	
	private boolean monitorQs = true;
	
	
	private int checkQsInterval = 60;

	
	private String ibbackupDir = "";

	
	private String innobackupDir = "";

	
	private String logfile = "";

	
	private String mirrorDir = "";
	
	
	private int waitTimeForRepAutoSwitch = 15;

	
	transient private String superDbnUser = "";

	
	transient private String superDbnPass = "";

	
	transient private String repDbnUser = "";

	
	transient private String repDbnPass = "";

	
	private byte[] superDbnUserBytes = null;

	
	private byte[] superDbnPassBytes = null;

	
	private byte[] repDbnUserBytes = null;

	
	private byte[] repDbnPassBytes = null;

	
	private PIDConfig						PIDConfig				= new PIDConfig();

	
	public PIDConfig getPIDConfig() {
		return PIDConfig;
	}

	
	public void setPIDConfig(
		PIDConfig config)
	{
		PIDConfig = config;
	}

	
	private long QUERY_QUOTA = 100000;

	public DDBConfig() {
		dbnQueryCond.add("120:0");
	}

	
	@Override
	public Object clone() {
		try {
			final DDBConfig cloned = (DDBConfig) super.clone();
			return cloned;
		} catch (final CloneNotSupportedException e) {
			return null;
		}
	}

	
	synchronized public int getXabCommitInterval() {
		return xabCommitInterval;
	}

	
	synchronized public long getDeadCheckInterval() {
		return deadCheckInterval;
	}

	
	synchronized public void setDeadCheckInterval(long deadCheckInterval) {
		if (deadCheckInterval >= 200) {
			this.deadCheckInterval = deadCheckInterval;
		} else {
			this.deadCheckInterval = 200;
		}
	}

	
	synchronized public long getDeadAssureInterval() {
		return deadAssureInterval;
	}

	
	public void setDeadAssureInterval(long deadAssureInterval) {
		if (deadAssureInterval >= 5000) {
			this.deadAssureInterval = deadAssureInterval;
		} else {
			this.deadAssureInterval = 5000;
		}
	}

	
	public void setXabCommitInterval(int xabInterval) {
		if (xabCommitInterval >= 1000) {
			this.xabCommitInterval = xabInterval;
		} else {
			this.xabCommitInterval = 1000;
		}
	}

	
	synchronized public int getMigUnit() {
		return migUnit;
	}

	
	public void setMigUnit(int migUnit) {
		if (migUnit < 1) {
			this.migUnit = 1;
		} else {
			this.migUnit = migUnit;
		}
	}

	
	public int getSocketTimeout() {
		return socketTimeout;
	}

	
	public void setSocketTimeout(int socketTimeout) {
		if (socketTimeout < 1000) {
			this.socketTimeout = 1000;
		} else {
			this.socketTimeout = socketTimeout;
		}
	}

	
	public long getDbnHBInterval() {
		return dbnHBInterval;
	}

	
	public void setDbnHBInterval(long dbnHBInterval) {
		if (dbnHBInterval >= 2000) {
			this.dbnHBInterval = dbnHBInterval;
		} else {
			this.dbnHBInterval = 2000;
		}
	}

	
	public long getDbnReportInterval() {
		return dbnReportInterval;
	}

	
	public void setDbnReportInterval(long reportInterval) {
		if (reportInterval >= 5000) {
			this.dbnReportInterval = reportInterval;
		} else {
			this.dbnHBInterval = 5000;
		}
	}

	
	synchronized public long getXabTimeout() {
		return xabTimeout;
	}

	
	public void setXabTimeout(long xabDeadTimeout) {
		if (xabDeadTimeout >= 10000) {
			this.xabTimeout = xabDeadTimeout;
		} else {
			this.xabTimeout = 10000;
		}
	}

	synchronized public String[] getCCList() {
		return ccList;
	}

	public void setCCList(String ccString) {
		if ((ccString.trim() != null) && !ccString.trim().equals("")) {
			this.ccList = ccString.split(";");
		} else {
			this.ccList = null;
		}
	}

	public void setCCList(String[] cc) {
		this.ccList = cc;
	}

	synchronized public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	synchronized public String getEmailPass() {
		return emailPass;
	}

	public void setEmailPass(String emailPass) {
		this.emailPass = emailPass;
	}

	synchronized public String[] getMobileList() {
		return mobileList;
	}

	public void setMobileList(String mobleString) {
		if ((mobleString.trim() != null) && !mobleString.trim().equals("")) {
			this.mobileList = mobleString.split(";");
		} else {
			this.mobileList = null;
		}
	}

	public void setMobileList(String[] mobiles) {
		this.mobileList = mobiles;
	}

	synchronized public String getSmtpServer() {
		return smtpServer;
	}

	public void setSmtpServer(String smtpServer) {
		this.smtpServer = smtpServer;
	}

	synchronized public String[] getToList() {
		return toList;
	}

	public void setToList(String toString) {
		if ((toString.trim() != null) && !toString.trim().equals("")) {
			this.toList = toString.split(";");
		} else {
			this.toList = null;
		}
	}

	public void setToList(String[] to) {
		this.toList = to;
	}

	synchronized public String getSmsUrl() {
		return smsUrl;
	}

	public void setSmsUrl(String smsUrl) {
		this.smsUrl = smsUrl;
	}

	synchronized public boolean isCheckHB() {
		return checkHB;
	}

	public void setCheckHB(boolean checkHB) {
		this.checkHB = checkHB;
	}

	synchronized public boolean isCheckSuspendXA() {
		return checkSuspendXA;
	}

	public void setCheckSuspendXA(boolean checkSuspendXA) {
		this.checkSuspendXA = checkSuspendXA;
	}

	synchronized public boolean isCheckSysDB() {
		return checkSysDB;
	}

	public void setCheckSysDB(boolean checkSysDB) {
		this.checkSysDB = checkSysDB;
	}

	synchronized public String getDumpPath() {
		return dumpPath;
	}

	public void setDumpPath(String path) {
		this.dumpPath = path;
	}

	
	synchronized public void updateConfig(DDBConfig conf) {
		this.name = conf.getName();
		this.host = conf.getHost();
		this.port = conf.getPort();
		this.dbaPort = conf.getDbaPort();
		this.sshIdFile = conf.getSshIdFile();
		this.checkDbnInterval = conf.getCheckDbnInterval();
		this.defaultDataBaseType = conf.getDefaultDataBaseType();
		this.checkDbnProcessInterval = conf.getCheckDbnProcessInterval();
		this.dbnQueryCond = conf.getDbnQueryConditions();
		this.dbnSimilarSqlThreshold = conf.getDbnSimilarSqlThreshold();
		this.dbnConnThreshold = conf.getDbnConnThreshold();
		this.dbnConnSameTimeLimit = conf.getDbnConnSameTimeLimit();
		this.dbnConnClientPoolLimit = conf.getDbnConnClientPoolLimit();
		this.disabledProcessSqls = conf.getDisabledProcessSqls();
		this.dbnFailTimes = conf.getDbnFailTimes();
		this.dbnHBInterval = conf.getDbnHBInterval();
		this.dbnReportInterval = conf.getDbnReportInterval();
		this.deadCheckInterval = conf.getDeadCheckInterval();
		this.deadAssureInterval = conf.getDeadAssureInterval();
		this.dbnRebootBegin = conf.getDbnRebootBegin();
		this.dbnRebootDuration = conf.getDbnRebootDuration();
		this.xabCheckInterval = conf.getXabCheckInterval();
		this.xabTimeout = conf.getXabTimeout();
		this.socketTimeout = conf.getSocketTimeout();
		this.connectTimeout = conf.getConnectTimeout();
		
		this.migUnit = conf.getMigUnit();
		this.mobileList = conf.getMobileList();
		this.smsUrl = conf.getSmsUrl();
		this.toList = conf.getToList();
		this.ccList = conf.getCCList();
		this.smtpServer = conf.getSmtpServer();
		this.emailAddress = conf.getEmailAddress();
		this.emailPass = conf.getEmailPass();
		this.dumpPath = conf.getDumpPath();
		this.alarmSwitch = conf.isAlarmSwitch();
		this.dropPartLimit = conf.getDropPartLimit();
		this.alarmInterval = conf.getAlarmInterval();
		this.wLockTimeout = conf.getWLockTimeout();
		this.checkRepInterval = conf.getCheckRepInterval();
		this.repFailTimes = conf.getRepFailTimes();
		this.repWarningDelay = conf.getRepWarningDelay();
		this.execDir = conf.getExecDir();
		this.monitorQs = conf.isMonitorQs();
		this.checkQsInterval = conf.getCheckQsInterval();
		this.tempDir = conf.getTempDir();
		this.ibbackupDir = conf.getIbbackupDir();
		this.innobackupDir = conf.getInnobackupDir();
		this.logfile = conf.getLogfile();
		this.mirrorDir = conf.getMirrorDir();
		this.superDbnUser = conf.getSuperDbnUser();
		this.superDbnPass = conf.getSuperDbnPass();
		this.repDbnUser = conf.getRepDbnUser();
		this.repDbnPass = conf.getRepDbnPass();
		this.useZookeeper = conf.isUseZookeeper();
		this.zkAddr = conf.getZkAddr();
	}

	synchronized public boolean isAlarmSwitch() {
		return alarmSwitch;
	}

	public void setAlarmSwitch(boolean alarmSwitch) {
		this.alarmSwitch = alarmSwitch;
	}

	synchronized public long getXabCheckInterval() {
		return xabCheckInterval;
	}

	
	public void setXabCheckInterval(long xabCheckInterval) {
		if (xabCheckInterval >= 5000) {
			this.xabCheckInterval = xabCheckInterval;
		} else {
			this.xabCheckInterval = 5000;
		}
	}

	synchronized public int getXabRetryTimes() {
		return xabRetryTimes;
	}

	
	public void setXabRetryTimes(int xabRetryTimes) {
		if (xabRetryTimes < 0) {
			this.xabRetryTimes = 0;
		} else if (xabRetryTimes > 10) {
			this.xabRetryTimes = 10;
		} else {
			this.xabRetryTimes = xabRetryTimes;
		}

	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		if (connectTimeout >= 0) {
			this.connectTimeout = connectTimeout;
		}
	}

	public int getDropPartLimit() {
		return dropPartLimit;
	}

	
	public void setDropPartLimit(int dropPartLimit) {
		if (dropPartLimit > 3) {
			this.dropPartLimit = dropPartLimit;
		}
	}

	public String getPidPath() {
		return pidPath;
	}

	public void setPidPath(String pidPath) {
		if ((pidPath != null) && !pidPath.equals("")) {
			this.pidPath = pidPath.trim();
		}
	}

	
    public String getZkAddr() {
        return this.zkAddr;
    }

    
    public void setZkAddr(String zkAddr) {
        this.zkAddr = zkAddr;
    }

	public String getSysDBPass() {
		return sysDBPass;
	}

	public void setSysDBPass(String sysDBPass) {
		if (sysDBPass != null) {
			this.sysDBPass = sysDBPass;
		}
	}

	public String getSysDBURL() {
		return sysDBURL;
	}

	public void setSysDBURL(String sysDBURL) {
		if ((sysDBURL != null) && !sysDBURL.trim().equals("")) {
			this.sysDBURL = sysDBURL;
		}
	}

	public String getSysDBUser() {
		return sysDBUser;
	}

	public void setSysDBUser(String sysDBUser) {
		if (sysDBUser != null) {
			this.sysDBUser = sysDBUser;
		}
	}

	public int getAlarmInterval() {
		return alarmInterval;
	}

	
	public void setAlarmInterval(int alarmInterval) {
		if (alarmInterval < 10) {
			this.alarmInterval = 10;
		} else {
			this.alarmInterval = alarmInterval;
		}
	}

	public long getWLockTimeout() {
		return wLockTimeout;
	}

	public void setWLockTimeout(long lockTimeout) {
		wLockTimeout = lockTimeout;
	}

	public long getQueryQuota() {
		return this.QUERY_QUOTA;
	}

	public void setQueryQuota(long quota) {
		this.QUERY_QUOTA = quota;
	}

	public boolean isCheckDbn() {
		return checkDbn;
	}

	public boolean isCheckDbnProcesslist() {
		return this.checkDbnProcess;
	}

	public void setCheckDbn(boolean checkDbn) {
		this.checkDbn = checkDbn;
	}

	public void setCheckDbnProcesslist(boolean check) {
		this.checkDbnProcess = check;
	}

	public long getCheckDbnInterval() {
		return checkDbnInterval;
	}

	public void setCheckDbnInterval(long checkDbnInterval) {
		this.checkDbnInterval = checkDbnInterval;
	}

	public DbnType getDefaultDataBaseType() {
		return defaultDataBaseType;
	}

	public void setDefaultDataBaseType(DbnType DBType) {
		this.defaultDataBaseType = DBType;
	}

	public long getCheckDbnProcessInterval() {
		return checkDbnProcessInterval;
	}

	public void setCheckDbnProcessInterval(long checkDbnProcessInterval) {
		this.checkDbnProcessInterval = checkDbnProcessInterval;
	}

	public List<String> getDbnQueryConditions() {
		return this.dbnQueryCond;
	}

	public void setDbnQueryConditions(List<String> conds) {
		this.dbnQueryCond = conds;
	}

	public int getDbnQueryThreshold() {
		return dbnQueryThreshold;
	}

	public void setDbnQueryThreshold(int dbnQueryThreshold) {
		this.dbnQueryThreshold = dbnQueryThreshold;
	}

	public int getDbnConnThreshold() {
		return dbnConnThreshold;
	}

	public void setDbnConnThreshold(int dbnConnThreshold) {
		this.dbnConnThreshold = dbnConnThreshold;
	}

	
	public int getDbnConnSameTimeLimit() {
		return dbnConnSameTimeLimit;
	}

	
	public void setDbnConnSameTimeLimit(int dbnConnSameTimeLimit) {
		this.dbnConnSameTimeLimit = dbnConnSameTimeLimit;
	}

	
	public int getDbnConnClientPoolLimit() {
		return dbnConnClientPoolLimit;
	}

	
	public void setDbnConnClientPoolLimit(int dbnConnClientPoolLimit) {
		this.dbnConnClientPoolLimit = dbnConnClientPoolLimit;
	}

	public int getDbnSimilarSqlThreshold() {
		return this.dbnSimilarSqlThreshold;
	}

	public void setDbnSimilarSqlThreshold(int dbnSimilarSqlThreshold) {
		this.dbnSimilarSqlThreshold = dbnSimilarSqlThreshold;
	}

	public List<String> getDisabledProcessHosts() {
		return disabledProcessHosts;
	}

	public void setDisabledProcessHosts(List<String> disabledProcessHosts) {
		this.disabledProcessHosts = disabledProcessHosts;
	}

	public List<DBNQueryDisabledCondition> getDisabledProcessSqls() {
		return disabledProcessSqls;
	}

	public void setDisabledProcessSqls(List<DBNQueryDisabledCondition> disabledProcessSqls) {
		this.disabledProcessSqls = disabledProcessSqls;
	}


	public int getDbnFailTimes() {
		return dbnFailTimes;
	}

	public void setDbnFailTimes(int dbnFailTimes) {
		this.dbnFailTimes = dbnFailTimes;
	}

	public long getCheckRepInterval() {
		return checkRepInterval;
	}

	public void setCheckRepInterval(long checkRepInterval) {
		this.checkRepInterval = checkRepInterval;
	}

	public int getRepFailTimes() {
		return repFailTimes;
	}

	public void setRepFailTimes(int repFailTimes) {
		this.repFailTimes = repFailTimes;
	}

	public Date getDbnRebootBegin() {
		return dbnRebootBegin;
	}

	public void setDbnRebootBegin(Date dbnRebootBegin) {
		this.dbnRebootBegin = dbnRebootBegin;
	}

	public long getDbnRebootDuration() {
		return dbnRebootDuration;
	}

	public void setDbnRebootDuration(long dbnRebootDuration) {
		this.dbnRebootDuration = dbnRebootDuration;
	}

	public boolean isDbnRebooting() {
		if (this.dbnRebootBegin == null)
			return false;
		if (this.dbnRebootDuration < 0)
			return false;
		final GregorianCalendar now = new GregorianCalendar();
		final GregorianCalendar rebootStart = new GregorianCalendar();
		rebootStart.setTime(dbnRebootBegin);
		rebootStart.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now
				.get(Calendar.DATE));
		if (rebootStart.after(now)) {
			rebootStart.add(Calendar.DATE, -1);
		}
		if (now.getTimeInMillis() <= (rebootStart.getTimeInMillis() + dbnRebootDuration * 1000))
			return true;
		return false;
	}

	public static void main(String[] args) {
		final DDBConfig config = new DDBConfig();
		final Date now = new Date();
		config
		.setDbnRebootBegin(new Date(now.getTime() - (long) 24 * 3600 * 1000 * 40 + 10 * 60
				* 1000));
		config.setDbnRebootDuration(30 * 60);
		System.out.println(new SimpleDateFormat().format(now) + " in "
				+ new SimpleDateFormat().format(config.getDbnRebootBegin()) + " during "
				+ config.getDbnRebootDuration() + " is " + config.isDbnRebooting());
	}

	public String getSshIdFile() {
		return sshIdFile;
	}

	public void setSshIdFile(String sshIdFile) {
		if (sshIdFile != null) {
			this.sshIdFile = sshIdFile.trim();
		}
	}

	public int getRepWarningDelay() {
		return repWarningDelay;
	}

	public void setRepWarningDelay(int repWarningDelay) {
		this.repWarningDelay = repWarningDelay;
	}

	public String getExecDir() {
		return execDir;
	}

	public void setExecDir(String execDir) {
		this.execDir = execDir;
	}
	
	public boolean isMonitorQs() {
		return monitorQs;
	}

	public void setMonitorQs(boolean monitorQs) {
		this.monitorQs = monitorQs;
	}

	public int getCheckQsInterval() {
		return checkQsInterval;
	}

	public void setCheckQsInterval(int checkQSInterval) {
		this.checkQsInterval = checkQSInterval;
	}

	public String getTempDir() {
		return tempDir;
	}

	public void setTempDir(String tempDir) {
		this.tempDir = tempDir;
	}

	public String getIbbackupDir() {
		return ibbackupDir;
	}

	public void setIbbackupDir(String ibbackupDir) {
		this.ibbackupDir = ibbackupDir;
	}

	public String getInnobackupDir() {
		return innobackupDir;
	}

	public void setInnobackupDir(String innobackupDir) {
		this.innobackupDir = innobackupDir;
	}

	public String getLogfile() {
		return logfile;
	}

	public void setLogfile(String logfile) {
		this.logfile = logfile;
	}

	public String getMirrorDir() {
		return mirrorDir;
	}

	public void setMirrorDir(String mirrorDir) {
		this.mirrorDir = mirrorDir;
	}
	
	public int getWaitTimeForRepAutoSwitch() {
		return waitTimeForRepAutoSwitch;
	}

	public void setWaitTimeForRepAutoSwitch(int waitTimeForRepAutoSwitch) {
		this.waitTimeForRepAutoSwitch = waitTimeForRepAutoSwitch;
	}

	public String getSuperDbnUser() {
		return superDbnUser;
	}

	public void setSuperDbnUser(String superDbnUser) {
		this.superDbnUser = superDbnUser;
	}

	public String getSuperDbnPass() {
		return superDbnPass;
	}

	public void setSuperDbnPass(String superDbnPass) {
		this.superDbnPass = superDbnPass;
	}

	public String getRepDbnUser() {
		return repDbnUser;
	}

	public void setRepDbnUser(String repDbnUser) {
		this.repDbnUser = repDbnUser;
	}

	public String getRepDbnPass() {
		return repDbnPass;
	}

	public void setRepDbnPass(String repDbnPass) {
		this.repDbnPass = repDbnPass;
	}

	public boolean isUseZookeeper() {
		return useZookeeper;
	}

	public void setUseZookeeper(boolean useZookeeper) {
		this.useZookeeper = useZookeeper;
	}

	
	public void encrypt() throws CryptException {
		superDbnUserBytes = CryptUtils.encrypt(superDbnUser.getBytes());
		superDbnPassBytes = CryptUtils.encrypt(superDbnPass.getBytes());
		superDbnUser = "";
		superDbnPass = "";
		repDbnUserBytes = CryptUtils.encrypt(repDbnUser.getBytes());
		repDbnPassBytes = CryptUtils.encrypt(repDbnPass.getBytes());
		repDbnUser = "";
		repDbnPass = "";
	}

	
	public void decrypt() throws CryptException {
		superDbnUser = new String(CryptUtils.decrypt(superDbnUserBytes));
		superDbnPass = new String(CryptUtils.decrypt(superDbnPassBytes));
		repDbnUser = new String(CryptUtils.decrypt(repDbnUserBytes));
		repDbnPass = new String(CryptUtils.decrypt(repDbnPassBytes));
		superDbnUserBytes = null;
		superDbnPassBytes = null;
		repDbnUserBytes = null;
		repDbnPassBytes = null;
	}
}
