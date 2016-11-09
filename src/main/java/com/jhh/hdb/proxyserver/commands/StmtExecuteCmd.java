package com.jhh.hdb.proxyserver.commands;

import com.jhh.hdb.proxyserver.codec.ComBuffer;

/**
 * 执行一个preparestatement命令
 * 
 statement_id:  statement identifier
 
 flags:         reserved for future use. In MySQL 4.0, always 0.
                In MySQL 5.0: 
                  0: CURSOR_TYPE_NO_CURSOR
                  1: CURSOR_TYPE_READ_ONLY
                  2: CURSOR_TYPE_FOR_UPDATE
                  4: CURSOR_TYPE_SCROLLABLE
 
 iteration_count: reserved for future use. Currently always 1.
 
 null_bit_map:  A bitmap indicating parameters that are NULL.
                Bits are counted from LSB, using as many bytes
                as necessary ((param_count+7)/8)
                i.e. if the first parameter (parameter 0) is NULL, then
                the least significant bit in the first byte will be 1.
 
 new_parameter_bound_flag:   Contains 1 if this is the first time
                             that "execute" has been called, or if
                             the parameters have been rebound.
 
 type:          Occurs once for each parameter; 
                The highest significant bit of this 16-bit value
                encodes the unsigned property. The other 15 bits
                are reserved for the type (only 8 currently used).
                This block is sent when parameters have been rebound
                or when a prepared statement is executed for the 
                first time.

 values:        for all non-NULL values, each parameters appends its value
                as described in Row Data Packet: Binary (column values)

 * 
 *
 */
public class StmtExecuteCmd extends CommandCmd {

	private int statementId;
	private byte flags;
	private int iterationCount;//no used
	private boolean[] nullMap;
	private ComBuffer parametersValueBuffer;

	@Override
	public MySQLCmdType getType() {
		return MySQLCmdType.stmtExecute;
	}

	public int getStatementId() {
		return statementId;
	}

	public void setStatementId(int statementId) {
		this.statementId = statementId;
	}

	public byte getFlags() {
		return flags;
	}

	public void setFlags(byte flags) {
		this.flags = flags;
	}

	public int getIterationCount() {
		return iterationCount;
	}

	public void setIterationCount(int iterationCount) {
		this.iterationCount = iterationCount;
	}

	public boolean[] getNullMap() {
		return nullMap;
	}

	public void setNullMap(boolean[] nullMap) {
		this.nullMap = nullMap;
	}
	
	public ComBuffer getParametersValueBuffer() {
		return parametersValueBuffer;
	}

	public void setParametersValueBuffer(ComBuffer parametersValueBuffer) {
		this.parametersValueBuffer = parametersValueBuffer;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("[StmtExecuteCmd] ");
		sb.append("StatementId:").append(this.statementId);
		sb.append(", Flags:").append(this.flags);
		sb.append(", ParametersValueBuffer:").append(
				this.parametersValueBuffer == null ? "null" : this.parametersValueBuffer);
		return sb.toString();
	}
}
