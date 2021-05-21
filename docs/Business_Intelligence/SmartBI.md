# SmartBI对接FusionInsight

## 适用场景
>
> SmartBI 9.5.64075.21175 <--> FusionInsight MRS 8.1.0 (Hive/Hetu/Clickhouse)

## Smartbi配置

- 修改smartbi主机的hosts文件将对接集群主机名加入

  ![20210521_104108_27](assets/smartbi/20210521_104108_27.png)

- 修改`C:\Smartbi\Tomcat\bin\startup.cmd`配置文件增加jvm启动参数

  ![20210521_102028_88](assets/smartbi/20210521_102028_88.png)

  ```
  -Djava.security.krb5.conf=C:/hetu/krb5.conf -Djava.security.auth.login.config=C:/hetu/jaas-zk.conf -Dzookeeper.server.principal=zookeeper/hadoop.hadoop.com -Dzookeeper.sasl.clientconfig=Client -Dzookeeper.auth.type=kerberos
  ```

- 对应的jaas-zk.conf文件内容为

  ```
  Client {
  com.sun.security.auth.module.Krb5LoginModule required
  useKeyTab=true
  keyTab="C:/hetu/user.keytab"
  principal="admintest@HADOOP.COM"
  useTicketCache=false
  storeKey=true
  debug=true;
  };
  ```

- 重启smartbi使jvm配置生效

## 对接hive(zk模式)

- 进入smartbi安装目录C:\Smartbi\Tomcat\bin\dynamicLibraryPath，新建驱动存放路径，例如MRS_Hive，并将hive的JDBC驱动放置于该目录下，如下图所示

  ![20210521_143128_31](assets/smartbi/20210521_143128_31.png)

  注意：从MRS客户端（/opt/client/Hive/Beeline/lib/jdbc）获取jar包本地上传，注意要删除该路径下的jdbc_pom.xml文件

- 启动smartbi，创建数据连接

  ![20210521_143652_81](assets/smartbi/20210521_143652_81.png)

  ```
  1. Huwei FusionInsight HD
  2. 选择自定义驱动  Hive_Hive
  3. org.apache.hive.jdbc.HiveDriver
  4. jdbc:hive2://172.16.5.51:24002,172.16.5.52:24002,172.16.5.53:24002/;serviceDiscoveryMode=zooKeeper;zooKeeperNamespace=hiveserver2;sasl.qop=auth-conf;auth=KERBEROS;principal=hive/hadoop.hadoop.com@HADOOP.COM;user.principal=admintest;user.keytab=C:/hetu/user.keytab
  5. admintest
  6. C:/hetu/krb5.conf
  7. C:/hetu/user.keytab
  ```

- 测试数据连接

  ![20210521_145215_10](assets/smartbi/20210521_145215_10.png)

- 创建数据集

  ![20210521_145257_35](assets/smartbi/20210521_145257_35.png)

  ![20210521_145310_31](assets/smartbi/20210521_145310_31.png)

- 查看数据结果

  ![20210521_145356_43](assets/smartbi/20210521_145356_43.png)

  ![20210521_105039_19](assets/smartbi/20210521_105039_19.png)  

## 对接hetu(zk模式)

- 进入smartbi安装目录C:\Smartbi\Tomcat\bin\dynamicLibraryPath，新建驱动存放路径，例如MRS_Hetu，并将hetu的JDBC驱动放置于该目录下，如下图所示

  ![20210521_104302_84](assets/smartbi/20210521_104302_84.png)

- 准备连接hetu的配置文件

  ![20210521_104336_53](assets/smartbi/20210521_104336_53.png)

  其中hetuserver.jks请从hetu broker节点获取（比如：/opt/huawei/Bigdata/FusionInsight_Hetu_8.1.0/1_6_HSBroker/etc/hetuserver.jks）

- 启动smartbi，创建数据连接

  ![20210521_105349_21](assets/smartbi/20210521_105349_21.png)

  ```
  1：Presto
  2: 选择自定义 MRS_Hetu
  3: io.prestosql.jdbc.PrestoDriver
  4: jdbc:presto://172.16.5.51:24002,172.16.5.52:24002,172.16.5.53:24002?serviceDiscoveryMode=zooKeeper&zooKeeperNamespace=hsbroker&deploymentMode=on_yarn&SSL=true&KerberosConfigPath=C:/hetu/krb5.conf&KerberosPrincipal=admintest&KerberosKeytabPath=C:/hetu/user.keytab&KerberosRemoteServiceName=HTTP&KerberosServicePrincipalPattern=%24%7BSERVICE%7D%40%24%7BHOST%7D
  5: admintest
  ```

  注意：密码可以留空

- 测试数据连接

  ![20210521_104802_67](assets/smartbi/20210521_104802_67.png)

- 创建数据集

  ![20210521_104902_64](assets/smartbi/20210521_104902_64.png)

  ![20210521_104938_16](assets/smartbi/20210521_104938_16.png)

- 查看数据结果

  ![20210521_105022_56](assets/smartbi/20210521_105022_56.png)

  ![20210521_105039_19](assets/smartbi/20210521_105039_19.png)  

## 对接hetu(用户名密码模式)

- 进入smartbi安装目录C:\Smartbi\Tomcat\bin\dynamicLibraryPath，新建驱动存放路径，例如MRS_Hetu，并将hetu的JDBC驱动放置于该目录下，如下图所示

  ![20210521_104302_84](assets/smartbi/20210521_104302_84.png)

- 准备连接hetu的配置文件

  ![20210521_104336_53](assets/smartbi/20210521_104336_53.png)

  其中hetuserver.jks请从hetu broker节点获取（比如：/opt/huawei/Bigdata/FusionInsight_Hetu_8.1.0/1_6_HSBroker/etc/hetuserver.jks）

- 启动smartbi，创建数据连接

  ![20210521_142545_70](assets/smartbi/20210521_142545_70.png)

  ```
  1：Presto
  2: 选择自定义 MRS_Hetu
  3: io.prestosql.jdbc.PrestoDriver
  4: jdbc:presto://172.16.5.51:29860,172.16.5.52:29860/hive/default?serviceDiscoveryMode=hsbroker
  5: 登录用户
  6：登录用户密码
  ```

- 测试数据连接

  ![20210521_142830_94](assets/smartbi/20210521_142830_94.png)

- 创建数据集

  ![20210521_142905_22](assets/smartbi/20210521_142905_22.png)

  ![20210521_142928_23](assets/smartbi/20210521_142928_23.png)


- 查看数据结果

  ![20210521_143005_63](assets/smartbi/20210521_143005_63.png)

  ![20210521_143020_47](assets/smartbi/20210521_143020_47.png)

## 对接clickhouse

- 准备clickhouse测试数据

  - 首先查看clickhouseserver实例ip

    ![20210518_113933_28](assets/smartbi/20210518_113933_28.png)

  - 检查测试用户是否有clickhouse的权限

    ![20210518_114025_64](assets/smartbi/20210518_114025_64.png)

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

  ![20210518_114754_21](assets/smartbi/20210518_114754_21.png)  

- 启动smartbi，创建数据连接

  ![20210521_145718_95](assets/smartbi/20210521_145718_95.png)

  ```
  1. Clickhouse
  2. 产品内置
  3. ru.yandex.clickhouse.ClickHouseDriver
  4. jdbc:clickhouse://172.16.5.53:21421
  5. clickhouse用户
  6. clickhouse用户密码
  ```

- 测试数据连接

  ![20210521_145835_84](assets/smartbi/20210521_145835_84.png)

- 创建数据集

  ![20210521_145906_80](assets/smartbi/20210521_145906_80.png)

  ![20210521_145923_64](assets/smartbi/20210521_145923_64.png)

- 查看数据结果

  ![20210521_150006_83](assets/smartbi/20210521_150006_83.png)

  ![20210521_150022_63](assets/smartbi/20210521_150022_63.png)

## FAQ

问题1 使用zk方式连接hetu的时候遇到如下报错：

![20210519_171412_28](assets/smartbi/20210519_171412_28.png)

`failed to connect zookeeper `

问题原因：连接MRS ZK遇到问题

解决方法：

1. 检查hetu jdbc url的正确性
2. 检查jvm配置（C:\Smartbi\Tomcat\bin\startup.cmd）参数是否正确配置

问题2 使用zk方式连接hetu的时候遇到如下报错：

![20210519_172100_49](assets/smartbi/20210519_172100_49.png)

问题原因：smartbi主机hosts文件未配置对接集群主机名

解决办法：

检查配置文件C:\Windows\System32\drivers\etc\hosts 添加集群主机名


问题3 使用zk方式连接hetu的时候遇到如下报错：

![20210519_172320_28](assets/smartbi/20210519_172320_28.png)

`client is not started`

问题原因: zk连接失败，跟问题1属于同一类问题

解决方法：

1. 检查hetu jdbc url的正确性
2. 检查jvm配置（C:\Smartbi\Tomcat\bin\startup.cmd）参数是否正确配置

问题4 使用zk方式连接hetu的时候遇到如下报错：

![20210519_172509_84](assets/smartbi/20210519_172509_84.png)

`cannot locate default realm`

问题原因:  krb5文件未被正常获取

解决办法：

1. 将krb5.conf文件重命名为krb5.ini放置在smartbi主机C:\Windows 目录下
2. 仔细检查hetu jdbc url， 着重检查KerberosConfigPath配置项是否正确

问题5 使用zk方式连接hetu的时候遇到如下报错：

![20210519_172820_61](assets/smartbi/20210519_172820_61.png)

`schema must be specified`

问题原因：配置连接的时候要选择驱动类型为Presto而不是Other

解决办法：更改驱动类型为Presto

问题6 使用用户名密码方式连接hetu的时候遇到如下报错：

![20210521_142230_53](assets/smartbi/20210521_142230_53.png)

`failed to connect xxx:29860`

问题原因：改主机没有部署hsbroker实例

解决办法： 登录manager查看hetu的hsbroker实例之后再填写url


问题7 使用用户名密码方式连接hetu的时候遇到如下报错：

![20210521_142403_64](assets/smartbi/20210521_142403_64.png)

`Get cluster failed for Internal error`

解决办法：检查用户名，密码是否填写正确
