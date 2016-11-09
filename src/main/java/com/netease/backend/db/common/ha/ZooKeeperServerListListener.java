
package com.netease.backend.db.common.ha;


public interface ZooKeeperServerListListener {

    void serverListChanged(String connectingStr);
}
