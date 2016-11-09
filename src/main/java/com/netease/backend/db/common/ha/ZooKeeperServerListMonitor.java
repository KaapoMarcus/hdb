
package com.netease.backend.db.common.ha;

import java.util.Arrays;

import org.apache.log4j.Logger;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.AsyncCallback.DataCallback;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.data.Stat;


public class ZooKeeperServerListMonitor extends ZooKeeperNodeMonitor {

    private static final Logger LOG;
    static {
        LOG = Logger.getLogger(ZooKeeperNodeMonitor.class);
    }

    private ZooKeeperServerListHandler _zkslHandler = new ZooKeeperServerListHandler();
    private ZooKeeperServerListListener _serverListListener;

    
    public ZooKeeperServerListMonitor(ZooKeeperProvider zkProvider,
            String nodePath, ZooKeeperServerListListener serverListListener) {
        super(zkProvider, nodePath);

        this._serverListListener = serverListListener;
    }

    
    @Override
    public void startWatching() {
        setWatching(true);

        
        getZKProvider().getZooKeeper().getData(getNodePath(), this,
                _zkslHandler, null);
    }

    
    @Override
    public void stopWatching() {
        setWatching(false);
    }

    
    public void process(WatchedEvent event) {
        assert event != null;

        LOG.trace("ZooKeeper event received:");
        LOG.trace("event state: " + event.getState());
        LOG.trace("event type: " + event.getType());
        LOG.trace("event path: " + event.getPath());

        String eventPath = event.getPath();
        switch (event.getType()) {
        case None:
            
            return;
        case NodeDeleted:
            
            LOG.warn("ZooKeeper's serverlist node was deleted! Client "
                    + "will no longer acknowledge any subsequent "
                    + "serverlist updates.");
            
            break;
        case NodeDataChanged:
        default:
            
            LOG.debug("New ZooKeeper Serverlist event received.");
            getZKProvider().getZooKeeper().getData(eventPath, this,
                    _zkslHandler, null);
            break;
        }
    }

    private class ZooKeeperServerListHandler implements DataCallback {

        private byte[] _prevData;

        
        public void processResult(int rc, String path, Object ctx, byte[] data,
                Stat stat) {
            Code code = Code.get(rc);

            LOG.trace("ZK data callback, Code:[" + code + "], path:'" + path
                    + "', ctx:'" + ctx + "'");

            if (!getZKProvider().isConnected()) {
                LOG.debug("Node: '" + path + "' not exist!");
                LOG.warn("Fetching server list failed, ZooKeeper server list "
                        + "not synced!");
                return;
            }

            switch (code) {
            case OK:
                if ((data == null && data != _prevData)
                        || (data != null && !Arrays.equals(_prevData, data))) {
                    _prevData = data;
                }
                
                if (_serverListListener != null) {
                    String newServerList = new String(_prevData);
                    LOG.debug("New ZooKeeper server list received: '"
                            + newServerList + "'");
                    _serverListListener.serverListChanged(newServerList);
                }
                break;
            case NONODE:
                LOG.warn("Serverlist node '" + path + "' unavailable!");
                
                break;
            case SESSIONEXPIRED:
            case NOAUTH:
                
                stopWatching();
                break;
            default:
                
                getZKProvider().getZooKeeper().getData(path,
                        ZooKeeperServerListMonitor.this, this, null);
                break;
            }
        }
    }
}
