package com.jhh.hdb.proxyserver.define;


public class MySQLCommandNumber {
	
    public static final byte COM_SLEEP               = 0x00;       // (none, this is an internal thread state)
    public static final byte COM_QUIT                = 0x01;       // mysql_close
    public static final byte COM_INIT_DB             = 0x02;       // mysql_select_db
    public static final byte COM_QUERY               = 0x03;       // mysql_real_query
    public static final byte COM_FIELD_LIST          = 0x04;       // mysql_list_fields
    public static final byte COM_CREATE_DB           = 0x05;       // mysql_create_db (deprecated)
    public static final byte COM_DROP_DB             = 0x06;       // mysql_drop_db (deprecated)
    public static final byte COM_REFRESH             = 0x07;       // mysql_refresh
    public static final byte COM_SHUTDOWN            = 0x08;       // mysql_shutdown
    public static final byte COM_STATISTICS          = 0x09;       // mysql_stat
    public static final byte COM_PROCESS_INFO        = 0x0a;       // mysql_list_processes
    public static final byte COM_CONNECT             = 0x0b;       // (none, this is an internal thread state)
    public static final byte COM_PROCESS_KILL        = 0x0c;       // mysql_kill
    public static final byte COM_DEBUG               = 0x0d;       // mysql_dump_debug_info
    public static final byte COM_PING                = 0x0e;       // mysql_ping
    public static final byte COM_TIME                = 0x0f;       // (none, this is an internal thread state)
    public static final byte COM_DELAYED_INSERT      = 0x10;       // (none, this is an internal thread state)
    public static final byte COM_CHANGE_USER         = 0x11;       // mysql_change_user
    public static final byte COM_BINLOG_DUMP         = 0x12;       // (used by slave server / mysqlbinlog)
    public static final byte COM_TABLE_DUMP          = 0x13;       // (used by slave server to get master table)
    public static final byte COM_CONNECT_OUT         = 0x14;       // (used by slave to log connection to master)
    public static final byte COM_REGISTER_SLAVE      = 0x15;       // (used by slave to register to master)
    public static final byte COM_STMT_PREPARE        = 0x16;       // mysql_stmt_prepare
    public static final byte COM_STMT_EXECUTE        = 0x17;       // mysql_stmt_execute
    public static final byte COM_STMT_SEND_LONG_DATA = 0x18;       // mysql_stmt_send_long_data
    public static final byte COM_STMT_CLOSE          = 0x19;       // mysql_stmt_close
    public static final byte COM_STMT_RESET          = 0x1a;       // mysql_stmt_reset
    public static final byte COM_SET_OPTION          = 0x1b;       // mysql_set_server_option
    public static final byte COM_STMT_FETCH          = 0x1c;       // mysql_stmt_fetch
    public static final byte COM_EOF                 = (byte) 0xfe; //
    
}
