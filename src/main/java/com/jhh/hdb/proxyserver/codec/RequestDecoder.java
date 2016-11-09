package com.jhh.hdb.proxyserver.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import com.jhh.hdb.proxyserver.codec.packets.mysql.AuthenticationPacket;
import com.jhh.hdb.proxyserver.codec.packets.mysql.InitDBPacket;
import com.jhh.hdb.proxyserver.codec.packets.mysql.MySQLPacket;
import com.jhh.hdb.proxyserver.codec.packets.mysql.PingPacket;
import com.jhh.hdb.proxyserver.codec.packets.mysql.QueryPacket;
import com.jhh.hdb.proxyserver.codec.packets.mysql.QuitPacket;
import com.jhh.hdb.proxyserver.codec.packets.mysql.StmtClosePacket;
import com.jhh.hdb.proxyserver.codec.packets.mysql.StmtExecutePacket;
import com.jhh.hdb.proxyserver.codec.packets.mysql.StmtFetchPacket;
import com.jhh.hdb.proxyserver.codec.packets.mysql.StmtPreparePacket;
import com.jhh.hdb.proxyserver.codec.packets.mysql.StmtResetPacket;
import com.jhh.hdb.proxyserver.codec.packets.mysql.StmtSendLongDataPacket;
import com.jhh.hdb.proxyserver.commands.MySQLCmd;
import com.jhh.hdb.proxyserver.define.MySQLCommandNumber;
import com.jhh.hdb.proxyserver.session.SessionContext;
import com.jhh.hdb.proxyserver.session.SessionStatusType;

/**
 * 解码器，把客户端发过来的请求解码成MySQLCmd的形式
 * 
 *
 */
public class RequestDecoder extends CumulativeProtocolDecoder {
	
	//提供解码操作的MySQLPacket子类列表
//	private MySQLPacket changeUserPacket;
	private MySQLPacket initDBPacket;
	private MySQLPacket pingPacket;
	private MySQLPacket queryPacket;
	private MySQLPacket quitPacket;
	private MySQLPacket stmtClosePacket;
	private MySQLPacket stmtExecutePacket;
	private MySQLPacket stmtFetchPacket;
	private MySQLPacket stmtPreparePacket;
	private MySQLPacket stmtResetPacket;
	private MySQLPacket stmtSendLongDataPacket;
	
	//认证包
	private AuthenticationPacket authPacket;
	
	RequestDecoder() {
		BufferFactory factory = new IOBufferFactory();
//		changeUserPacket = new ChangeUserPacket(factory);
		initDBPacket = new InitDBPacket(factory);
		pingPacket = new PingPacket(factory);
		queryPacket = new QueryPacket(factory);
		quitPacket = new QuitPacket(factory);
		stmtClosePacket = new StmtClosePacket(factory);
		stmtExecutePacket = new StmtExecutePacket(factory);
		stmtFetchPacket = new StmtFetchPacket(factory);
		stmtPreparePacket = new StmtPreparePacket(factory);
		stmtResetPacket = new StmtResetPacket(factory);
		stmtSendLongDataPacket = new StmtSendLongDataPacket(factory);
		
		authPacket = new AuthenticationPacket(factory);
	}

	/**
	 * 做解包操作，由mina框架负责调用
	 */
	protected boolean doDecode(IoSession session, IoBuffer in,
			ProtocolDecoderOutput out) throws Exception {
		if (!MySQLPacket.checkPacketLength(in)) {
			return false;
		}

		SessionContext context = SessionContext.getSessionContext(session);
		SessionStatusType sessionStatus = context.getSessionStatus();

		switch (sessionStatus) {
		case connected: {
			MySQLCmd cmd = authPacket.decode(new MinaComBuffer(in), context);
			out.write(cmd);
			break;
		}
		case authenticated: {
			decodeCommand(in, out, context);
			break;
		}
		default:
			throw new Exception("Illegal sessionStatus: " + sessionStatus);
		}
		
		if (in.remaining() > 0)
			return true;//粘包
		else
			return false;//刚好
	}
	
	private void decodeCommand(IoBuffer in, ProtocolDecoderOutput out,
			SessionContext context) throws Exception {
		//命令类型编号在第5字节处
		byte commandType = in.get(in.position() + 4);
		
		MySQLPacket cmdDecoder = null;
		/*if (commandType == MySQLCommandNumber.COM_CHANGE_USER) {
			cmdDecoder = changeUserPacket;
		} else */if (commandType == MySQLCommandNumber.COM_INIT_DB) {
			cmdDecoder = initDBPacket;
		} else if (commandType == MySQLCommandNumber.COM_PING) {
			cmdDecoder = pingPacket;
		} else if (commandType == MySQLCommandNumber.COM_QUERY) {
			cmdDecoder = queryPacket;
		} else if (commandType == MySQLCommandNumber.COM_QUIT) {
			cmdDecoder = quitPacket;
		} else if (commandType == MySQLCommandNumber.COM_STMT_CLOSE) {
			cmdDecoder = stmtClosePacket;
		} else if (commandType == MySQLCommandNumber.COM_STMT_EXECUTE) {
			cmdDecoder = stmtExecutePacket;
		} else if (commandType == MySQLCommandNumber.COM_STMT_FETCH) {
			cmdDecoder = stmtFetchPacket;
		} else if (commandType == MySQLCommandNumber.COM_STMT_PREPARE) {
			cmdDecoder = stmtPreparePacket;
		} else if (commandType == MySQLCommandNumber.COM_STMT_RESET) {
			cmdDecoder = stmtResetPacket;
		} else if (commandType == MySQLCommandNumber.COM_STMT_SEND_LONG_DATA) {
			cmdDecoder = stmtSendLongDataPacket;
		} else if (commandType == MySQLCommandNumber.COM_FIELD_LIST) {
			/**
			 * MySQL终端在连接服务器时会执行初始化操作，包括获取每张表的字段信息等。
			 * 由于QS当前尚不支持COM_FIELD_LIST命令，如果直接抛出异常，每次MySQL终端连接过来都会打印大量ERROR级别的日志，影响阅读。
			 * 所以在QsCmdHandler中做了特殊处理，如果发现是UnsupportedCommandTypeException，则只打印DEBUG日志
			 */
			throw new UnsupportedCommandTypeException("Unsupported command type COM_FIELD_LIST");
		} else {
			throw new Exception("Unsupported command type #" + commandType);
		}
		
		MySQLCmd cmd = cmdDecoder.decode(new MinaComBuffer(in), context);
		out.write(cmd);
	}
}
