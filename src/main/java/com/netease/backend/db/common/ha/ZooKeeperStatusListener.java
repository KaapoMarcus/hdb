
package com.netease.backend.db.common.ha;


public interface ZooKeeperStatusListener {

    public void zkAvailable(ZooKeeperProvider zkProvider);

    public void zkUnavailable(ZooKeeperProvider zkProvider);
}
