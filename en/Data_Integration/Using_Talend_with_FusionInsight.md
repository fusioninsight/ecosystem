
# Connection Instruction between Talend and FusionInsight


## Succeeded Case
>Talend 7.0.1 <--> FusionInsight HD V100R002C80SPC200(HDFS,HBase Component)
>
>Talend 6.4.1 <--> FusionInsight HD V100R002C80SPC200(HDFS,HBase,Hive)
>
>Note: Because of the version bug of Talend 7.0.1, Hive cannot be successfully connected. Using Talend 6.4.1 for substitution.

## Installing Talend

### Purpose
Installing Talend 7.0.1


### Prerequisites

- Installing FusionInsight HD cluster and its client completed

### Procedure



- Configure the JAVA_HOME into Path Environment Variables

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-2018091019055025.png)

- Configure Kerberos

  Get Kerberos related userkeytab and krb5.conf files by login into the FusionInsight HD manager web UI and put them into the following directory `C:\ProgramData\Kerberos`. In addition, create a new file named krb5.ini with the same content of krb5.conf, put the krb5.ini file into the following directory `C:\Windows`

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20180910191911976.png)



- Download TOS from the following web pages `https://www.talend.com/products/big-data/big-data-open-studio/` , create the jaas.conf file for zookeeper connection with its content shown as follows

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

- Sart TOS_BD by clicking `TOS_BD-win-x86_64.exe`

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20181027103716507.png)

  Installing additional Talend Packages

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20181027103930508.png)

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20181027104029626.png)



## Connecting Talend to HDFS

### Purpose

Configuring Talend related HDFS processor to connect FusionInsight HD HDFS

### Prerequisites

  - Installing Talend 7.0.1 completed

  - Installing FusionInsight HD cluster and its client completed


### HDFS Connection Procedure


  - Add the tHDFSConnection component with its configuration shown as follows:

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20181027111926482.png)

  In detail：
  ```
  1: Cloudera CDH 5.8(YARN mode)
  2: "hdfs://172.21.3.103:25000"
  3: "hdfs/hadoop.hadoop.com@HADOOP.COM"
  4: "developuser"
  5: "C:/developuser/user.keytab"
  6: "hadoop.security.authentication" ->  "kerberos"
       "hadoop.rpc.protection"          ->  "privacy"
  ```
  - Test completed：

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20181027112247960.png)



### HDFS Get Procedure  
  - The whole process shown as the following pic:

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20180910220302703.png)

  - The configuration of **tHDFSConnection** component does not change


  - The configuration of **tHDFSGet** component shown as follows

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20181027112848678.png)


  Note: Put the `out.csv` into the HDFS filesystem with the following directory `/tmp/talend_test`, `C:/SOFT` is the local folder for file output

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20180910220900887.png)

  - TEST completed：

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20181027113549569.png)


  Check the test outcome by coming into the local directory `C:/SOFT`

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20181027113744223.png)

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20180910221157828.png)

### HDFS Put Procedure

  - The whole process shown as the following pic:

  - The configuration of **tHDFSConnection** component does not change

  - The configuration of **tHDFSPut** component shown as follows

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20181027114148747.png)



  Note: Before test starts, create `HDFSPut.txt` located at the directory `C:/SOFT` with its content shown as follows

  ```
  It is create on local PC.
  ```

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20180910221936544.png)

  - Test Completed：

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-2018102711460775.png)


  Login into the cluster to check the test outcome:

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20180910222146338.png)








## Connecting Talend to Hive

### Purpose

Configuring Talend related Hive processor to connect FusionInsight HD Hive


### Prerequisites

- Installing Talend 6.4.1 completed

- Installing FusionInsight HD cluster and its client completed


### Hive Connection Procedure

  - The Talend version for Hive connection is 6.4.1

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20181027141810509.png)


  - The whole process shown as the following pic:

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20180910210133159.png)


  - The configuration of **tHiveConnection** component shown as follows


  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20181027142536795.png)

  ```
  1: Custom-Unsuported
  2: Hive2
  3: "172.21.3.103:24002,172.21.3.101:24002,172.21.3.102"
  4: "24002"
  5: "default"
  6: "developuser"
  7: ";serviceDiscoveryMode=zooKeeper;zooKeeperNamespace=hiveserver2;sasl.qop=auth-conf;auth=KERBEROS;principal=hive/hadoop.hadoop.com@HADOOP.COM;user.principal=developuser;user.keytab=C:/SOFT/cfg/user.keytab"
  ```


  Note: Need to click the button which besides the **Distribution** to import the required jar files of FusionInsight HD. If there still need to add extra jar files, you can complete this step either by Talend itself or manually add these jar files.

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20181027143157715.png)



  - Test Completed：

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20181027143538549.png)

### Hive Create Table & Load Procedure


  - The configuration of **tHiveConnection** component does not change

  - The configuration of **tHiveCreateTable** component shown as follows

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20181027144138537.png)


  Note: It is required to **Edit schema** of the table

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-2018102714423808.png)


  - The configuration of **tHiveLoad** component shown as follows


  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20181027144612401.png)


  Note: Before test starts, the file `out.csv` need to be uploaded into the hdfs filesystem directory `/tmp/talend_test/`

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20180910212755601.png)

  The content of `out.csv` shown as follows
  ：
  ```
  1;EcitQU
  2;Hyy6RC
  3;zju1jR
  4;R9fex9
  5;EU2mVq
  ```



  - The configuration of **tHiveClose** component shown as follows

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20181027144750903.png)

  - Test Completed：

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20181027144955115.png)


  Check the table `createdTableTalend` by login into the cluster

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20180910213430414.png)


### Hive Input Procedure


  - The whole process shown as the following pic:

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20180910213731636.png)

  - The configuration of **tHiveConnection** component does not change


  - The configuration of **tHiveInput** component shown as follows

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20181027145453118.png)




  Note: It is required to **Edit schema** of the hive table


  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20181027145630808.png)


  - The configuration of **tLogRow** keeps by default

  - The configuration of **tHiveClose** component shown as follows

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20181027145824481.png)

  - Test Completed：

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20181027150004526.png)

### Hive Row Procedure

  - The whole process shown as the following pic:

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-2018091021431317.png)


  - The configuration of **tHiveConnection** component does not change


  - The configuration of **tHiveRow** component shown as follows

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20181027150354934.png)


  Note: It is required to **Edit schema** of hive table

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20181027150458443.png)


  - Test Completed：

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20181027150645583.png)


  Check the cluster outcome by login into the FusionInsight Cluster

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20181027150815520.png)

## Connecting Talend to HBase

### Purpose

Configuring Talend related HBase processor to connect FusionInsight HD HBase

### Prerequisites

- Installing Talend 7.0.1 completed

- Installing FusionInsight HD cluster and its client completed

### HBase Connection Procedure

  - The whole process shown as the following pic:

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-2018091210512971.png)


  - Using **eclipse** to export the **LoginUtil** which from HBase sample project code of FusionInsight HD client (Sample project code in this time can be found by following directory `C:\FusionInsightHD\FusionInsight_Services_ClientConfig\HBase\hbase-example`)

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-2018091019371235.png)

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20180910193823624.png)

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20180910194042337.png)


  - Find the **tHbaseConnection** component by Palette

    ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20180910194309989.png)



  - The configuration of **tHbaseConnection** shown as following pic:

  Note: It is required to import the jar files of HBase sample project and the exported  **hbase_loginUtil.jar**

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20181027155307109.png)

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20181027155451471.png)

  `hbase-example` required jar faile can be located by the following directory `C:\FusionInsight_Services_ClientConfig\HBase\FusionInsight-HBase-1.0.2.tar.gz\hbase\lib`

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20181027153644203.png)


  - The configuration of **tLibraryLoad** shown as folloing pic:

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20181027154254960.png)


  Click on `Advanced settings` and add the java code `import com.huawei.hadoop.security.LoginUtil;` shown as follows:

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20181027155002101.png)


  - Use **tJava** component to customize the **tHBaseConnection** component

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-2018102716005417.png)

  The content of the Java code shown as follows：
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
  - Test Completed

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20181027160217311.png)

### HBase Input Output Procedure
  - The content of the Java code shown as follows：

    ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20180910202047622.png)

  - The configuration of **tLibraryLoad**，**tHBaseConnection**，**tJava**, **tHBaseClose** do not change



  - The configuration of **tFileInputDelimited** shown as following pic:

    Note: It is required to **Edit schema** of `out.csv`

    ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20181027161437375.png)

    The content of `out.csv` shown as follows:
    ```
    1;EcitQU
    2;Hyy6RC
    3;zju1jR
    4;R9fex9
    5;EU2mVq
    ```

  - The configuration of **tHBaseOutput** shown as folloing pic:

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20181027161745118.png)




  Note: It is required to **Edit Schema** of table:

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20181027162050223.png)




  - The configuration of **tHBaseInput** shown as folloing pic:

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20181027162342379.png)



  - The configuration of **tLogRow** keeps by default

  - Test Completed:

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20181027163025959.png)



  Login into the FusinInsight HD cluster and check the HBase table `hbaseInputOutputTest` by using following comands:

  ```
  hbase shell
  scan 'hbaseInputOutputTest'
  ```

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20180910204722570.png)

  ![](assets/Using_Talend_with_FusionInsight/markdown-img-paste-20180910204755477.png)
