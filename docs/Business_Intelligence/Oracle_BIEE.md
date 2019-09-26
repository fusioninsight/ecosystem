# Oracle BIEE对接FusionInsight

## 适用场景

> Oracle BIEE 11g <--> FusionInsight HD V100R002C60U20 (Hive/SparkSQL)
>
> Oracle BIEE 11g <--> FusionInsight HD V100R002C70SPC200 (Hive/SparkSQL/ELK/GaussDB)
>
> Oracle BIEE 12c <--> FusionInsight HD V100R002C60U20 (Hive/SparkSQL)
>
> Oracle BIEE 12c <--> FusionInsight HD V100R002C70SPC200 (Hive/SparkSQL/ELK/GaussDB)
>
> Oracle BIEE 12c <--> FusionInsight HD 6.5 (Hive/SparkSQL/ELK/GaussDB)

## Linux环境安装OBIEE

### 安装OS

* 安装RedHat6.5操作系统，desktop版
* 创建用户oracle

### 安装jdk8

* 获取jdk8安装包，执行安装

  ![](assets/Oracle_BIEE/image1.png)

### 安装Weblogic

* 创建oracle home目录：
  ```shell
  umask 027
  mkdir -p /Oracle/Middleware/Oracle_Home
  chown -R oracle:oracle /Oracle/
  ```

* 上传weblogic安装包，解压

* 以oracle用户登录图形界面

  ![](assets/Oracle_BIEE/image2.png)

  ![](assets/Oracle_BIEE/image3.png)

  ![](assets/Oracle_BIEE/image4.png)

  ![](assets/Oracle_BIEE/image5.png)

  ![](assets/Oracle_BIEE/image6.png)

### 安装BI Server

* 上传OBIEE安装包，解压
  ```shell
  chmod 755 bi_platform-12.2.1.2.0_linux64.bin
  ```

* 以oracle用户登录图形界面
  ```shell
  ./bi_platform-12.2.1.2.0_linux64.bin
  ```

  ![](assets/Oracle_BIEE/image7.png)

  ![](assets/Oracle_BIEE/image8.png)

  ![](assets/Oracle_BIEE/image9.png)

  ![](assets/Oracle_BIEE/image10.png)

  ![](assets/Oracle_BIEE/image11.png)

  ![](assets/Oracle_BIEE/image12.png)

* 补齐lib包
  ```
  yum install -y compat-libcap1 compat-libstdc++-33 libstdc++-devel gcc gcc-c++ libaio-devel
  ```

* 取消当前安装，重新运行安装程序

  ![](assets/Oracle_BIEE/image13.png)

  ![](assets/Oracle_BIEE/image14.png)

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

  ![](assets/Oracle_BIEE/image15.png)

  ![](assets/Oracle_BIEE/image16.png)

  ![](assets/Oracle_BIEE/image17.png)

  ![](assets/Oracle_BIEE/image18.png)

* 创建数据库实例
  ```shell
  cd /Oracle/database/product/12.1.0/dbhome_1/bin/
  ./dbca
  ```

  ![](assets/Oracle_BIEE/image19.png)

  字符集选择AL32UTF8，不勾选“create as container database”

  ![](assets/Oracle_BIEE/image20.png)

  ![](assets/Oracle_BIEE/image21.png)

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

  ![](assets/Oracle_BIEE/image22.png)

  ![](assets/Oracle_BIEE/image23.png)

  ![](assets/Oracle_BIEE/image24.png)

  ![](assets/Oracle_BIEE/image25.png)

  ![](assets/Oracle_BIEE/image26.png)

  ![](assets/Oracle_BIEE/image27.png)

  ![](assets/Oracle_BIEE/image28.png)

### 配置BI Server

* 执行配置
  ```
  cd /Oracle/Middleware/Oracle_Home/bi/bin
  ./config.sh
  ```

  ![](assets/Oracle_BIEE/image29.png)

  ![](assets/Oracle_BIEE/image30.png)

  ![](assets/Oracle_BIEE/image31.png)

  ![](assets/Oracle_BIEE/image32.png)

  ![](assets/Oracle_BIEE/image33.png)

  ![](assets/Oracle_BIEE/image34.png)

  ![](assets/Oracle_BIEE/image35.png)

  ![](assets/Oracle_BIEE/image36.png)

  ![](assets/Oracle_BIEE/image37.png)

### 安装BI Client

* 在Win7(64 bit)系统上安装BI Client

  ![](assets/Oracle_BIEE/image38.png)

  ![](assets/Oracle_BIEE/image39.png)

  ![](assets/Oracle_BIEE/image40.png)

  ![](assets/Oracle_BIEE/image41.png)

## 对接Hive

### 配置客户端系统DSN

* 配置Kerberos认证

  从[http://web.mit.edu/kerberos/](http://web.mit.edu/kerberos/)下载安装kfw-4.1

  ![](assets/Oracle_BIEE/image42.png)

  ![](assets/Oracle_BIEE/image43.png)

* 安装配置Hive ODBC Driver

  下载安装Hive ODBC Driver（Windows版本），[下载地址](https://www.cloudera.com/downloads/connectors/hive/odbc/2-5-5.html)

  在BI客户端所在的Windows机器上配置系统DSN

  ![](assets/Oracle_BIEE/image44.png)

  测试ODBC连接

  ![](assets/Oracle_BIEE/image45.png)

### BI 管理工具新建RDP

* Client端打开Oracle BI 管理工具

  ![](assets/Oracle_BIEE/image46.png)

  ![](assets/Oracle_BIEE/image47.png)

* 选择上一步配置的DSN，用户名口令任意输入，但不能为空

  ![](assets/Oracle_BIEE/image48.png)

  ![](assets/Oracle_BIEE/image49.png)

  ![](assets/Oracle_BIEE/image50.png)

  ![](assets/Oracle_BIEE/image51.png)

  ![](assets/Oracle_BIEE/image52.png)

  ![](assets/Oracle_BIEE/image53.png)

  ![](assets/Oracle_BIEE/image54.png)

  ![](assets/Oracle_BIEE/image55.png)

  ![](assets/Oracle_BIEE/image56.png)

  ![](assets/Oracle_BIEE/image57.png)

  ![](assets/Oracle_BIEE/image58.png)

  ![](assets/Oracle_BIEE/image59.png)

  ![](assets/Oracle_BIEE/image60.png)

  ![](assets/Oracle_BIEE/image61.png)

  ![](assets/Oracle_BIEE/image62.png)

  ![](assets/Oracle_BIEE/image63.png)

  ![](assets/Oracle_BIEE/image64.png)

### 禁用BI Server高速缓存

* 登录Weblogic域管理界面[http://162.1.115.81:9500/em](http://162.1.115.81:9500/em)

  ![](assets/Oracle_BIEE/image65.png)

  ![](assets/Oracle_BIEE/image66.png)

  ![](assets/Oracle_BIEE/image67.png)

  ![](assets/Oracle_BIEE/image68.png)

* 配置中禁用高速缓存

  ![](assets/Oracle_BIEE/image69.png)

  ![](assets/Oracle_BIEE/image70.png)

  ![](assets/Oracle_BIEE/image71.png)

### 上传RPD文件到服务端

* 客户端 cmd 切换到`E:\Oracle\Middleware\Oracle_Home\bi\bitools\bin`目录
* 执行命令上传RPD
  ```
  datamodel.cmd uploadrpd -U weblogic -P Huawei123 -I E:\Oracle\Middleware\Oracle_Home\bi\bifoundation\server\obiee-hive.rpd -W Huawei@123 -S 162.1.115.81 -N 9502 -SI ssi
  ```

  ![](assets/Oracle_BIEE/image72.png)

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

  ![](assets/Oracle_BIEE/image73.png)

* 安装配置Cloudera Hive ODBC Driver

  ```
  yum install -y unixODBC
  ```

  下载Hive ODBC Driver（Linux版本）[下载地址](https://www.cloudera.com/downloads/connectors/hive/odbc/2-5-5.html)

  安装Hive ODBC Driver
  ```
  rpm -Uvh ClouderaHiveODBC-2.5.5.1006-1.el6.x86_64.rpm
  ```

  ![](assets/Oracle_BIEE/image74.png)

  修改DSN配置，与Client端生成的RPD文件的DSN名称和配置保持一致
  ```
  mv /etc/odbc.ini /etc/odbc.ini.bak
  cp /opt/cloudera/hiveodbc/Setup/odbc.ini /etc/
  vi /etc/odbc.ini
  ```

  ![](assets/Oracle_BIEE/image75.png)

  修改odbc配置文件
  ```
  vi /opt/cloudera/hiveodbc/Setup/cloudera.hiveodbc.ini
  ```

  ![](assets/Oracle_BIEE/image76.png)

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

  ![](assets/Oracle_BIEE/image77.png)

* BI域配置系统ODBC
  ```
  cd /Oracle/Middleware/Oracle_Home/user_projects/domains/bi/config/fmwconfig/bienv/core
  cp odbc.ini odbc.ini.bak
  vi odbc.ini
  ```

  ![](assets/Oracle_BIEE/image78.png)

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

  ![](assets/Oracle_BIEE/image79.png)

  ![](assets/Oracle_BIEE/image80.png)

  选择待分析的列拖到右侧区域

  ![](assets/Oracle_BIEE/image81.png)

  点击“结果”页签，检索所选列数据

  ![](assets/Oracle_BIEE/image82.png)

  ![](assets/Oracle_BIEE/image83.png)

  点击右上角的保存按钮，保存查询结果

  ![](assets/Oracle_BIEE/image84.png)

* 创建可视分析器项目

  ![](assets/Oracle_BIEE/image85.png)

  添加数据源

  ![](assets/Oracle_BIEE/image86.png)

  选取数据显示形式

  ![](assets/Oracle_BIEE/image87.png)

  ![](assets/Oracle_BIEE/image88.png)

  ![](assets/Oracle_BIEE/image89.png)

  添加计算

  ![](assets/Oracle_BIEE/image90.png)

## 对接Spark SQL

### 配置客户端系统DSN

* Kerberos认证

  Kerberos获取认证票据

  ![](assets/Oracle_BIEE/image43.png)

* 安装配置Simba Spark ODBC Driver

  下载安装 Simba Spark ODBC Driver：[下载地址](https://downloads.tableausoftware.com/drivers/simba/SimbaSparkODBC64.msi)

  配置DSN：

  ![](assets/Oracle_BIEE/image91.png)

  测试ODBC连接：

  ![](assets/Oracle_BIEE/image92.png)

### BI管理工具新建RDP

* 新建obiee-spark.rdp，DSN选择上一步配置的 Sample Simba Spark DSN

  ![](assets/Oracle_BIEE/image93.png)

### 上传RDP文件到服务端

* 上传RDP

  ![](assets/Oracle_BIEE/image95.png)

### 配置服务端系统DSN

* Kerberos认证
  ```
  su oracle
  kinit test_cn
  ```

  ![](assets/Oracle_BIEE/image73.png)

* 安装配置Simba Spark ODBC Driver

  下载Simba Spark ODBC Driver：[下载地址](https://databricks.com/spark/odbc-driver-download)
  ```
  rpm -Uvh SimbaSparkODBC-1.2.2.1002-1.el6.x86_64.rpm
  ```

  ![](assets/Oracle_BIEE/image96.png)

  修改DSN配置，增加Sample Simba Spark DSN，与Client端配置相同 `vi /etc/odbc.ini`

  ![](assets/Oracle_BIEE/image97.png)

  修改odbcinst.ini，`vi /etc/odbcinist.ini`

  ![](assets/Oracle_BIEE/image98.png)

  配置环境变量 `vi /etc/profile`

  ![](assets/Oracle_BIEE/image99.png)

  导入环境变量 `source /etc/profile`

  测试ODBC连接
  ```
  su oracle
  isql -v 'Sample Simba Spark DSN'
  ```

  ![](assets/Oracle_BIEE/image100.png)

* BI域配置系统ODBC
  ```
  cd /Oracle/Middleware/Oracle_Home/user_projects/domains/bi/config/fmwconfig/bienv/core
  vi odbc.ini
  ```

  ![](assets/Oracle_BIEE/image101.png)

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

  ![](assets/Oracle_BIEE/59b8e.png)

### BI管理工具新建RDP

* 新建obiee-elk.rdp，DSN选择上一步配置的 PostgreSQL35W

  ![](assets/Oracle_BIEE/1e24a.png)

### 上传RDP文件到服务端

* 上传RDP

  ![](assets/Oracle_BIEE/70966.png)

### 配置服务端系统DSN

* 参考LibrA/ELK的产品文档的Linux下配置数据源章节，完成obiee节点下的ODBC驱动的安装

  ![](assets/Oracle_BIEE/e415a.png)

  测试ODBC连接，确保ODBC驱动安装成功
  ```
  isql -v PostgreSQL35W
  ```

  ![](assets/Oracle_BIEE/7c26b.png)

* BI域配置系统ODBC
  ```
  cd /Oracle/Middleware/Oracle_Home/user_projects/domains/bi/config/fmwconfig/bienv/core
  vi odbc.ini
  ```

  在ODBC Data Sources部分增加PostgreSQL35W的DSN

  ![](assets/Oracle_BIEE/a1629.png)

  在文件末尾增加PostgreSQL35W的DSN的详细配置

  ![](assets/Oracle_BIEE/959a4.png)

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
