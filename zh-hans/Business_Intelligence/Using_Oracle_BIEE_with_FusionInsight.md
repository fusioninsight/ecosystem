# Oracle BIEE对接FusionInsight

## 适用场景

> Oracle BIEE 11g <-> FusionInsight HD V100R002C60U20
>
> Oracle BIEE 11g <-> FusionInsight HD V100R002C70SPC200
>
> Oracle BIEE 12c <-> FusionInsight HD V100R002C60U20
>
> Oracle BIEE 12c <-> FusionInsight HD V100R002C70SPC200
>
> Oracle BIEE 12c <-> FusionInsight HD V100R002C80SPC200

## Linux环境安装OBIEE

### 安装OS

* 安装RedHat6.5操作系统，desktop版
* 创建用户oracle

### 安装jdk8

* 获取jdk8安装包，执行安装

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image1.png)

### 安装Weblogic

* 创建oracle home目录：
  ```shell
  umask 027
  mkdir -p /Oracle/Middleware/Oracle_Home
  chown -R oracle:oracle /Oracle/
  ```

* 上传weblogic安装包，解压

* 以oracle用户登录图形界面

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image2.png)

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image3.png)

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image4.png)

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image5.png)

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image6.png)

### 安装BI Server

* 上传OBIEE安装包，解压
  ```shell
  chmod 755 bi_platform-12.2.1.2.0_linux64.bin
  ```

* 以oracle用户登录图形界面
  ```shell
  ./bi_platform-12.2.1.2.0_linux64.bin
  ```

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image7.png)

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image8.png)

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image9.png)

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image10.png)

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image11.png)

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image12.png)

* 补齐lib包
  ```
  yum install -y compat-libcap1 compat-libstdc++-33 libstdc++-devel gcc gcc-c++ libaio-devel
  ```

* 取消当前安装，重新运行安装程序

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image13.png)

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image14.png)

### 安装Oracle Database 12c

* 安装数据库软件

  创建数据库安装目录
  ```shell
  mkdir -p /Oracle/database
  chown -R oracle:oracle /Oracle
  ```

  下载Oracle Database 12c安装包，解压得到database文件夹
  ```shell
  chmod -R 755 database/
  cd database/
  su oracle
  ./runInstaller
  ```

  只安装单实例数据库软件

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image15.png)

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image16.png)

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image17.png)

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image18.png)

* 创建数据库实例
  ```shell
  cd /Oracle/database/product/12.1.0/dbhome_1/bin/
  ./dbca
  ```

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image19.png)

  字符集选择AL32UTF8，不勾选“create as container database”

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image20.png)

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image21.png)

* 配置环境变量`vi ~/.bash_profile`
  ```bash
  ORACLE_BASE=/Oracle/database
  ORACLE_HOME=$ORACLE_BASE/product/12.1.0/dbhome_1
  ORACLE_SID=orcl
  ORACLE_TERM=xterm
  PATH=$PATH:$ORACLE_HOME/bin
  export ORACLE_BASE
  export ORACLE_HOME
  export ORACLE_SID
  export ORACLE_TERM
  export PATH
  ```

  导入环境变量
  ```
  source ~/.bash_profile
  ```

* 配置监听程序和网络服务名
  ```
  netca
  ```

  Listener端口设为默认值1521

  网络服务名配置为 ORCL

* 启动数据库和监听程序

  主机重启后，需要重新执行以下命令启动数据库和监听程序
  ```shell
  su oracle
  source ~/.bash_profile
  lsnrctl start
  sqlplus / as sysdba
  ```

  sqlplus界面执行`startup`

### 使用RCU创建Schema

* 启动rcu
  ```
  cd /Oracle/Middleware/Oracle_Home/oracle_common/bin/
  ./rcu
  ```

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image22.png)

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image23.png)

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image24.png)

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image25.png)

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image26.png)

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image27.png)

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image28.png)

### 配置BI Server

* 执行配置
  ```
  cd /Oracle/Middleware/Oracle_Home/bi/bin
  ./config.sh
  ```

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image29.png)

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image30.png)

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image31.png)

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image32.png)

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image33.png)

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image34.png)

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image35.png)

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image36.png)

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image37.png)

### 安装BI Client

* 在Win7(64 bit)系统上安装BI Client

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image38.png)

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image39.png)

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image40.png)

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image41.png)

## 对接Hive

### 配置客户端系统DSN

* 配置Kerberos认证

  从[http://web.mit.edu/kerberos/](http://web.mit.edu/kerberos/)下载安装kfw-4.1

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image42.png)

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image43.png)

* 安装配置Hive ODBC Driver

  下载安装Hive ODBC Driver（Windows版本），[下载地址](https://www.cloudera.com/downloads/connectors/hive/odbc/2-5-5.html)

  在BI客户端所在的Windows机器上配置系统DSN

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image44.png)

  测试ODBC连接

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image45.png)

### BI 管理工具新建RDP

* Client端打开Oracle BI 管理工具

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image46.png)

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image47.png)

* 选择上一步配置的DSN，用户名口令任意输入，但不能为空

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image48.png)

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image49.png)

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image50.png)

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image51.png)

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image52.png)

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image53.png)

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image54.png)

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image55.png)

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image56.png)

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image57.png)

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image58.png)

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image59.png)

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image60.png)

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image61.png)

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image62.png)

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image63.png)

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image64.png)

### 禁用BI Server高速缓存

* 登录Weblogic域管理界面[http://162.1.115.81:9500/em](http://162.1.115.81:9500/em)

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image65.png)

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image66.png)

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image67.png)

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image68.png)

* 配置中禁用高速缓存

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image69.png)

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image70.png)

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image71.png)

### 上传RPD文件到服务端

* 客户端 cmd 切换到`E:\Oracle\Middleware\Oracle_Home\bi\bitools\bin`目录
* 执行命令上传RPD
  ```
  datamodel.cmd uploadrpd -U weblogic -P Huawei123 -I E:\Oracle\Middleware\Oracle_Home\bi\bifoundation\server\obiee-hive.rpd -W Huawei@123 -S 162.1.115.81 -N 9502 -SI ssi
  ```

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image72.png)

### 配置服务端系统DSN

* 配置Kerberos认证

  ```
  mv /etc/krb5.conf /etc/krb5.conf.bak
  ```

  将FusionInsight集群的krb5.conf上传到/etc目录下

  kerberos认证
  ```
  su oracle
  kinit test_cn
  ```

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image73.png)

* 安装配置Cloudera Hive ODBC Driver

  ```
  yum install -y unixODBC
  ```

  下载Hive ODBC Driver（Linux版本）[下载地址](https://www.cloudera.com/downloads/connectors/hive/odbc/2-5-5.html)

  安装Hive ODBC Driver
  ```
  rpm -Uvh ClouderaHiveODBC-2.5.5.1006-1.el6.x86_64.rpm
  ```

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image74.png)

  修改DSN配置，与Client端生成的RPD文件的DSN名称和配置保持一致
  ```
  mv /etc/odbc.ini /etc/odbc.ini.bak
  cp /opt/cloudera/hiveodbc/Setup/odbc.ini /etc/
  vi /etc/odbc.ini
  ```

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image75.png)

  修改odbc配置文件
  ```
  vi /opt/cloudera/hiveodbc/Setup/cloudera.hiveodbc.ini
  ```

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image76.png)

  ```
  mv /etc/odbcinst.ini /etc/odbcinst.ini.bak
  cp /opt/cloudera/hiveodbc/Setup/odbcinst.ini /etc/
  ```

  配置环境变量`vi /etc/profile`
  ```
  export LD_LIBRARY_PATH=/usr/lib64:/opt/cloudera/hiveodbc/lib/64
  export ODBCINI=/etc/odbc.ini
  export ODBCSYSINI=/etc
  export SIMBAINI=/opt/cloudera/hiveodbc/Setup/cloudera.hiveodbc.ini
  ```

  导入环境变量`source /etc/profile`

  测试ODBC连接
  ```
  su oracle
  isql -v 'Sample Cloudera Hive DSN'
  ```

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image77.png)

* BI域配置系统ODBC
  ```
  cd /Oracle/Middleware/Oracle_Home/user_projects/domains/bi/config/fmwconfig/bienv/core
  cp odbc.ini odbc.ini.bak
  vi odbc.ini
  ```

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image78.png)

  重启OBIS
  ```
  su oracle
  cd /Oracle/Middleware/Oracle_Home/user_projects/domains/bi/bitools/bin
  ./stop.sh
  ./start.sh
  ```

### 服务端分析Hive数据

* 打开BI Analytics界面[http://162.1.115.81:9502/analytics](http://162.1.115.81:9502/analytics)

* 创建分析

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image79.png)

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image80.png)

  选择待分析的列拖到右侧区域

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image81.png)

  点击“结果”页签，检索所选列数据

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image82.png)

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image83.png)

  点击右上角的保存按钮，保存查询结果

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image84.png)

* 创建可视分析器项目

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image85.png)

  添加数据源

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image86.png)

  选取数据显示形式

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image87.png)

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image88.png)

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image89.png)

  添加计算

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image90.png)

## 对接Spark SQL

### 配置客户端系统DSN

* Kerberos认证

  Kerberos获取认证票据

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image43.png)

* 安装配置Simba Spark ODBC Driver

  下载安装 Simba Spark ODBC Driver：[下载地址](https://downloads.tableausoftware.com/drivers/simba/SimbaSparkODBC64.msi)

  配置DSN：

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image91.png)

  测试ODBC连接：

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image92.png)

### BI管理工具新建RDP

* 新建obiee-spark.rdp，DSN选择上一步配置的 Sample Simba Spark DSN

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image93.png)

### 上传RDP文件到服务端

* 上传RDP

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image95.png)

### 配置服务端系统DSN

* Kerberos认证
  ```
  su oracle
  kinit test_cn
  ```

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image73.png)

* 安装配置Simba Spark ODBC Driver

  下载Simba Spark ODBC Driver：[下载地址](https://databricks.com/spark/odbc-driver-download)
  ```
  rpm -Uvh SimbaSparkODBC-1.2.2.1002-1.el6.x86_64.rpm
  ```

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image96.png)

  修改DSN配置，增加Sample Simba Spark DSN，与Client端配置相同 `vi /etc/odbc.ini`

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image97.png)

  修改odbcinst.ini，`vi /etc/odbcinist.ini`

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image98.png)

  配置环境变量 `vi /etc/profile`

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image99.png)

  导入环境变量 `source /etc/profile`

  测试ODBC连接
  ```
  su oracle
  isql -v 'Sample Simba Spark DSN'
  ```

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image100.png)

* BI域配置系统ODBC
  ```
  cd /Oracle/Middleware/Oracle_Home/user_projects/domains/bi/config/fmwconfig/bienv/core
  vi odbc.ini
  ```

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/image101.png)

  重启OBIS
  ```
  su oracle
  cd /Oracle/Middleware/Oracle_Home/user_projects/domains/bi/bitools/bin
  ./stop.sh
  ./start.sh
  ```

### 服务端分析Spark数据

参考[服务端分析Hive数据](#服务端分析hive数据)

## 对接LibrA/ELK

配置LibrA与ELK的方式没有区别，以下以对接ELK为例进行操作

### 配置客户端系统DSN

* 配置obiee客户端的ODBC驱动

  按照ELK的产品文档的指导安装配置ELK的windows驱动

  配置DSN，测试ODBC连接，保存ODBC连接

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/59b8e.png)

### BI管理工具新建RDP

* 新建obiee-elk.rdp，DSN选择上一步配置的 PostgreSQL35W

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/1e24a.png)

### 上传RDP文件到服务端

* 上传RDP

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/70966.png)

### 配置服务端系统DSN

* 参考LibrA/ELK的产品文档的Linux下配置数据源章节，完成obiee节点下的ODBC驱动的安装

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/e415a.png)

  测试ODBC连接，确保ODBC驱动安装成功
  ```
  isql -v PostgreSQL35W
  ```

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/7c26b.png)

* BI域配置系统ODBC
  ```
  cd /Oracle/Middleware/Oracle_Home/user_projects/domains/bi/config/fmwconfig/bienv/core
  vi odbc.ini
  ```

  在ODBC Data Sources部分增加PostgreSQL35W的DSN

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/a1629.png)

  在文件末尾增加PostgreSQL35W的DSN的详细配置

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/959a4.png)

  > PostgreSQL35W的DSN的详细配置最后一行DriverUnicodeType=1需要加上，否则obiee查询的时候会报错[nQSError: 12010] Communication error connecting to remote end point: address = obiee; port = 9514. (HY000)

  重启OBIS
  ```
  su oracle
  cd /Oracle/Middleware/Oracle_Home/user_projects/domains/bi/bitools/bin
  ./stop.sh
  ./start.sh
  ```

### 服务端分析Spark数据

参考[服务端分析Hive数据](#服务端分析hive数据)

Q&A
1.在服务端执行isql可以正常连接至数据库，在web界面上显示无法加载驱动

  ![](assets/Using_Oracle_BIEE_with_FusionInsight/e45af.png)
  A:
