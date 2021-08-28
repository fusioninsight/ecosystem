# FineBI对接FusionInsight

## 适用场景

> FineBI 5.1 <--> FusionInsight HD 6.5 (Hive)

> FineBI 5.1 <--> FusionInsight MRS 8.0 (Hive/hetu/clickhouse)

## 安装FineBI

- 安装FineBI, 以路径`C:\soft\fineBI\FineBI5.1`为例

## 配置JDBC接口对接Hive

- 将对接集群（版本6.5.1）的认证文件下载到`C:\651client`文件夹下，包括user.keytab和krb5.conf

  说明：如果是对接mrs 8.0版本，根据上步把对应客户端，认证文件下载到本地

- 在FineBI的bin目录下找到配置文件finebi.vmoptions

  ![](assets/FineBI_5.1/markdown-img-paste-20191017104256220.png)

  并在该文件中添加配置：

  ![](assets/FineBI_5.1/markdown-img-paste-20191017104318937.png)

  jaas.conf文件内容为：

  ![](assets/FineBI_5.1/markdown-img-paste-20191017104347390.png)


- 下载FI HD6.5.1的客户端到本地，补全jar包，找到Hive\Beeline\lib路径可以看到相关的jar包

  ![](assets/FineBI_5.1/markdown-img-paste-20191017105301254.png)

  说明： 如果是mrs 8.0版本，则需要的驱动jar包为 客户端路径\Hive\jdbc下所有jar包加上如下额外三个jar包（如果缺少的话）
  ```
  commons-lang-2.6.jar
  zookeeper-jute-3.5.6-hw-ei-302002.jar
  commons-collections-3.2.2.jar
  ```

- 找到并进入FineBI相关依赖路径，具体为`C:\soft\fineBI\FineBI5.1\webapps\webroot\WEB-INF\lib`, 需要做以下三个操作
  1.  找到jar包fine-bi-engine-third-5.1.jar，右键使用winRAR打开

    ![](assets/FineBI_5.1/markdown-img-paste-20191017110457789.png)

    进入org/apache目录，找到并删除zookeeper文件夹

    ![](assets/FineBI_5.1/markdown-img-paste-20191017110523906.png)

  2.  删除FineBI自带的zookeeper-3.4.6.jar

    ![](assets/FineBI_5.1/markdown-img-paste-20191017110604549.png)

  3.  将上一步集群客户端Hive\Beeline\lib路径下所有的jar包拷贝到当前文件夹（`C:\soft\fineBI\FineBI5.1\webapps\webroot\WEB-INF\lib`）

- 找到FineBI路径`C:\soft\fineBI\FineBI5.1\webapps\webroot\WEB-INF\resources`,将认证相关文件user.keytab, krb5.conf, krb5.ini拷贝到改目录下

- 启动FineBI，找到管理系统 -> 数据连接 -> 新建数据连接 -> 更多数据连接 找到fusioninsight hd 选中点确定

  ![](assets/FineBI_5.1/markdown-img-paste-2019101711071559.png)

- （hd 6.5对接）参考下图配置连接参数

  URL: `jdbc:hive2://172.16.4.21:24002,172.16.4.22:24002,172.16.4.23:24002/;serviceDiscoveryMode=zooKeeper;zooKeeperNamespace=hiveserver2;sasl.qop=auth-conf;auth=KERBEROS;principal=hive/hadoop.hadoop.com@HADOOP.COM;user.principal=developuser;user.keytab=C:/651client/user.keytab`

  ![](assets/FineBI_5.1/markdown-img-paste-20191017110741603.png)

  需要注意的是user.principal和user.keytab需要同实际情况匹配

  点击测试连接测试

  ![](assets/FineBI_5.1/markdown-img-paste-20191017110831692.png)

- （mrs 8.0对接）参考下图配置连接参数

  ![20201027_094904_42](assets/FineBI_5.1/20201027_094904_42.png)

  ```
  1： ,172.16.10.132:24002,172.16.10.133:24002/default;serviceDiscoveryMode=zooKeeper;zooKeeperNamespace=hiveserver2;sasl.qop=auth-conf;auth=KERBEROS;principal=hive/hadoop.hadoop.com@HADOOP.COM;

  2： 172.16.10.131
  3： 24002
  4： jdbc:hive2://172.16.10.131:24002,172.16.10.132:24002,172.16.10.133:24002/default;serviceDiscoveryMode=zooKeeper;zooKeeperNamespace=hiveserver2;sasl.qop=auth-conf;auth=KERBEROS;principal=hive/hadoop.hadoop.com@HADOOP.COM;
  ```

  说明（重要）： 配置1 + 配置2 + 配置3 会生成配置4的连接url,要时刻保持着4个配置的正确性，否则对接失败

  点击测试连接测试：

  ![20201027_095147_35](assets/FineBI_5.1/20201027_095147_35.png)

- 点击创建 -> 添加添加数据库表

  ![](assets/FineBI_5.1/markdown-img-paste-20191017110850520.png)

  选择一张表，如果已经选择配置过则为灰色,选择需要导入的数据列表路径

  ![](assets/FineBI_5.1/markdown-img-paste-20191017110903474.png)

  在数据准备->对应的数据列表路径中找到之前配置好的表test

  ![](assets/FineBI_5.1/markdown-img-paste-20191017110918221.png)


## 配置JDBC接口对接Hetu（zk方式连接）

- 参考上述hive配置步骤完成基础配置

- 准备hetu对接配置文件，比如

  ![20201117_171938_94](assets/FineBI_5.1/20201117_171938_94.png)

- 在FineBI的bin目录下找到配置文件finebi.vmoptions

  ![20201117_171605_13](assets/FineBI_5.1/20201117_171605_13.png)

- 找到并进入FineBI相关依赖路径，具体为`C:\soft\fineBI\FineBI5.1\webapps\webroot\WEB-INF\lib`，将hetu驱动Jar包 presto-jdbc-316-hw-ei-302002.jar 导入到该路径下

  ![20201117_172117_65](assets/FineBI_5.1/20201117_172117_65.png)

- 启动FineBI,选择other jdbc做如下配置

  ![20201117_172327_84](assets/FineBI_5.1/20201117_172327_84.png)

  ```
  1: io.prestosql.jdbc.PrestoDriver
  2: ,172.16.10.132:24002,172.16.10.133:24002/hive/default?serviceDiscoveryMode=zooKeeper&zooKeeperNamespace=hsbroker&deploymentMode=on_yarn&user=developuser&SSL=true&SSLTrustStorePath=E:/mrs_hetu_config/hetuserver.jks&KerberosConfigPath=E:/mrs_hetu_config/krb5.conf&KerberosPrincipal=developuser&KerberosKeytabPath=E:/mrs_hetu_config/user.keytab&KerberosRemoteServiceName=HTTP&KerberosServicePrincipalPattern=%24%7BSERVICE%7D%40%24%7BHOST%7D
  3: 172.16.10.131
  4: 24002
  5: jdbc:presto://172.16.10.131:24002,172.16.10.132:24002,172.16.10.133:24002/hive/default?serviceDiscoveryMode=zooKeeper&zooKeeperNamespace=hsbroker&deploymentMode=on_yarn&user=developuser&SSL=true&SSLTrustStorePath=E:/mrs_hetu_config/hetuserver.jks&KerberosConfigPath=E:/mrs_hetu_config/krb5.conf&KerberosPrincipal=developuser&KerberosKeytabPath=E:/mrs_hetu_config/user.keytab&KerberosRemoteServiceName=HTTP&KerberosServicePrincipalPattern=%24%7BSERVICE%7D%40%24%7BHOST%7D
  ```

  说明（重要）： 配置2 + 配置3 + 配置4 会生成配置5的连接url,要时刻保持着4个配置的正确性，否则对接失败

- 测试连接：

  ![20201117_172641_43](assets/FineBI_5.1/20201117_172641_43.png)

- 查询结果：

  ![20201117_172718_35](assets/FineBI_5.1/20201117_172718_35.png)

## 配置JDBC接口对接Hetu（直连方式连接）

- 找到并进入FineBI相关依赖路径，具体为C:\soft\fineBI\FineBI5.1\webapps\webroot\WEB-INF\lib，将hetu驱动Jar包 presto-jdbc-316-hw-ei-310010.jar 导入到该路径下

- 启动FineBI,选择Presto做如下配置

  ![20210828_113141_75](assets/FineBI_5.1/20210828_113141_75.png)

  ```
  1: Presto
  2: io.prestosql.jdbc.PrestoDriver
  3: ,192.100.0.53:29860/hive/default?serviceDiscoveryMode=hsbroker
  4: 192.100.0.10
  5. 29860
  6: developuser
  7: 密码
  ```

  说明（重要）： 配置3 + 配置4 + 配置5 会生成最终的连接url,要时刻保持着4个配置的正确性，否则对接失败

- 测试连接：

  ![20210828_113503_49](assets/FineBI_5.1/20210828_113503_49.png)

- 查询结果：

  ![20210828_113609_55](assets/FineBI_5.1/20210828_113609_55.png)


## 配置JDBC接口对接ClickHouse

- 准备clickhouse测试数据

  - 首先查看clickhouseserver实例ip

    ![20210518_113933_28](assets/FineBI_5.1/20210518_113933_28.png)

  - 检查测试用户是否有clickhouse的权限

    ![20210518_114025_64](assets/FineBI_5.1/20210518_114025_64.png)

  - 登录客户端，登录所有的clickhouseserver，创建表

    ```
    Kinit developuser

    登录第一个clickhouseserver: clickhouse client --host 172.16.5.53 --port 21423

    建表：CREATE TABLE ceshi_TinyLog(uid Int64,uname String,wid Int64,word String,pv Int64,click Int64,cost float,date Date,time String) ENGINE=TinyLog;

    登录另一个clickhouseserver: clickhouse client --host 172.16.5.52 --port 21423

    建表：CREATE TABLE ceshi_TinyLog(uid Int64,uname String,wid Int64,word String,pv Int64,click Int64,cost float,date Date,time String) ENGINE=TinyLog;
    ```

  - 使用命令传数据

    ```
    clickhouse client -m --host 172.16.5.53 --port 21423 --database="default" --query="insert into default.ceshi_TinyLog FORMAT CSV" < /opt/clickhousenew.csv

    clickhouse client -m --host 172.16.5.52 --port 21423 --database="default" --query="insert into default.ceshi_TinyLog FORMAT CSV" < /opt/clickhousenew.csv
    ```

    样例数据clickhousenew.csv

    ```
    27,花信风,22,图片,6,0,568.1720730083482,2020-03-16,10:07:01
    38,侯振宇,3,官网,4,8,539.9461401800766,2020-03-23,18:11:31
    31,韩浩月,9,儿童,5,3,473.69330165688615,2020-04-14,00:43:02
    61,恭小兵,10,阅读网,5,9,694.1459730283839,2020-04-03,23:17:17
    0,李公明,13,全集观看,18,10,837.9050944474849,2020-04-22,08:35:21
    74,傅光明,3,官网,20,0,526.4335879041444,2020-03-02,02:38:17
    63,高远,17,房屋租赁,17,8,487.0733326823028,2020-03-17,03:37:22
    8,李轶男,11,查询网,8,3,275.12075933899723,2020-04-03,06:38:30
    81,杜仲华,6,查询电话,12,5,90.02009064670109,2020-03-18,11:55:54
    65,郭妮,0,网站大全,18,9,840.7250869772428,2020-03-01,21:32:25
    15,洁尘,26,六年,11,8,529.7926355483769,2020-04-01,12:05:25
    ```

- clickhouse客户端检查数据：  

  ```
  Kinit developuser

  clickhouse client --host 172.16.5.53 --port 21423
  ```

  ![20210518_114754_21](assets/FineBI_5.1/20210518_114754_21.png)

- 启动FineBI,选择ClickHouse做如下配置

  ![20210828_115358_38](assets/FineBI_5.1/20210828_115358_38.png)

- 测试链接

  ![20210828_115443_30](assets/FineBI_5.1/20210828_115443_30.png)

- 查看结果

  ![20210828_115510_62](assets/FineBI_5.1/20210828_115510_62.png)
