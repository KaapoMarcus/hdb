
package com.netease.backend.db.common.ha;


public interface ZooKeeperLeaderChangeHandler {

    
    void leaderChanged(String newPath, byte[] newData);

    
    void gotException(String cause);
}
