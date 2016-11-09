
package com.netease.backend.db.common.schema;


public class ConnectingHost {

    public static String TYPE_APP = "APP";
    public static String TYPE_QS = "QS";
    public static String TYPE_DBA = "DBA";

    private String ip;
    private String type;

    public ConnectingHost(String ip, String type) {
        this.ip = ip;
        this.type = type;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ConnectingHost)) { return false; }
        if (this == obj) { return true; }
        final ConnectingHost host = (ConnectingHost) obj;
        return this.getType().equals(host.getType())
                && this.getIp().equals(host.getIp());
    }

    @Override
    public int hashCode() {
        final String base = this.getType() + this.getIp();
        return base.hashCode();
    }

    
    public String getIp() {
        return this.ip;
    }

    
    public String getType() {
        return this.type;
    }
}
