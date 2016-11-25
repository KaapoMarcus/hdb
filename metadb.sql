--				hinstanceconfig hdb实例的配置
--				hdb  hdb中的数据库
--				htable hdb中的表
--				hindex hdb中的索引
--				hcolumn hdb中的字段
--				huser hdb中的用户
--				hdp hdb中的策略
--				nodeinstance 节点实例
--				nodedb 节点中的数据库

-- 2个 hdb    hdb0,hdb1
-- 每个hdb下面有4个数据库 hdb0_0..3  hdb1_0..3
-- 2个nodedb nodedb0 , nodedb1

-- hdb0  htable0_0   3张表  用户基本信息,用户登录信息,用户银行账号信息


drop database hdbmeta ;
create database hdbmeta charset utf8 ;
use hdbmeta ;


CREATE TABLE nodeinstance  (
    nodeinstance_name varchar(64) not null default '',
    PRIMARY KEY(nodeinstance_name)
) ENGINE = INNODB comment '节点实例';


CREATE TABLE nodedb  (
    nodedb_name varchar(64) not null default '',
    nodeinstance_name varchar(64) not null default '',
    hdb_name varchar(64) not null default '',
    hdb_orderno int not null default 0 ,
    PRIMARY KEY(nodeinstance_name,nodedb_name)
) ENGINE = INNODB comment '节点中的数据库';


CREATE TABLE hdbconfig  (
    config_name varchar(64) not null default '',
    config_type varchar(64) not null default '' ,
    config_value varchar(64) not null default '' ,
    PRIMARY KEY(config_name)
) ENGINE = INNODB comment 'hdb实例的配置';


CREATE TABLE hdb  (
    hdb_name varchar(64) not null default '',
    PRIMARY KEY(hdb_name)
) ENGINE = INNODB comment 'hdb中的数据库';


CREATE TABLE htable  (
	hdb_name varchar(64) not null default '',
    htable_name varchar(64) not null default '',
	hdp_name varchar(64) not null default '',
    PRIMARY KEY(hdb_name,htable_name)
) ENGINE = INNODB comment 'hdb中的表';


CREATE TABLE hdp  (
    hdp_name varchar(64) not null default '',
	hdp_type varchar(64) not null default '' comment '策略的方式 1 range , 2 hash , 3 hash里面range , 4 range里面hash(不用)',
	hdp_algo varchar(64) not null default '' comment '策略的算法 1 单独int , 2 单独字符串 , 3 多个字符串 , 4 int和字符串混合',
	hdp_min varchar(64) not null default '',
	hdp_max varchar(64) not null default '',
	hdp_step varchar(64) not null default '',
    PRIMARY KEY(hdp_name)
) ENGINE = INNODB comment 'hdb中的表策略';


CREATE TABLE hcolumn  (
	hdb_name varchar(64) not null default '',
	htable_name varchar(64) not null default '',
    hcolumn_name varchar(64) not null default '',
	hcolumn_type varchar(64) not null default '',
	htable_orderno int not null default 0 ,
	hdc_flag int not null default 0 ,
	hdc_orderno int not null default 0 ,
    PRIMARY KEY(hdb_name,htable_name,hcolumn_name)
) ENGINE = INNODB comment 'hdb中的字段'; 


CREATE TABLE hindex  (
	hdb_name varchar(64) not null default '',
	htable_name varchar(64) not null default '',
    hindex_name varchar(64) not null default '',
    hindex_type varchar(64) not null default ''  comment ' 1 主键 , 2 唯一索引 , 3 二级索引',
    PRIMARY KEY(hdb_name,htable_name,hindex_name)
) ENGINE = INNODB comment 'hdb中的索引';


CREATE TABLE rhindexhcolumn  (
	hdb_name varchar(64) not null default '',
	htable_name varchar(64) not null default '',
    hindex_name varchar(64) not null default '',
	hcolumn_name varchar(64) not null default '',
    hcolumn_orderno int not null default 0 ,
    PRIMARY KEY(hdb_name,htable_name,hindex_name,hcolumn_name)
) ENGINE = INNODB comment 'hdb中的索引和列的对应关系';

