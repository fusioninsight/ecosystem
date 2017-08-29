# Oracle GoldenGate对接FusionInsight

## 适用场景

> Oracle GoldenGate 12.2 <--> FusionInsight HD V100R002C60U20

## 环境信息

### 软件信息

* Oracle GoldenGate 12.2.0.1.1 for Oracle database
* Oracle GoldenGate 12.2.0.1.1 for BigData
* Oracle database 12.1.0.2.0
* jdk-7u71-linux-x64.rpm
* FusionInsight V100R002C60U20

### 硬件信息

* 源端OGG VM: 162.1.115.68  Redhat6.5 （包含Oracle DB12c的数据库）
* 目标端OGG VM: 162.1.115.69 Redhat6.5（包含Hadoop的客户端）

### 拓朴结构

测试拓朴结构如下图所示：
![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image1.png)

### 测试表

源端测试表：

在源端Oracle的PDBORCL数据库的test用户下创建test1表，其中ID为主键

![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image2.png)

## OGG for Oracle安装

前置条件：完成oracle12c数据库的安装（IP：162.1.115.68）

软件版本：linuxamd64_12102_database_1of2.zip, linuxamd64_12102_database_1of2.zip

### 下载并安装OGG for Oracle

* 将fbo_ggs_Linux_x64_shiphome.zip上传至oracle客户端（ip：162.1.115.68）`/home/oracle`目录下，切换至oracle用户，解压生成bo_ggs_Linux_x64_shiphome目录。

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image3.png)

* 在`/home/oracle/fbo_ggs_Linux_x64_shiphome/Disk1`目录下，运行`./runInstaller`

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image4.png)

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image5.png)

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image6.png)

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image7.png)

* 安装成功，/home/orcle/OGG/是OGG for Oracle的安装目录。

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image8.png)

### 配置环境变量

* 切换到oracle用户

  ```
  su - oracle
  vi .bash_profile
  ```


* 文件.bash_profile内容如下：
  ```shell
  # .bash_profile
  # Get the aliases and functions
  if [ -f ~/.bashrc ]; then
          . ~/.bashrc
  fi

  # User specific environment and startup programs

  PATH=$PATH:$HOME/bin

  export PATH

  PATH=$PATH:$HOME/bin:/u01/app/oracle/product/12.1.0/db_1/bin
  export PATH
  umask 022
  export ORACLE_BASE=/u01/app/oracle
  export ORACLE_HOME=/u01/app/oracle/product/12.1.0/db_1
  export ORACLE_SID=orcl

  export LD_LIBRARY_PATH=$ORACLE_HOME/lib
    ```

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image9.png)

* 运行OGG

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image10.png)

### 打开数据库归档及开启最小附加日志

* 使用Sqlplus / as sysdba登陆Oracle源端数据库后打开Archive Log:
  ```sql
  shutdown immediate;
  startup mount;
  alter database archivelog;
  alter database open;
  archive log list;
  ```

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image11.png)

* 源端数据库打开数据库级最小附加日志及force logging：
  ```sql
  SELECT supplemental_log_data_min, force_logging FROM v$database;
  alter database add supplemental log data;
  alter database force logging;
  ```

* 切换日志以使附加日志生效：
  ```sql
  ALTER SYSTEM switch logfile;
  ```

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image12.png)

* Enabling Oracle GoldenGate in the Database:
  ```sql
  show parameter enable_goldengate_replication;
  alter system set enable_goldengate_replication = true scope=both;
  ```

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image13.png)

* 配置DB12c PDB的tnsname信息`vi $ORACLE_HOME/network/admin/tnsnames.ora`：

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image14.png)

### 在数据库中创建ogg用户并赋予权限

* 使用`sqlplus / as sysdba`登陆数据库后创建ogg用户并赋予权限
  ```sql
  create user c##ogg identified by welcome1;
  grant dba to c##ogg container=all;
  grant create session, connect, resource to c##ogg container=all;
  grant alter any table to c##ogg container=all;
  grant alter system to c##ogg container=all;
  exec dbms_goldengate_auth.grant_admin_privilege('c##ogg',container=>'all');
  ```

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image15.png)

### 配置GoldenGate 登陆数据库的别名

* 在GoldenGate中创建用户别名，用于登录Oracle数据库读取数据库日志：
  ```sql
  add credentialstore
  ALTER CREDENTIALSTORE ADD USER c##ogg PASSWORD welcome1 ALIAS ogg_src
  ```

* 这样就可以用别名ogg_src登陆数据库了：
  ```sql
  dblogin useridalias ogg_src
  ```

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image16.png)

* C##ogg是Oracle DB12c的普通用户，可以访问多个数据库实例。

### 创建test用户和test1表

test用户是基于pdborcl数据库实例的：

* 登陆数据库
  ```
  Sqlplus / as sysdba
  ```

* 创建用户
  ```sql
  alter session set container=pdborcl;
  alter database open;
  create user test identified by welcome1;
  grant resource, connect to test;
  CREATE TABLESPACE test DATAFILE '/u01/app/oracle/oradata/orcl/pdborcl/test01.dbf' SIZE 500M UNIFORM SIZE 128k;
  alter user test quota unlimited on test;
  alter user test quota unlimited on users;
  ```

* 创建测试表
  ```sql
  conn test/welcome1@pdborcl;
  create table test1(id number primary key, name varchar2(50));
  ```

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image17.png)

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image18.png)

### 配置GoldenGate捕获进程

* 编辑eora.prm，在GGSCI命令行下运行edit param eora命令：

  ```
  GGSCI> edit param eora
  ```

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image19.png)

  ```
  GGSCI> edit param mgr
  ```

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image20.png)

  ```
  GGSCI> edit param phdfs
  ```

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image21.png)

  ```
  GGSCI> edit param phbase
  ```

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image22.png)

  ```
  GGSCI> edit param pkafka
  ```

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image23.png)

  ```
  GGSCI> edit param pflume
  ```

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image24.png)

* 编辑`diroby/eora.oby`文件，在GGSCI命令行下运行`shell vi diroby/eora.oby`命令：(shell之后接操作系统命令)

* 使用oracle用户创建diroby目录：

  ```
  cd /home/oracle/OGG/
  mkdir diroby
  ```

  ```
  GGSCI> shell vi diroby/eora.oby
  ```

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image25.png)

  > **注意进程名eora和数据文件dirdat/eo的对应关系**

* 在GGSCI命令行下运行obey diroby/eora.oby命令，把捕获进程eora加入到管理者进程中：

  ```
  GGSCI> obey diroby/eora.oby
  ```

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image26.png)

* 把捕获进程eora注册到pdborcl数据库中：

  ```
  GGSCI> dblogin useridalias ogg_src
  GGSCI> register extract eora database container(pdborcl)

  ```

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image27.png)

* 为pdborcl.test下的所有表添加表级附加日志：

  ```
  GGSCI> add schematrandata pdborcl.test allcols
  ```

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image28.png)

* 启动GoldenGate捕获进程eora:

  ```
  GGSCI> start eora
  ```

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image29.png)

### 配置GoldenGate传输进程phdfs

配置GoldenGate传输进程phdfs，将OGG生成的数据文件传递给目标端GoldenGate
HDFS处理。

* 编辑phdfs.prm，在GGSCI命令行下运行`edit param phdfs`命令：

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image21.png)

* 编辑`diroby/phdfs.oby`文件，在GGSCI命令行下运行`shell vi diroby/phdfs.oby`命令：(shell之后接操作系统命令)

  ```
  GGSCI> shell vi diroby/phdfs.oby
  ```

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image30.png)

  > **注意进程名**phdfs**和数据文件dirdat/rs的对应关系**

* 在GGSCI命令行下运行`obey diroby/phdfs.oby`命令，把捕获进程phdfs加入到管理者进程中：

  ```
  GGSCI> obey diroby/phdfs.oby
  ```

![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image31.png)

* 启动GoldenGate捕获进程phdfs:

  ```
  GGSCI> start phdfs
  ```

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image32.png)

### 配置GoldenGate传输进程phbase

配置GoldenGate传输进程phbase，将OGG生成的数据文件传递给目标端GoldenGate
HBASE处理。

* 编辑phbase.prm，在GGSCI命令行下运行`edit param phbase`命令：

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image22.png)

编辑`diroby/phbase.oby`文件，在GGSCI命令行下运行`shell vi diroby/phbase.oby`命令：(shell之后接操作系统命令)

  ```
  GGSCI> shell vi diroby/phbase.oby
  ```

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image33.png)

  > **注意进程名**phbase**和数据文件dirdat/se的对应关系**

* 在GGSCI命令行下运行`obey diroby/phbase.oby`命令，把捕获进程phbase加入到管理者进程中：

  ```
  GGSCI> obey diroby/phbase.oby
  ```

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image34.png)

* 启动GoldenGate捕获进程phbase:

  ```
  GGSCI> start phbase
  ```

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image35.png)

### 配置GoldenGate传输进程pflume

配置GoldenGate传输进程pflume，将OGG生成的数据文件传递给目标端GoldenGate
FLUME处理。

* 编辑pflume.prm，在GGSCI命令行下运行`edit param pflume`命令：

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image24.png)

* 编辑`diroby/pflume.oby`文件，在GGSCI命令行下运行`shell vi diroby/pflume.oby`命令：(shell之后接操作系统命令)

  ```
  GGSCI> shell vi diroby/pflume.oby
  ```

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image36.png)

  > **注意进程名**pflume**和数据文件dirdat/rf的对应关系**

* 在GGSCI命令行下运行`obey diroby/pflume.oby`命令，把捕获进程pflume加入到管理者进程中：

  ```
  GGSCI> obey diroby/pflume.oby
  ```

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image37.png)

* 启动GoldenGate捕获进程pflume:

  ```
  GGSCI> start pflume
  ```

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image38.png)

### 配置GoldenGate传输进程pkafka

配置GoldenGate传输进程pkafka，将OGG生成的数据文件传递给目标端GoldenGate
Kafka处理。

* 编辑pkafka.prm，在GGSCI命令行下运行`edit param pkafka`命令：

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image23.png)

* 编辑`diroby/pkafka.oby`文件，在GGSCI命令行下运行`shell vi diroby/pkafka.oby`命令：(shell之后接操作系统命令)

  ```
  GGSCI> shell vi diroby/pkafka.oby
  ```

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image39.png)

  > **注意进程名**pkafka**和数据文件dirdat/rk的对应关系**

* 在GGSCI命令行下运行`obey diroby/pkafka.oby`命令，把捕获进程pkafka加入到管理者进程中：

  ```
  GGSCI> obey diroby/ pkafka.oby
  ```

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image40.png)

* 启动GoldenGate捕获进程pkafka:

  ```
  GGSCI> start pkafka
  ```

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image41.png)

### 查看GoldenGate进程运行状态

* 查看GoldenGate进程状态：(EORCL是与ELK对接的进程)

  ```
  GGSCI> info all
  ```

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image42.png)

* 查看某个进程的详细信息：
  ```
  GGSCI> info eora detail
  ```

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image43.png)

* 查看GoldenGate的统计信息：
  ```
  GGSCI> stats eora, latest
  ```

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image44.png)

* 查看GoldenGate进程报告，用于定位问题：
  ```
  GGSCI> view report eora
  ```

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image45.png)

## OGG for Bigdata安装

### 环境准备

* 下载安装FusionInsight客户端

* 在Bigdata客户端机器上（ip：162.1.115.69）按照FusionInsight产品文档安装FusionInsight客户端。将客户端JDK替换成1.7版本。

* 下载并安装oracle JDK1.7

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image46.png)

  将krb5.conf放在/etc/目录下

* 下载并安装OGG for Bigdata

  将122011_ggs_Adapters_Linux_x64.zip上传至客户端/opt目录下：

  ```
  unzip 122011_ggs_Adapters_Linux_x64.zip
  ```

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image47.png)

  将解压后的ggs_Adapters_Linux_x64.tar解压到/opt/OGG_HADOOP目录下：

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image48.png)

* 配置环境变量

  更改环境变量，编辑根目录下`vi .bash_profile`
  ```bash
  # .bash_profile

  # Get the aliases and functions
  if [ -f ~/.bashrc ]; then
          . ~/.bashrc
  fi

  # User specific environment and startup programs
  export JAVA_HOME=/usr/java/jdk1.7.0_40
  #export JAVA_HOME=/usr/lib/jvm/java-1.7.0-openjdk-1.7.0.9.x86_64/jre
  export CLASSPATH=$CLASSPATH:$JAVA_HOME/lib:$JAVA_HOME/jre/lib
  PATH=$JAVA_HOME/bin:$PATH:$HOME/bin

  export PATH

  #export LD_LIBRARY_PATH=/usr/lib/jvm/java-1.7.0-openjdk-1.7.0.9.x86_64/jre/lib/amd64/server/libjvm.so:/usr/lib/jvm/java-1.7.0-openjdk-1.7.0.9.x86_64/jre/lib/amd64/server:/usr/lib/jvm/java-1.7.0-openjdk-1.7.0.9.x86_64/jre/lib/amd64/libjsig.so:/root/OGG_PostgreSQL/lib:$LD_LIBRARY_PATH

  export LD_LIBRARY_PATH=/usr/java/jdk1.7.0_40/jre/lib/amd64/server/libjvm.so:/usr/java/jdk1.7.0_40/jre/lib/amd64/server:/usr/java/jdk1.7.0_40/jre/lib/amd64/libjsig.so:/root/OGG_PostgreSQL/lib:$LD_LIBRARY_PATH

  export LD_LIBRARY_PATH=/usr/local/lib:$LD_LIBRARY_PATH
  ```

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image49.png)

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image50.png)

  Source环境变量，`source .bash_profile`.

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image51.png)

  将`/opt/OGG_HADOOP/AdapterExamples/big-data`下的四个目录下的所有文件拷贝到`/opt/OGG_HADOOP/dirprm`目录下。

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image52.png)

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image53.png)

### 配置GoldenGate管理进程

* 编辑mgr.prm
  ```
  GGSCI> edit param mgr
  ```

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image54.png)
  ```
  GGSCI>start mgr
  GGSCI>info all
  ```

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image55.png)

### 配置GoldenGate HDFS 复制进程

* 编辑rhdfs.prm，在GGSCI命令行下运行`edit param rhdfs`命令：

  ```
  GGSCI> edit param rhdfs
  ```

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image56.png)

* 编辑hdfs.props, 在GGSCI命令行下运行`shell vi dirprm/hdfs.props`命令：(shell之后接操作系统命令)

  ```
  GGSCI> shell vi dirprm/hdfs.props
  ```

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image57.png)

* 需要在HDFS中创建/ogg1目录。

* 将hdfs.keytab文件拷贝到/opt/OGG_HADOOP/dirprm目录中：

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image58.png)

* 把GoldenGate复制进程rhdfs加入到GoldenGate管理者进程中：

  ```
  GGSCI> add replicat rhdfs, exttrail dirdat/rs
  ```

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image59.png)

  ```
  GGSCI>info all
  ```

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image55.png)

  ```
  GGSCI>start rhdfs
  GGSCI>info all
  ```

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image60.png)

### 配置GoldenGate HBase 复制进程

* 编辑rhbase.prm，在GGSCI命令行下运行`edit param rhbase`命令：

  ```
  GGSCI> edit param rhbase
  ```

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image61.png)

* 编辑hbase.props, 在GGSCI命令行下运行`shell vi dirprm/hbase.props`命令：(shell之后接操作系统命令)

  ```
  GGSCI> shell vi dirprm/hbase.props
  ```

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image62.png)

* 拷贝hbase.keytab和jaas.conf到`/opt/OGG_HADOOP/dirprm/`下：

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image63.png)

* jaas.conf 文件

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image64.png)

* 把GoldenGate复制进程rhbase加入到GoldenGate管理者进程中：

  ```
  GGSCI> add replicat rhbase, exttrail dirdat/se
  ```

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image65.png)

  ```
  GGSCI>start rhbase
  ```

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image66.png)

  ```
  GGSCI>info all
  ```

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image67.png)

### 配置GoldenGate Kafka 复制进程

* 创建kafka消息，进入FusionInsight客户端`/opt/hadoopclient/Kafka/kafka/bin`

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image68.png)

  Kafka创建消息：
  ```
  ./kafka-topics.sh --create --zookeeper 162.1.93.101:24002,162.1.93.102:24002,162.1.93.103:24002/kafka --replication-factor 1 --partitions 1 --topic test
  ```

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image69.png)

  Kafka查看消息：
  ```
  ./kafka-topics.sh --list --zookeeper  162.1.93.101:24002,162.1.93.102:24002,162.1.93.103:24002/kafka --topic test
  ```

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image70.png)

  Kafka给消息授权：
  ```
  ./kafka-acls.sh --authorizer-properties zookeeper.connect=162.1.93.101:24002,162.1.93.102:24002,162.1.93.103:24002/kafka --add --operation All --allow-principal User:* --cluster --topic test
  ```

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image71.png)

* 编辑rkafka.prm，在GGSCI命令行下运行`edit param rkafka`命令：

  ```
  GGSCI> edit param rkafka
  ```

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image72.png)

* 编辑kafka.props, 在GGSCI命令行下运行`shell vi dirprm/kafka.props`命令：(shell之后接操作系统命令)

  ```
  GGSCI> shell vi dirprm/kafka.props
  ```

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image73.png)

* 其中 **gg.handler.kafkahandler.BlockingSend** 属性控制同步和异步，默认false，异步。

  ```
  GGSCI> shell vi dirprm/custom_kafka_producer.properties
  ```

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image74.png)

* 修改Kafka里的配置，将如下选项修改为True

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image75.png)

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image76.png)

* 把GoldenGate复制进程rkafka加入到GoldenGate管理者进程中：

  ```
  GGSCI> add replicat rkafka, exttrail dirdat/rk
  ```

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image77.png)

  ```
  GGSCI>start rkafka
  ```

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image78.png)

  ```
  GGSCI>info all
  ```

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image79.png)

### 配置GoldenGate Flume 复制进程

* 安装Flume客户端，配置非加密传输

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image80.png)

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image81.png)

* 配置Server的配置文件properties.properties

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image82.png)

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image83.png)

* 导出的properties.properties文件，增加如下配置：

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image84.png)

* 可以在HDFS中增加/ogg/flume目录

* 将此properties.properties文件上传至FusionInsight。

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image85.png)

* 编辑rflume.prm，在GGSCI命令行下运行`edit param rflume`命令：

  ```
  GGSCI> edit param rflume
  ```

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image86.png)

* 编辑flume.props, 在GGSCI命令行下运行`shell vi dirprm/flume.props`命令：(shell之后接操作系统命令)

  ```
  GGSCI> shell vi dirprm/flume.props
  ```

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image87.png)

  gg.handler.flumehandler.PropagateSchema=false 控制DDL

  gg.handler.flumehandler.format.WrapMessageInGenericAvroMessage=false  相同SCHAME打包

  ```
  GGSCI> shell vi dirprm/custom-flume-rpc.properties
  ```

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image88.png)

* 拷贝flume.keytab文件到`/opt/OGG_HADOOP/dirprm/`目录下

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image89.png)

* 把GoldenGate复制进程rflume加入到GoldenGate管理者进程中：

  ```
  GGSCI> add replicat rflume, exttrail dirdat/rf
  ```

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image90.png)

  ```
  GGSCI>start rflume
  ```

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image91.png)

  ```
  GGSCI>info all
  ```

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image92.png)

## 测试结果

### Oracle端启动所有的传输进程

* 确保所有传输进程均已经正常启动

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image93.png)

### 在Oracle数据库源端做Insert操作

  ```shell
  su – oracle
  source .bash_profile
  sqlplus test/welcome1@pdborcl
  ```

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image94.png)

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image95.png)

* 查看HDFS同步情况，`hadoop fs –ls /ogg1`

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image96.png)

* 查看HBase同步情况

  ```
  hbase shell
  ```

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image97.png)

* 查看kafka结果，进入kafka客户端`/opt/hadoopclient/Kafka/kafka/bin`

  执行以下命令：

  ```shell
  ./kafka-console-consumer.sh --zookeeper 162.1.93.101:24002,162.1.93.102:24002,162.1.93.103:24002/kafka --topic test --from-beginning
  ```

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image98.png)

* 在HDFS中查看flume运行结果：查看/ogg/flume/下数据文件：

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image99.png)


### 在Oracle数据库源端做Update操作

* 执行以下命令
  ```shell
  su – oracle
  source .bash_profile
  sqlplus test/welcome1@pdborcl
  ```

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image100.png)

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image101.png)

* 查看HDFS同步情况，`hadoop fs –ls /ogg1`

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image102.png)

* 查看HBase同步情况

  ```
  hbase shell
  ```

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image103.png)

* 查看kafka结果，进入kafka客户端`/opt/hadoopclient/Kafka/kafka/bin`

  执行以下命令：

  ```shell
  ./kafka-console-consumer.sh --zookeeper 162.1.93.101:24002,162.1.93.102:24002,162.1.93.103:24002/kafka --topic test --from-beginning
  ```

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image104.png)

* 在HDFS中查看flume运行结果：查看/ogg/flume/下数据文件：

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image105.png)

### 在Oracle数据库源端做Delete操作

* 执行以下命令
  ```shell
  su – oracle
  source .bash_profile
  sqlplus test/welcome1@pdborcl
  ```

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image106.png)

* 查看HDFS同步情况，hadoop fs –ls /ogg1

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image107.png)

* 查看HBase同步情况

  ```shell
  hbase shell
  ```

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image108.png)

* 查看kafka结果，进入kafka客户端`/opt/hadoopclient/Kafka/kafka/bin`

  执行以下命令：
  ```shell
  ./kafka-console-consumer.sh --zookeeper 162.1.93.101:24002,162.1.93.102:24002,162.1.93.103:24002/kafka --topic test --from-beginning
  ```

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image109.png)

* 在HDFS中查看flume运行结果：查看/ogg/flume/下数据文件：

  ![](assets/Using_Oracle_GoldenGate_with_FusionInsight/image110.png)
