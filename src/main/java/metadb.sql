--				hinstanceconfig hdb实例的配置
--				hdb  hdb中的数据库
--				htable hdb中的表
--				hindex hdb中的索引
--				hcolumn hdb中的字段
--				huser hdb中的用户
--				hdp hdb中的策略
--				nodeinstance 节点实例
--				nodedb 节点中的数据库


-- 

2个 hdb    hdb0,hdb1
每个hdb下面有4个数据库 hdb0_0..3  hdb1_0..3
2个nodedb nodedb0 , nodedb1

hdb0  htable0_0   3张表  用户基本信息,用户登录信息,用户银行账号信息

--
drop database hdb_metadb ;
create database hdb_metadb charset utf8 ;
use hdb_metadb ;



CREATE TABLE nodeinstance  (
    nodeinstance_id int not null default 0 ,
    nodeinstance_ip varchar(32) not null default '',
    nodeinstance_port int not null default 0 ,
    PRIMARY KEY(nodeinstance_id)
) ENGINE = INNODB comment '节点实例';


CREATE TABLE nodedb  (
    nodedb_id int not null default 0 ,
    nodedb_name varchar(32) not null default '',
    nodeinstance_id int not null default 0 ,
	hdb_id int not null default 0 ,
    PRIMARY KEY(nodedb_id)
) ENGINE = INNODB comment '节点中的数据库';




CREATE TABLE hdbconfig  (
    config_id int not null default 0 ,
    config_name varchar(32) not null default '',
    config_type varchar(32) not null default '' ,
    config_value varchar(128) not null default '' ,
    PRIMARY KEY(config_id)
) ENGINE = INNODB comment 'hdb实例的配置';



CREATE TABLE hdb  (
    hdb_id int not null default 0 ,
    hdb_name varchar(32) not null default '',
    PRIMARY KEY(hdb_id)
) ENGINE = INNODB comment 'hdb中的数据库';


CREATE TABLE htable  (
    htable_id int not null default 0 ,
    htable_name varchar(32) not null default '',
	hdb_id int not null default 0 ,
	hdp_id int not null default 0 ,
    PRIMARY KEY(htable_id)
) ENGINE = INNODB comment 'hdb中的表';

CREATE TABLE hdp  (
    hdp_id int not null default 0 ,
    hdp_name varchar(64) not null default '',
	hdp_type int not null default 0 comment '策略的方式 1 range , 2 hash , 3 hash里面range , 4 range里面hash(不用)',
	hdp_algo int not null default 0 comment '策略的算法 1 单独int , 2 单独字符串 , 3 多个字符串 , 4 int和字符串混合',
	hdp_min bigint not null default 0 ,
	hdp_max bigint not null default 0 ,
	hdp_step int not null default 0 ,
    PRIMARY KEY(hdp_id)
) ENGINE = INNODB comment 'hdb中的表策略';



CREATE TABLE hcolumn  (
    hcolumn_id int not null default 0 ,
    hcolumn_name varchar(64) not null default '',
	hcolumn_type varchar(32) not null default '',
	htable_id int not null default 0 ,
	hdc_flag int not null default 0 ,
	hdc_index int not null default 0 ,
    PRIMARY KEY(hcolumn_id)
) ENGINE = INNODB comment 'hdb中的字段'; 


CREATE TABLE hindex  (
    hindex_id int not null default 0 ,
    htable_id int not null default 0 ,
    hindex_name varchar(32) not null default '',
    PRIMARY KEY(hindex_id)
) ENGINE = INNODB comment 'hdb中的索引';





CREATE TABLE r_hindex_hcolumn  (
    hindex_id int not null default 0 ,
	hcolumn_id int not null default 0 ,
    hcolumn_idxnum int not null default 0 ,
    PRIMARY KEY(hindex_id,hcolumn_id)
) ENGINE = INNODB comment 'hdb中的索引';



replace into nodeinstance values (  0,'192.168.1.10' ,3306 ) ;
replace into nodeinstance values (  1,'192.168.1.11' ,3306 ) ;

replace into nodedb values (  0,'hdb0_0' ,0 ,0) ;
replace into nodedb values (  1,'hdb0_1' ,0 ,0) ;
replace into nodedb values (  2,'hdb0_2' ,0 ,0) ;
replace into nodedb values (  3,'hdb0_3' ,0 ,0) ;

replace into nodedb values (  4,'hdb1_0' ,1 ,1) ;
replace into nodedb values (  5,'hdb1_1' ,1 ,1) ;
replace into nodedb values (  6,'hdb1_2' ,1 ,1) ;
replace into nodedb values (  7,'hdb1_3' ,1 ,1) ;


replace into hdbconfig values (  0,'hdb_id' ,'string','hdb_0000' ) ;
replace into hdbconfig values (  1,'hdb_host' ,'string','0.0.0.0' ) ;
replace into hdbconfig values (  2,'hdb_port' ,'string','8888' ) ;


replace into hdb values (  0,'hdb0' ) ;
replace into hdb values (  1,'hdb1' ) ;


replace into htable values (  0,'htable0_0' ,0 ,0) ;
replace into htable values (  1,'htable0_1' ,0 ,0) ;
replace into htable values (  2,'htable0_2' ,0 ,0) ;

replace into hdp values (  0,'hdp0' ,1,1,  0,100000000,1000000 ) ;

replace into hcolumn values (  0,'uid'   ,'int',          0,  1,0 ) ;
replace into hcolumn values (  1,'uname' ,'varchar(32)',  0,  0,0 ) ;
replace into hcolumn values (  2,'uage'  ,'int',          0,  0,0 ) ;

replace into hcolumn values (  3,'uid'   ,'int',                1,  1,0 ) ;
replace into hcolumn values (  4,'uloginip' ,'varchar(32)',     1,  0,0 ) ;
replace into hcolumn values (  5,'ulogindatetime' ,'datetime',  1,  0,0 ) ;

replace into hcolumn values (  6,'uid' ,'int',  2,  1,0 ) ;
replace into hcolumn values (  7,'ucardbank' ,'varchar(32)',  2,  0,0 ) ;
replace into hcolumn values (  8,'ucardno' ,'varchar(32)',  2,  0,0 ) ;


replace into hindex values (  0,0,'idx_uid'    ) ;
replace into hindex values (  1,0,'idx_uname'  ) ;

replace into hindex values (  2,1,'idx_uid'    ) ;
replace into hindex values (  3,1,'idx_uloginip_ulogindatetime'  ) ;

replace into hindex values (  4,2,'idx_uid'    ) ;
replace into hindex values (  5,2,'idx_ucardno'  ) ;


replace into r_hindex_hcolumn values (  0,0,0  ) ;
replace into r_hindex_hcolumn values (  1,1,0  ) ;

replace into r_hindex_hcolumn values (  2,3,0    ) ;
replace into r_hindex_hcolumn values (  3,4,0  ) ;
replace into r_hindex_hcolumn values (  3,5,1  ) ;

replace into r_hindex_hcolumn values (  4,6,0    ) ;
replace into r_hindex_hcolumn values (  5,8,0  ) ;
