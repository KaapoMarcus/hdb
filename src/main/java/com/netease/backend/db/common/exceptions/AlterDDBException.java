package com.netease.backend.db.common.exceptions;

import com.netease.backend.db.common.cloud.CloudCommonUtils;



public class AlterDDBException extends MSException 
{

	private static final long serialVersionUID = -4009097400909158078L;

	
	private boolean isDBNAltered = false;
	
	
	private boolean isClientNotified = false;
	
	public AlterDDBException()
	{
	}
	
	public AlterDDBException(String desc)
	{
		super(desc);
	}
	
	
	public AlterDDBException(String cloudErrorCode, String msg) {
		super(CloudCommonUtils.generateErrorMessage(cloudErrorCode, msg));
	}
	
	
	public AlterDDBException(String desc, boolean isDBAltered, boolean isClientNotified)
	{
		super(desc);
		this.isDBNAltered = isDBAltered;
		this.isClientNotified = isClientNotified;
	}

	public boolean isClientNotified() {
		return isClientNotified;
	}

	public boolean isDBNAltered() {
		return isDBNAltered;
	}

}
