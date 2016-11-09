package com.netease.backend.db.common.management;

import java.io.Serializable;
import java.util.Properties;

import com.netease.backend.db.common.config.PropertiesConfig;
import com.netease.backend.db.common.config.PropertiesConfigHelper;
import com.netease.backend.db.common.config.PropertiesConfigHelperBase;


public class PIDConfig implements PropertiesConfig, Serializable, Cloneable {
	private static final long serialVersionUID = -7479533182999429436L;

	
	public static final int TYPE_SIMPLE = 0;
	public static final int TYPE_TIMEBASED = 1;

	
	private static final int ASSIGN_NUM_LIMIT_DEFAULT = 1000;
	
	private static final int ID_TTF_DEFAULT = 1 ;
	
	private static final int CLOCK_MLTD_DEFAULT = 2000 ;
	
	private static final int BASE_TS_SYNC_PERIOD_DEFAULT = 15 * 60 * 1000 ;
	
	private static final int TYPE_DEFAULT = TYPE_SIMPLE;

	
	private int idAssignType = TYPE_DEFAULT;
	
	private int idAssignNumLimit = ASSIGN_NUM_LIMIT_DEFAULT;

	
	private int idTTF = ID_TTF_DEFAULT;
	
	private int clockMLTD = CLOCK_MLTD_DEFAULT;
	
	private int baseTSsyncPeriod = BASE_TS_SYNC_PERIOD_DEFAULT;

	
	
	private boolean ignoreMLTDError = false;
	
	private boolean ignoreSyncError = true;

	public PIDConfig() {
	}

	
	public PIDConfig(int idAssignType, int idAssignNumLimit, int idGenTimeout,
			int idTTF, int clockMLTD, int baseTSsyncPeriod) {
		this.idAssignType = idAssignType;
		this.idAssignNumLimit = idAssignNumLimit;
		this.idTTF = idTTF;
		this.clockMLTD = clockMLTD;
		this.baseTSsyncPeriod = baseTSsyncPeriod;
	}

	
	public PIDConfig(int idAssignType, int idAssignNumLimit, int idGenTimeout,
			int idTTF, int clockMLTD, int baseTSsyncPeriod,
			boolean ignoreMLTDError, boolean ignoreSyncError) {
		this.idAssignType = idAssignType;
		this.idAssignNumLimit = idAssignNumLimit;
		this.idTTF = idTTF;
		this.clockMLTD = clockMLTD;
		this.baseTSsyncPeriod = baseTSsyncPeriod;
		this.ignoreMLTDError = ignoreMLTDError;
		this.ignoreSyncError = ignoreSyncError;
	}

	
	public int getIdAssignType() {
		return idAssignType;
	}

	
	public void setIdAssignType(int idAssignType) {
		this.idAssignType = idAssignType;
	}

	
	public int getIdAssignNumLimit() {
		return idAssignNumLimit;
	}

	
	public void setIdAssignNumLimit(int idAssignNumLimit) {
		this.idAssignNumLimit = idAssignNumLimit;
	}

	
	public int getIdTTF() {
		return idTTF;
	}

	
	public void setIdTTF(int idTTF) {
		this.idTTF = idTTF;
	}

	
	public int getClockMLTD() {
		return clockMLTD;
	}

	
	public void setClockMLTD(int clockMLTD) {
		this.clockMLTD = clockMLTD;
	}

	
	public int getBaseTSSyncPeriod() {
		return baseTSsyncPeriod;
	}

	
	public void setBaseTSSyncPeriod(int syncPeriod) {
		this.baseTSsyncPeriod = syncPeriod;
	}

	
	public boolean isIgnoreMLTDError() {
		return ignoreMLTDError;
	}

	
	public void setIgnoreMLTDError(boolean ignoreMLTDError) {
		this.ignoreMLTDError = ignoreMLTDError;
	}

	
	public boolean isIgnoreSyncError() {
		return ignoreSyncError;
	}

	
	public void setIgnoreSyncError(boolean ignoreSyncError) {
		this.ignoreSyncError = ignoreSyncError;
	}

	public String getDescription() {
		return "PID assigning/generating configurations.";
	}

	public Properties toProperties() {
		return helper.toProperties(this);
	}

	public String toPropertiesString() {
		return helper.toPropertiesString(this);
	}

	public PIDConfig update(Properties props) {
		helper.updateConfig(this, props);
		return this;
	}

	public PIDConfig update(PIDConfig config) {
		helper.updateConfig(this, config);
		return this;
	}

	
	private static final PIDConfigHelper helper;

	static {
		helper = new PIDConfigHelper();
	}

	
	public static PIDConfigHelper getHelper() {
		return helper;
	}

	public static final class PIDConfigHelper extends
			PropertiesConfigHelperBase<PIDConfig> implements
			PropertiesConfigHelper<PIDConfig> {
		public PIDConfig fromProperties(String name, Properties props,
				String desc) {
			final PIDConfig config = new PIDConfig();
			this.updateConfig(config, props);
			return config;
		}

		public Class<PIDConfig> getTypeClass() {
			return PIDConfig.class;
		}
	}
}
