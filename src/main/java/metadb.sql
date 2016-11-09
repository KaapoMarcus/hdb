
-- CREATE UNIQUE INDEX index_name ON tbl_name (index_col_name,...) ;

CREATE TABLE sysconfig (
    id int not null default 0 ,
    var_name varchar(50) not null default '',
    var_value varchar(256) not null default '',
    PRIMARY KEY(id)
) ENGINE = INNODB comment '系统配置';
   
CREATE TABLE user_info (
    id int not null default 0 ,
    username varchar(64) not null default '',
    password varchar(128) not null default '',
    user_type int not null default 0 ,
    grant_str varchar(1024) not null default '',
    clientip_str varchar(1024) not null default '',
    adminip_str varchar(1024) not null default '',
    user_desc varchar(256) not null default '',
    roles varchar(64) not null default '',
    PRIMARY KEY(id)
) ENGINE = INNODB comment '用户信息';

CREATE TABLE db_info (
    id int not null default 0 ,
    dbname varchar(64) not null default '',
    db_ip varchar(20) not null default '',
    db_port int not null default 0 ,
    db_name varchar(255) not null default '',
    db_url varchar(255) not null default '',
    db_status int not null default 0 ,
    isenabled int not null default 0 ,
    sshuser varchar(255) not null default '',
    sshport int not null default 0 ,
    ismaster int not null default 0 ,
    masterdb int not null default 0 ,
    weight int not null default 0 ,
    configFile varchar(1024) not null default '',
    isdirty int not null default 0 ,
    test_query varchar(255) not null default '',
    PRIMARY KEY(id)
) ENGINE = INNODB comment '数据库节点信息';

CREATE TABLE node_info (
    id int not null default 0 ,
    node_ip varchar(16) not null default '',
    syscpu_busy_rate FLOAT(7,4) not null default 0,
    mysqlcpu_used_rate FLOAT(7,4) not null default 0,
    sysmem_used_rate FLOAT(7,4) not null default 0,
    sys_total_mem bigint not null default 0 ,
    sys_free_mem bigint not null default 0 ,
    mysqlmem_used_rate FLOAT(7,4) not null default 0,
    mysql_used_mem bigint not null default 0 ,
    sysnet_rec_bytes_ps bigint not null default 0 ,
    sysnet_tran_bytes_ps bigint not null default 0 ,
    sysdisk_path varchar(48) not null default '',
    sysdisk_total bigint not null default 0 ,
    sysdisk_free bigint not null default 0 ,
    sysdisk_used_rate FLOAT(7,4) not null default 0,
    sysdisk_readKB_ps FLOAT,
    sysdisk_writeKB_ps FLOAT,
    dt DATETIME NOT NULL default '1970-01-01',
    PRIMARY KEY(id)
) ENGINE = INNODB comment '机器节点信息';


CREATE TABLE client_info (
    id int not null default 0 ,
    client_type int not null default 0 ,
    name varchar(50) not null default '',
    ip   varchar(20) not null default '',
    port int not null default 0 ,
    connectTime DATETIME NOT NULL default '1970-01-01',
    accessTime DATETIME NOT NULL default '1970-01-01',
    status int not null default 0 ,
    ddb_user varchar(1024) not null default '',
    PRIMARY KEY(id)
) ENGINE = INNODB comment '客户端信息';

CREATE TABLE table_info (
    id int not null default 0 ,
    name varchar(64) not null default '',
    tab_type varchar(20) not null default '',
    startid bigint not null default 0 ,
    remainid bigint not null default 0 ,
    assigncount bigint not null default 0 ,
    policyname varchar(50) not null default '',
    balancefield varchar(64) not null default '',
    balancefieldtype varchar(16) not null default '',
    usebucketno int not null default 0 ,
    write_enabled int not null default 0 ,
    dupkeychk int not null default 0 ,
    isview int not null default 0 ,
    basetables varchar(512) not null default '',
    viewsql varchar(1024) not null default '',
    num int not null default 0 ,
    loadbalance int not null default 0 ,
    desc_info varchar(60) not null default '',
    model varchar(64) not null default '',
    PRIMARY KEY(id)
) ENGINE = INNODB comment '表信息';

CREATE TABLE column_info (
    id int not null default 0 ,
    name varchar(64) not null default '',
    col_type int not null default 0 ,
    col_len bigint not null default 0 ,
    tablename varchar(64) not null default '',
    num int not null default 0 ,
    isunique int not null default 0 ,
    desc_info varchar(255) not null default '',
    PRIMARY KEY(id)
) ENGINE = INNODB comment '列信息';

CREATE TABLE index_info (
    id int not null default 0 ,
    table_name varchar(64) not null default '',
    index_name varchar(64) not null default '',
    is_primary int not null default 0 ,
    is_unique int not null default 0 ,
    seq_in_index int not null default 0 ,
    column_name varchar(64) not null default '',
    cardinality bigint not null default 0 ,
    rows_per_value bigint not null default 0 ,
    PRIMARY KEY(id)
) ENGINE = INNODB comment '索引信息';
   
CREATE TABLE bucket_info (
    id int not null default 0 ,
    num int not null default 0 ,
    dburl varchar(255) not null default '',
    policyname varchar(255) not null default '',
    PRIMARY KEY(id)
) ENGINE = INNODB comment '桶信息';

CREATE TABLE policy_info (
    id int not null default 0 ,
    policyname varchar(50) not null default '',
    policytype int not null default 0 ,
    arg int not null default 0 ,
    hash varchar(50) not null default '',
    status int not null default 0 ,
    migbucket varchar(2048) not null default '',
    migdburl varchar(255) not null default '',
    desc_info varchar(255) not null default '',
    PRIMARY KEY(id)
) ENGINE = INNODB comment '均衡信息';   

CREATE TABLE bucket_stats (
    id int not null default 0 ,
    task_id int not null default 0 ,
    result_id int not null default 0 ,
    client_id bigint not null default 0 ,
    policy varchar(50) not null default '',
    bucket int not null default 0 ,
    db_url varchar(255) not null default '',
    read_count int not null default 0 ,
    update_count int not null default 0 ,
    insert_count int not null default 0 ,
    PRIMARY KEY(id)
) ENGINE = INNODB comment '均衡统计';

CREATE TABLE column_stats (
    id int not null default 0 ,
    task_id int not null default 0 ,
    result_id int not null default 0 ,
    client_id bigint not null default 0 ,
    tablename varchar(64) not null default '',
    columnname varchar(64) not null default '',
    refer_count int not null default 0 ,
    PRIMARY KEY(id),
    key(task_id, client_id),
    key(tablename, columnname)
) ENGINE = INNODB comment '列统计';

CREATE TABLE index_stats (
    id int not null default 0 ,
    task_id int not null default 0 ,
    result_id int not null default 0 ,
    client_id bigint not null default 0 ,
    tablename varchar(64) not null default '',
    indexname varchar(64) not null default '',
    used_count int not null default 0 ,
    PRIMARY KEY(id)
) ENGINE = INNODB comment '索引统计';

CREATE TABLE table_stats (
    id int not null default 0 ,
    dbn_id int not null default 0 ,
    tablename varchar(64) not null default '',
    engine varchar(16) not null default '',
    row_num bigint not null default 0 ,
    avg_row_len bigint not null default 0 ,
    data_len bigint not null default 0 ,
    max_data_len bigint not null default 0 ,
    index_len bigint not null default 0 ,
    data_free bigint not null default 0 ,
    auto_increment bigint not null default 0 ,
    create_time DATETIME NOT NULL default '1970-01-01',
    update_time DATETIME NOT NULL default '1970-01-01',
    check_time DATETIME NOT NULL default '1970-01-01',
    stats_time DATETIME NOT NULL default '1970-01-01',
    PRIMARY KEY(id)
) ENGINE = INNODB comment '表统计';

CREATE TABLE stat_result (
    id int not null default 0 ,
    task_id int not null default 0 ,
    result_id int not null default 0 ,
    start_time DATETIME NOT NULL default '1970-01-01',
    stop_time DATETIME NOT NULL default '1970-01-01',
    description varchar(1024) not null default '',
    PRIMARY KEY(id)
) ENGINE = INNODB comment '统计结果';
   

  




