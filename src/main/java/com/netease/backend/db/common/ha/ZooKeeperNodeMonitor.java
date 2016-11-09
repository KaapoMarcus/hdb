
package com.netease.backend.db.common.ha;

import org.apache.log4j.Logger;
import org.apache.zookeeper.Watcher;


public abstract class ZooKeeperNodeMonitor implements Watcher {
    private static final Logger LOG;
    static {
        LOG = Logger.getLogger(ZooKeeperNodeMonitor.class);
    }

    private ZooKeeperProvider _zkProvider;
    private final String _nodePath;
    private boolean _watching = true;

    
    public ZooKeeperNodeMonitor(ZooKeeperProvider zkProvider, String nodePath) {
        assert zkProvider != null;
        assert nodePath != null;

        this._zkProvider = zkProvider;
        this._nodePath = nodePath;
    }

    
    public abstract void startWatching();

    
    public abstract void stopWatching();

    
    protected void hibernate() {
        this._zkProvider.addToNotificationPool(this);
        LOG.debug("current monitor on '" + this._nodePath + "' hibernated.");
    }

    
    public String getNodePath() {
        return this._nodePath;
    }

    
    protected boolean isWatching() {
        return this._watching;
    }

    
    protected void setWatching(boolean watching) {
        this._watching = watching;
    }

    
    public ZooKeeperProvider getZKProvider() {
        return this._zkProvider;
    }
}
