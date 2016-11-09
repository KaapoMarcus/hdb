package com.netease.backend.db.common.utils;

import javax.transaction.xa.Xid;

public class XidUtils {
    
    public static long getASId(Xid xid) {
    	byte [] gtrid = xid.getGlobalTransactionId();
    	if (gtrid == null)
    		return 0;
    	
    	int len = 0;
    	for (len = 0; len < gtrid.length; len++) {
    		if (gtrid[len] == '_') 
    			break;
    	}
    	if (len == 0 || len == gtrid.length)
    		return 0;
    	
    	byte [] asId = new byte [len];
    	System.arraycopy(gtrid, 0, asId, 0, asId.length);
    	
    	return Long.parseLong(new String(asId));
    }

}
