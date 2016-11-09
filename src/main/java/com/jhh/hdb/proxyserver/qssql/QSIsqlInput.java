package com.jhh.hdb.proxyserver.qssql;

import java.io.IOException;

import com.netease.exec.InputDevice;

/**
 * @author shiyong
 * copy from DbaIsqlInput
 *
 */
class QSIsqlInput implements InputDevice {

    /* (non-Javadoc)
     * @see com.netease.exec.InputDevice#askForChoice(java.lang.String, java.lang.Integer, java.lang.String[], java.lang.Object[])
     */
    public Object askForChoice(String question, Integer defaultValue, String[] choice, 
            Object[] values) throws IOException {
        return values[defaultValue];
    }

    /* (non-Javadoc)
     * @see com.netease.exec.InputDevice#askForOneInteger(java.lang.String, java.lang.Integer)
     */
    public int askForOneInteger(String question, Integer defaultValue) throws IOException {
        return defaultValue;
    }

    /* (non-Javadoc)
     * @see com.netease.exec.InputDevice#askForOneString(java.lang.String, java.lang.String)
     */
    public String askForOneString(String question, String defaultValue) throws IOException {
        return defaultValue;
    }

    /* (non-Javadoc)
     * @see com.netease.exec.InputDevice#askForYesNo(java.lang.String, boolean)
     */
    public boolean askForYesNo(String question, boolean defaultValue) throws IOException {
        return true;
    }

}
