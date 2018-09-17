# Talend对接FusionInsight

## 试用场景

>Talend 7.0.1 <--> FusionInsight HD V100R002C80SPC200(HDFS,HBase组件)
>
>Talend 6.4.1 <--> FusionInsight HD V100R002C80SPC200(hive组件)
>
>注：因为Talend 7.0.1版本bug，HIve组件无法在版本7.0.1中通过，对接hive组件使用Talend 6.4.1版本

## 安装Talend

### 操作场景
安装Talend 7.0.1


### 前提条件
- 已完成FusionInsight HD客户端的安装(可参考产品文档->应用开发指南->安全模式->配置客户端文件)

### 操作步骤

- 配置环境变量JAVA_HOME,Path

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-2018091019055025.png)

- 配置Kerberos认证
向FusionInsight HD集群管理员获取集群Kerberos的krb5.conf文件,把相应的krb5.conf文件重命名为
krb5.ini,并放到`C:\ProgramData\Kerberos`目录中，同时将krb5.ini文件放到`C:\Windows`目录下（Talend默认从此目录下查找）

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20180910191911976.png)

- 下载TOS并修改TOS启动参数
在`https://www.talend.com/products/big-data/big-data-open-studio/`下载TOS，创建连接zookeeper的jaas配置文件（如`C:\developuser\jaas.conf`），内容格式如下：
```
Client {
com.sun.security.auth.module.Krb5LoginModule required
useKeyTab=true
keyTab="c:/developuser/user.keytab"
principal="developuser@HADOOP.COM"
useTicketCache=false
storeKey=true
debug=true;
};
```
- 启动TOS_BD，运行TOS_BD-win-x86_64.exe
  ![](assets/Using_Talend_with_FusionInsight/54f00.png)

  安装必需的第三方库

  ![](assets/Using_Talend_with_FusionInsight/353ca.png)

  ![](assets/Using_Talend_with_FusionInsight/fac07.png)


## Talend连接HDFS

### 操作场景

Talend中配置HDFS解析器，对的FI HD HDFS接口

### 前提条件

  - 已经完成Talend 7.0.1的安装

  - 已完成FusionInsight HD和客户端的安装，包含HDFS组件

### HDFS Connection 操作步骤

  - 添加tHDFSConnection组件，配置如下:

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20180910215644294.png)

  具体配置：
  ```
  1: 选择Cloudera，版本为Cloudera CDH 5.8(YARN mode)
  2: "hdfs://172.21.3.103:25000"
  3: "hdfs/hadoop.hadoop.com@HADOOP.COM"
  4: "developuser"
  5: "C:/developuser/user.keytab"
  6: "hadoop.security.authentication" ->  "kerberos"
       "hadoop.rpc.protection"          ->  "privacy"
  ```
  - 测试结果：

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20180910220104867.png)

### HDFS Get 操作步骤  
  - 整个流程如图所示:

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20180910220302703.png)

  - tHDFSConnection组件配置不变

  - tHDFSGet组件配置如下：

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-2018091022073497.png)

  注意：测试前在集群HDFS文件系统上 `/tmp/talend_test`路径下已经传入文件`out.csv`，`C:/SOFT`为本地输出文件路径

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20180910220900887.png)

  - 测试结果：

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20180910221016973.png)

  到本地路径`C:/SOFT`下查看测试结果

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20180910221132790.png)

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20180910221157828.png)

### HDFS Put 操作步骤

  - 整个流程如图所示:

  - tHDFSConnection组件配置不变

  - tHDFSPut组件配置如下

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-2018091022171376.png)

  注意：测试前在本地目录`C:/SOFT`下创建文件`HDFSPut.txt`, 内容如下：

  ```
  It is create on local PC.
  ```

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20180910221936544.png)

  - 测试结果：

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20180910222014906.png)

  登录到集群查看测试结果：

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20180910222146338.png)







## Talend连接Hive

### 操作场景

Talend中配置JDBC解析器，对的FI HD Hive接口

### 前提条件

- 已经完成Talend 6.4.1的安装

- 已完成FusionInsight HD和客户端的安装，包含Hive组件

### Hive Connection 操作步骤
  - 对接Hive组件Talend版本需要6.4.1

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20180910205919526.png)

  - 整个流程如图所示:

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20180910210133159.png)

  - tHiveConnection组件配置如下
  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20180910210432946.png)
  ```
  1: Custom-Unsuported
  2: Hive2
  3: "172.21.3.103:24002,172.21.3.101:24002,172.21.3.102"
  4: "24002"
  5: "default"
  6: "developuser"
  7: ";serviceDiscoveryMode=zooKeeper;zooKeeperNamespace=hiveserver2;sasl.qop=auth-conf;auth=KERBEROS;principal=hive/hadoop.hadoop.com@HADOOP.COM;user.principal=developuser;user.keytab=C:/SOFT/cfg/user.keytab"
  ```
  注意：需要点击Distritution旁边的按钮来导入FusionInsight HD客户端Hive样例代码中的所有jar包，如果还有缺失的jar包，可用Talend自带的类库进行自动补全，或者也可以手动导入

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-2018091021135320.png)

  - 测试结果：

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20180910211537856.png)

### Hive Create Table & Load 操作步骤
  - tHiveConnection组件配置保持不变

  - tHiveCreateTable组件配置如下

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20180910212248165.png)

  注意：需要点击编辑架构旁边的按钮来配置需要导入hive表的结构

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20180910212352556.png)

  - tHiveCreateTable组件配置如下：

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20180910212551764.png)

  注意：提前需要向hdfs文件存储系统`/tmp/talend_test/`路径下传入文件`out.csv`

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20180910212755601.png)

  `out.csv`文件内容如下：
  ```
  1;EcitQU
  2;Hyy6RC
  3;zju1jR
  4;R9fex9
  5;EU2mVq
  ```



  - tHiveClose组件配置如下:

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20180910212946615.png)

  - 测试结果：

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20180910213034312.png)

  在集群上检查传入的表`createdTableTalend`

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20180910213430414.png)


### Hive Input 操作步骤
  - 整个流程如图所示:

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20180910213731636.png)

  - tHiveConnection组件配置保持不变

  - tHiveInput组件配置如下：

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20180910213852739.png)

  注意：需要点击编辑架构旁边的按钮来配置hive表的结构

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-2018091021394894.png)

  - tLogRow组件使用默认配置

  - tHiveClose组件配置如下

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20180910214111930.png)

  - 测试结果：

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20180910214209444.png)

### Hive Row 操作步骤

  - 整个流程如图所示:

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-2018091021431317.png)

  - tHiveConnection组件配置保持不变

  - tHiveRow组件配置如下

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20180910214413779.png)

  注意：需要点击编辑架构旁边的按钮来配置hive表的结构

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-2018091021452584.png)

  - 测试结果：

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20180910214636560.png)

  连接到集群查看测试结果

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20180910214724875.png)

## Talend连接HBase

### 操作场景

Talend中配置HBase解析器，对的FI HD HBase接口

### 前提条件

- 已经完成Talend 7.0.1的安装

- 已完成FusionInsight HD和客户端的安装，包含HBase组件

### HBase Connection 操作步骤
  - 整个流程如图所示:
  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-2018091210512971.png)
  - 用eclipse导出FusionInsight HD客户端中Hbase样例代码中的LoginUtil类（样例代码路径如`C:\FusionInsightHD\FusionInsight_Services_ClientConfig\HBase\hbase-example`）

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-2018091019371235.png)

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20180910193823624.png)

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20180910194042337.png)

  - 在Talend里插入tHbaseConnection组件，点击组件进行设置

    ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20180910194309989.png)

  - 首先点击tHBaseConnection图标下面的组件按钮，选择版本为`Custom - Unsupported`和`Hadoop 2`，再点击版本旁边的按钮导入jar包，需要导入的是上一步导出的hbase_loginUtil.jar以及FusionInsight HD客户端中Hbase样例代码`hbase-example`中引用的所有jar包，如果还有缺失的jar包，可用Talend自带的类库进行自动补全，或者也可以手动导入

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20180910195417607.png)

  `hbase-example`样例代码中lib目录下所有的jar包如下：

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20180910195736840.png)

  - 使用tLibraryLoad组件导入hbase_loginUtil.jar
  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20180910200204792.png)

  点击 `Advanced settings`在Import中增加`import com.huawei.hadoop.security.LoginUtil;`

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-2018091020034718.png)

  - tHBaseConnection配置如下:
    ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20180910201017467.png)

  - 引入tJava组件用定制代码替代Connection组件
  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20180910201240407.png)

  代码内容如下：
```
  org.apache.hadoop.conf.Configuration conf = org.apache.hadoop.hbase.HBaseConfiguration.create();

  System.setProperty("java.security.krb5.conf", "C:\\developuser\\krb5.conf");
  conf.set("hadoop.security.authentication","Kerberos");

  conf.addResource(new org.apache.hadoop.fs.Path("C:/SOFT/cfg/core-site.xml"));
  conf.addResource(new org.apache.hadoop.fs.Path("C:/SOFT/cfg/hdfs-site.xml"));
  conf.addResource(new org.apache.hadoop.fs.Path("C:/SOFT/cfg/hbase-site.xml"));

  System.out.println("=====");
  System.out.println(org.apache.hadoop.hbase.security.User.isHBaseSecurityEnabled(conf));

  System.setProperty("java.security.auth.login.config", "C:/developuser/jaas.conf");

  LoginUtil.setJaasConf("developuser", "developuser", "C:\\developuser\\krb5.conf");
  LoginUtil.setZookeeperServerPrincipal("zookeeper.server.principal", "zookeeper/hadoop.hadoop.com");

  LoginUtil.login("developuser", "C:/developuser/user.keytab", "C:/developuser/krb5.conf", conf);

  globalMap.put("conn_tHbaseConnection_1", conf);
```
  - 测试结果

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20180910201800687.png)

### HBase Input Output 操作步骤
  - 整个流程如图所示:

    ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20180910202047622.png)

  - tLibraryLoad，tHBaseConnection，tJava配置不变

  - 加入tFileInputDelimited组件配置如下：

    注意需要点击编辑架构旁边的按钮，根据需要存入文件(out.csv)的格式定义列和类型

    ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20180910202521764.png)

    `out.csv`测试数据如下：
    ```
    1;EcitQU
    2;Hyy6RC
    3;zju1jR
    4;R9fex9
    5;EU2mVq
    ```
  - 加入tHBaseOutput组件配置如下：

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20180910203656130.png)

  注意需要点击编辑架构旁边的按钮编辑表的架构：

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20180910203133199.png)

  - tHBaseInput组件配置如下，需要注意的是同样需要点击编辑架构旁边的按钮配置表的结构
  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20180910203818161.png)

  - tLogRow组件使用默认配置

  - 测试结果

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20180910204213486.png)

  检查集群创建的HBase表`hbaseInputOutputTest`

  在集群上使用代码
  ```
  hbase shell
  scan 'hbaseInputOutputTest'
  ```

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20180910204722570.png)

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20180910204755477.png)
