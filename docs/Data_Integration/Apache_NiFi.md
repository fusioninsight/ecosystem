# Connection Instruction between Apache NiFi and FusionInsight


## Succeeded Case
>Apache NiFi 1.7.1 <--> FusionInsight HD V100R002C80SPC200

## Installing Apache NiFi

### Purpose

Installing Apache NiFi 1.7.1

### Prerequisites


  - Installing FusionInsight HD cluster and its client completed

### Procedure

  - Get JAVA_HOME configuration by execute **source** command on client side
  ```
  source /opt/hadoopclient/bigdata_env
  echo $JAVA_HOME
  ```
  ![](assets/Apache_NiFi/markdown-img-paste-20180912165818271.png)



  - Download NiFi installation file from `https://nifi.apache.org/download.html`, move the file to client side by using tool **WinSCP** , execute command `unzip nifi-1.7.1-bin.zip` to unzip the installation file to the following directory `/usr/nifi/nifi-1.7.1`

  ![](assets/Apache_NiFi/markdown-img-paste-20180912160728825.png)



  - Configure NiFi server IP address and port by execute following command `vi /usr/nifi/nifi-1.7.1/conf/nifi.properties` and adjust the propeties within the **nifi.properties** file

    ```
    nifi.web.http.host=172.16.52.190
    nifi.web.http.port=8085
    ```
    ![](assets/Apache_NiFi/markdown-img-paste-20180912163428506.png)

  - Start and Stop NiFi server
    ```
    cd /usr/nifi/nifi-1.7.1
    bin/nifi.sh start
    bin/nifi.sh stop
    ```
    ![](assets/Apache_NiFi/markdown-img-paste-2018091216532553.png)

  - Start NiFi Server

    `bin/nifi.sh start`

    ![](assets/Apache_NiFi/markdown-img-paste-20180912170131362.png)



## Configuring Kerberos authentication within NiFi

### Purpose


Configuring Kerberos authentication within NiFi server for the later connection usage


### Prerequisites

  - Installing Apache NiFi completed

  - Installing FusionInsight HD cluster and its client completed

  - Create a developuser for connection


### Procedure



  - Download the required Kerberos authentication files `user.keytab` and `krb5.conf` from FusionInsight HD Manager site, save the files into the following directory `/opt/developuser`



  - Configure Kerberos authentication  by execute following command `vi /usr/nifi/nifi-1.7.1/conf/nifi.properties` and adjust the propeties within the **nifi.properties** file

    ```
    Detailed Configuration：
    nifi.kerberos.krb5.file=/opt/developuser/krb5.conf
    nifi.kerberos.service.principal=developuser
    nifi.kerberos.service.keytab.location=/opt/developuser/user.keytab
    ```

    ![](assets/Apache_NiFi/markdown-img-paste-20180912171916975.png)


  - Enter NiFi Web UI site, right click on canvas and click on **Configure** icon

    ![](assets/Apache_NiFi/markdown-img-paste-20180912174115953.png)

    Click on **plus** icon to add the service

    ![](assets/Apache_NiFi/markdown-img-paste-20180912174226784.png)

    Find `KeytabCredentialsService` and click **ADD**

  ![](assets/Apache_NiFi/markdown-img-paste-2018091217444348.png)

    Click on **gear** icon to configure

    ![](assets/Apache_NiFi/markdown-img-paste-20180912174747644.png)

    ![](assets/Apache_NiFi/markdown-img-paste-2018091217482271.png)

    Click on **lightning** icon to enable and save the KeytabCredentialsService

    ![](assets/Apache_NiFi/markdown-img-paste-20180912174904147.png)

    ![](assets/Apache_NiFi/markdown-img-paste-20180912175037790.png)

  - Completed
  ![](assets/Apache_NiFi/markdown-img-paste-20180912175122322.png)






## Connecting NiFi to HDFS




### Purpose

Configuring NiFi related HDFS processor to connect FusionInsight HD HDFS



### Prerequisites


  - Installing NiFi 1.7.1 completed

  - Installing FusionInsight HD cluster and its client completed

  - Configuring Kerberos authentication within NiFi completed


### PutHDFS Procedure




  - Find and Copy the `hdfs-site.xml`，`core-site.xml` files which located in FusionInsight HD client to the following directory `/usr/nifi/nifi-1.7.1/conf`


  - Make an adjustment to the content of `hdfs-site.xml` that is to delete the following property
    ```
    <property>
    <name>dfs.client.failover.proxy.provider.hacluster</name>
    <value>org.apache.hadoop.hdfs.server.namenode.ha.BlackListingFailoverProxyProvider</value>
    </property>
    ```

  - Make an adjustment to the content of `core-site.xml` that is to change **halcluster** into detailed namenode ip with its port

    ```
    <property>
    <name>fs.defaultFS</name>
    <value>hdfs://172.21.3.102:25000</value>
    </property>
    ```




  - The whole process shown as the following pic:

  ![](assets/Apache_NiFi/markdown-img-paste-20180912172755668.png)



  - The configuration of processor **GetFile**

  ![](assets/Apache_NiFi/markdown-img-paste-20180912172954559.png)
  ```
  In detail：
  1: /home/dataset
  ```

  - The configuration of processor **PutHDFS**

  ![](assets/Apache_NiFi/markdown-img-paste-20180912181011524.png)

  ![](assets/Apache_NiFi/markdown-img-paste-20180912175441819.png)
  ```
  In detail：
  1: /usr/nifi/nifi-1.7.1/conf/hdfs-site.xml,/usr/nifi/nifi-1.7.1/conf/core-site.xml
  2: Choose KeytabCredentialsService which was completed in previous section
  3: /tmp/nifitest
  ```



  - The configuration of the connection between two former processors

  ![](assets/Apache_NiFi/markdown-img-paste-20180912181110245.png)



  - Move the file `nifiHDFS.csv` into the following directory `/home/dataset` before test start

  ![](assets/Apache_NiFi/markdown-img-paste-2018091218142008.png)

  Content of `nifiHDFS.csv`：
  ```
  1;EcitQU
  2;Hyy6RC
  3;zju1jR
  4;R9fex9
  5;EU2mVq
  ```
  - Test completed

  ![](assets/Apache_NiFi/markdown-img-paste-20180912181628255.png)

  Log into FusionInsight HDFS to check the test outcome by using the following command

  `hdfs dfs -cat /tmp/nifitest/nifiHDFS.csv`

  ![](assets/Apache_NiFi/markdown-img-paste-20180912181828621.png)



### GetHDFS Procedure

  - The whole process shown as the following pic:

  ![](assets/Apache_NiFi/markdown-img-paste-20180913092152648.png)


  - The configuration of processor **GetHDFS**

  ![](assets/Apache_NiFi/markdown-img-paste-20180913093359126.png)

  ```
  In detail：
  1: /usr/nifi/nifi-1.7.1/conf/hdfs-site.xml,/usr/nifi/nifi-1.7.1/conf/core-site.xml
  2: Choose KeytabCredentialsService which was completed in previous section
  3: /tmp/nifitest/HDFS
  ```


  - The configuration of processor **PutFile**

  ![](assets/Apache_NiFi/markdown-img-paste-20180913092500227.png)

  ```
  In detail：
  1: /home/dataset/HDFS
  ```

  ![](assets/Apache_NiFi/markdown-img-paste-20180913092557712.png)



  - Move the file `nifiHDFS.csv` into HDFS directory `/tmp/nifitest/HDFS`

  ![](assets/Apache_NiFi/markdown-img-paste-20180913093946472.png)

  - Test completed

  ![](assets/Apache_NiFi/markdown-img-paste-20180913094130431.png)


  - Log into the FusionInsight HD client side to check the outcome with the directory `/home/dataset/HDFS`

  ![](assets/Apache_NiFi/markdown-img-paste-20180913094327358.png)

### ListHDFS & FetchHDFS Procedure

  - The whole process shown as the following pic:

  ![](assets/Apache_NiFi/markdown-img-paste-20180913101752826.png)


  - The configuration of processor **ListHDFS**

  ![](assets/Apache_NiFi/markdown-img-paste-20180913102631618.png)

  ```
    In detail：
    1. /usr/nifi/nifi-1.7.1/conf/hdfs-site.xml,/usr/nifi/nifi-1.7.1/conf/core-site.xml
    2. KeytabCredentialsService
    3. /tmp/nifitest
  ```



  - The configuration of processor **RouteOnAttribute**

  ![](assets/Apache_NiFi/markdown-img-paste-2018091310313577.png)

  Note: Add one customized property `requiredfilenames` with the value `${filename:matches('sanguo.*')}` by clicking on **plus** icon

  ```
    In detail：
    1. Route to Property name
    2. requiredfilenames
    3. ${filename:matches('sanguo.*')}
  ```


  - The relationship configuration between processor **RouteOnAttribute** and upper processor **FetchHDFS** shown as the following pic

  ![](assets/Apache_NiFi/markdown-img-paste-20180913103920309.png)

  - The relationship configuration between processor **RouteOnAttribute** and lower processor **FetchHDFS** shown as the following pic

  ![](assets/Apache_NiFi/markdown-img-paste-20180913103937690.png)



  - The configuration of processor **FetchHDFS**

  ![](assets/Apache_NiFi/markdown-img-paste-20180913104044399.png)

  ```
    In detail：
    1. /usr/nifi/nifi-1.7.1/conf/hdfs-site.xml,/usr/nifi/nifi-1.7.1/conf/core-site.xml
    2. KeytabCredentialsService
  ```



  - The configuration of upper processor **PutFile**

  ![](assets/Apache_NiFi/markdown-img-paste-20180913104357511.png)

  - The configuration of lower processor **PutFile**

  ![](assets/Apache_NiFi/markdown-img-paste-20180913104436582.png)


  - Check the files on FusionInsight HDFS by executing command `hdfs dfs -ls /tmp/nifitest`

  ![](assets/Apache_NiFi/markdown-img-paste-20180913105052385.png)

  - Test completed

  ![](assets/Apache_NiFi/markdown-img-paste-20180913105440238.png)

  Log into FusionInsight HD client side to check the outcomes separately

  ![](assets/Apache_NiFi/markdown-img-paste-20180913105716184.png)




## Connecting NiFi to Hive

### Purpose


Configuring NiFi Hive processor to connect FusionInsight HD Hive

### Prerequisites

- Installing NiFi 1.7.1 completed

- Installing FusionInsight HD cluster and its client completed

- Configuring Kerberos authentication within NiFi completed

### HiveConnectionPool Procedure



- Enter NiFi Web UI site, right click on canvas and click on **Configure** icon

  ![](assets/Apache_NiFi/markdown-img-paste-20180912174115953.png)


- Click on **plus** icon to add the service

    ![](assets/Apache_NiFi/markdown-img-paste-20180912174226784.png)



- Find `HiveConnectionPool` and click **ADD**

 ![](assets/Apache_NiFi/markdown-img-paste-20180913111208294.png)



- Click on **gear** icon to configure

 ![](assets/Apache_NiFi/markdown-img-paste-20180913111250616.png)

 ![](assets/Apache_NiFi/markdown-img-paste-20180913111431623.png)

 ```
 In detail
 1: jdbc:hive2://172.21.3.103:24002,172.21.3.101:24002,172.21.3.102:24002/;serviceDiscoveryMode=zooKeeper;principal=hive/hadoop.hadoop.com@HADOOP.COM
 2: KeytabCredentialsService
 ```

- Click on **lightning** icon to enable and save the HiveConnectionPool

  ![](assets/Apache_NiFi/markdown-img-paste-20180913111645264.png)

  ![](assets/Apache_NiFi/markdown-img-paste-20180913111723364.png)

- Completed
  ![](assets/Apache_NiFi/markdown-img-paste-20180913111757929.png)



- Create `jaas.conf` file which located at directory `/usr/nifi/nifi-1.7.1/conf` wit the following content

  ```
  Client {
  com.sun.security.auth.module.Krb5LoginModule required
  useKeyTab=true
  keyTab="/opt/developuser/user.keytab"
  principal="developuser"
  useTicketCache=false
  storeKey=true
  debug=true;
  };
  ```




- Make an adjustment to the `bootstrap.conf` file by executing following command `vi /usr/nifi/nifi-1.7.1/conf/bootstrap.conf`

  ![](assets/Apache_NiFi/markdown-img-paste-20180913114735353.png)

  ```
  java.arg.17=-Djava.security.auth.login.config=/usr/nifi/nifi-1.7.1/conf/jaas.conf
  java.arg.18=-Dsun.security.krb5.debug=true
  ```



- Make an adjustment to the `nifi.properties` file by executing following command `vi /usr/nifi/nifi-1.7.1/conf/nifi.properties`

  ![](assets/Apache_NiFi/markdown-img-paste-20180913115135676.png)

  ```
  nifi.zookeeper.auth.type=sasl
  nifi.zookeeper.kerberos.removeHostFromPrincipal=true
  nifi.zookeeper.kerberos.removeRealmFromPrincipal=true
  ```




- Execute the following command to come into the directory of  NiFi Hive related library
 `cd /usr/nifi/nifi-1.7.1/work/nar/extensions/nifi-hive-nar-1.7.1.nar-unpacked/META-INF/bundled-dependencies`

 Substitute `zookeeper-3.5.1.jar` which is from FusionInsight HD client side for the original `zookeeper-3.4.6.jar`

  ![](assets/Apache_NiFi/markdown-img-paste-2018091312062027.png)



### SelectHiveQL read Hive table Procedure

 - The whole process shown as the following pic:

 ![](assets/Apache_NiFi/markdown-img-paste-2018091312072896.png)


 - The configuration of processor **SelectHiveQL**

 ![](assets/Apache_NiFi/markdown-img-paste-20180913120825568.png)

 ![](assets/Apache_NiFi/markdown-img-paste-20180913121003311.png)

 ```
  In detail：
  1: HiveConnectionPool
  2: select * from default.t2
  3. CSV
 ```


 - The configuration of processor **PutFile**

 ![](assets/Apache_NiFi/markdown-img-paste-20180913121155239.png)



 - Log into FusionInsight cluster to check table **t2** on hive

 ![](assets/Apache_NiFi/markdown-img-paste-2018091312133394.png)

 - Completed

 ![](assets/Apache_NiFi/markdown-img-paste-20180913121452543.png)

 Check the outcome by log into the following directory `/home/dataset/HIVE`

 ![](assets/Apache_NiFi/markdown-img-paste-20180913121747276.png)

### PutHiveQL load whole table Procedure

  - The whole process shown as the following pic:

  ![](assets/Apache_NiFi/markdown-img-paste-20180913144112926.png)


  - The configuration of processor **GetFile**

  ![](assets/Apache_NiFi/markdown-img-paste-20180913145536626.png)

  ```
    In detail：
    1： /home/dataset/
    2: iris.txt
  ```
  Content of `iris.txt`：

  ```
    1,5.1,3.5,1.4,0.2,setosa
    2,4.9,3,1.4,0.2,setosa
    3,4.7,3.2,1.3,0.2,setosa
    4,4.6,3.1,1.5,0.2,setosa
    5,5,3.6,1.4,0.2,setosa
    6,5.4,3.9,1.7,0.4,setosa
    7,4.6,3.4,1.4,0.3,setosa
    8,5,3.4,1.5,0.2,setosa
    9,4.4,2.9,1.4,0.2,setosa
    10,4.9,3.1,1.5,0.1,setosa
  ```


  - The configuration of processor **PutHDFS**

  ![](assets/Apache_NiFi/markdown-img-paste-20180913145851111.png)

  ```
    In detail：
    1： /usr/nifi/nifi-1.7.1/conf/hdfs-site.xml,/usr/nifi/nifi-1.7.1/conf/core-site.xml
    2： KeytabCredentialsService
    3: /tmp/nifitest/loadhive
  ```


  - The configuration of processor **ReplaceText**

  ![](assets/Apache_NiFi/markdown-img-paste-20180913150058238.png)

  ```
    In detail：
    1: CREATE TABLE IF NOT EXISTS iris_createdBy_NiFi ( ID string, sepallength FLOAT, sepalwidth FLOAT, petallength FLOAT, petalwidth FLOAT, species string ) ROW FORMAT DELIMITED FIELDS TERMINATED BY ',' STORED AS TEXTFILE;LOAD DATA INPATH "hdfs:///tmp/nifitest/loadhive/iris.txt" into table iris_createdBy_NiFi;
  ```


  - The configuration of processor **PutHiveQL**

  ![](assets/Apache_NiFi/markdown-img-paste-20180913150303396.png)



  - Move the data file `iris.txt` into the following directory `/home/dataset/` before test

  ![](assets/Apache_NiFi/markdown-img-paste-20180913150605645.png)



  - Completed:

  ![](assets/Apache_NiFi/markdown-img-paste-20180913144501255.png)

  Login the HIVE to check the test outcome

  ![](assets/Apache_NiFi/markdown-img-paste-20180913145436548.png)

### PutHiveQL Load the table by rows Procedure


  - The whole process shown as the following pic:

  ![](assets/Apache_NiFi/markdown-img-paste-20180913152255705.png)



  - The configuration of processor **GetFile**

  ![](assets/Apache_NiFi/markdown-img-paste-20180913154003755.png)

  ```
    In detail：
    1： /home/dataset/
    2： iris_add.txt
  ```

  Content of `iris_add.txt`：

  ```
  "11",5.8,2.8,5.1,2.4,"virginica"
  "12",6.4,3.2,5.3,2.3,"virginica"
  "13",6.5,3,5.5,1.8,"virginica"
  "14",5.7,3,4.2,1.2,"versicolor"
  "15",5.7,2.9,4.2,1.3,"versicolor"
  ```


  - The configuration of processor **SplitText**

  ![](assets/Apache_NiFi/markdown-img-paste-20180913154219909.png)


  - There is no change for the configuration of  processor **ExtractText**



  - The configuration of processor **ReplaceText**

  ![](assets/Apache_NiFi/markdown-img-paste-20180913154421612.png)


  - The configuration of processor **PutHiveQL**

  ![](assets/Apache_NiFi/markdown-img-paste-20180913154500461.png)



  - Move the data file `iris_add.txt` into the following directory `/home/dataset/` before test

  ![](assets/Apache_NiFi/markdown-img-paste-20180913152411341.png)

  - Completed：

  ![](assets/Apache_NiFi/markdown-img-paste-2018091315371178.png)

  Login the HIVE to check the test outcome：

  ![](assets/Apache_NiFi/markdown-img-paste-20180913153858212.png)



## Connecting NiFi to HBase

### Purpose


Configuring NiFi HBase processor to connect FusionInsight HD HBase

### Prerequisites

- Installing NiFi 1.7.1 completed

- Installing FusionInsight HD cluster and its client completed

- Configuring Kerberos authentication within NiFi completed

### HBase_1_1_2_ClientService Procedure


  - Move the hbase related configuration file `hbase-site.xml` which is within the FusionInsight HD client side into the following directory `/usr/nifi/nifi-1.7.1/conf`



  - Execute the following command to come into the directory of  NiFi HBase related library
   `cd /usr/nifi/nifi-1.7.1/work/nar/extensions/nifi-hbase_1_1_2-client-service-nar-1.7.1.nar-unpacked/META-INF/bundled-dependencies`

   Substitute `zookeeper-3.5.1.jar` which is from FusionInsight HD client side for the original `zookeeper-3.4.6.jar`

  ![](assets/Apache_NiFi/markdown-img-paste-20180913171320336.png)



  - Enter NiFi Web UI site, right click on canvas and click on **Configure** icon

  ![](assets/Apache_NiFi/markdown-img-paste-20180912174115953.png)


  - Click on **plus** icon to add the service

  ![](assets/Apache_NiFi/markdown-img-paste-20180913160637627.png)



  - Find `HBase_1_1_2_ClientService` and click **ADD**

  ![](assets/Apache_NiFi/markdown-img-paste-20180913160755948.png)

  - Click on **gear** icon to configure

  ![](assets/Apache_NiFi/markdown-img-paste-20180913160901190.png)

  ![](assets/Apache_NiFi/markdown-img-paste-20180913161005377.png)

  ```
    In detail：
    1： /usr/nifi/nifi-1.7.1/conf/hbase-site.xml,/usr/nifi/nifi-1.7.1/conf/core-site.xml
    2： KeytabCredentialsService
  ```

  - Click on **lightining** icon to enable and save the `HBase_1_1_2_ClientService`

  ![](assets/Apache_NiFi/markdown-img-paste-20180913161345190.png)

  ![](assets/Apache_NiFi/markdown-img-paste-20180913161410495.png)

  - Completed

  ![](assets/Apache_NiFi/markdown-img-paste-20180913161431790.png)

### PutHBaseJSON load the table Procedure


 - The whole process shown as the following pic:

 ![](assets/Apache_NiFi/markdown-img-paste-20180913170925283.png)



 - The configuration of  processor **GetFile**

 ![](assets/Apache_NiFi/markdown-img-paste-20180913171543197.png)

 Content of `hbase_test.csv`：
  ```
  1,5.1,3.5,setosa
  2,6.1,3.6,versicolor
  3,7.1,3.7,virginica
  ```


  - The configuration of processor **InverAvroSchema**

  ![](assets/Apache_NiFi/markdown-img-paste-20180913171824677.png)

  ```
    In detail：
    1: flowfile-attribute
    2: csv
    3: false
    4: hbase_test_data
  ```



  - The configuration of  processor **ConvertCSVToAvro**

  ![](assets/Apache_NiFi/markdown-img-paste-20180913172037547.png)


  - The configuration of  processor **ConvertAvroToJSON**

  ![](assets/Apache_NiFi/markdown-img-paste-20180913172201962.png)


  - The configuration of  processor **SplitJson**

  ![](assets/Apache_NiFi/markdown-img-paste-20180913172242678.png)


  - The configuration of  processor **PutHBaseJSON**

  ![](assets/Apache_NiFi/markdown-img-paste-20180913172428386.png)

  ```
    In detail:
    1: HBase_1_1_2_ClientService
    2: hbase_test
    3: ${UUID()}
    4: data
  ```



  - Move the data file `hbase_test.csv` into the following directory `/home/dataset/HBASE` before test

  ![](assets/Apache_NiFi/markdown-img-paste-20180913173017728.png)

  In addition, execute following command to create a HBase table

  ```
  hbase shell
  create 'HBase_test','data'
  ```

  ![](assets/Apache_NiFi/markdown-img-paste-20180913173220335.png)

- Completed：

  ![](assets/Apache_NiFi/markdown-img-paste-20180913173552752.png)

  Login into the FusionInsight HD cluster to check the outcome:

  ![](assets/Apache_NiFi/markdown-img-paste-20180913173626203.png)

### GetHbase Procedure

- The whole process shown as the following pic:

  ![](assets/Apache_NiFi/markdown-img-paste-20180913174304746.png)


- The configuration of  processor **GetHBase**

  ![](assets/Apache_NiFi/markdown-img-paste-20180913190606193.png)


- The configuration of  processor **PutFile**

  ![](assets/Apache_NiFi/markdown-img-paste-20180913191235986.png)


- Completed

  ![](assets/Apache_NiFi/markdown-img-paste-20180913184243562.png)

  Login into the following directory `/home/dataset/GetHBase_test` to check the test outcome

  ![](assets/Apache_NiFi/markdown-img-paste-20180913191317245.png)

  ![](assets/Apache_NiFi/markdown-img-paste-2018091319144054.png)




## Connecting NiFi to Spark

### Purpose


Configuring NiFi Livy Session processor to connect FusionInsight HD Spark

### Prerequisites

- Installing NiFi 1.7.1 completed

- Installing FusionInsight HD cluster and its client completed

- Configuring Kerberos authentication within NiFi completed

- Installing and configuring Apache Livy 0.5.0 (Apache Livy can be installed on test host or any other host as long as they can connect to each other including FusionInsight HD cluster)
> There exist connection instruction between Apache Livy and FusionInsight, please check the FusionInsight ecosystem



### LivySessionController Procedure



- Enter NiFi Web UI site, right click on canvas and click on **Configure** icon

  ![](assets/Apache_NiFi/markdown-img-paste-20180912174115953.png)


- Click on **plus** icon to add the service

  ![](assets/Apache_NiFi/markdown-img-paste-20180914104314385.png)


- Find `LivySessionController` and click **ADD**

  ![](assets/Apache_NiFi/markdown-img-paste-20180914104402554.png)


  - Click on **gear** icon to configure

  ![](assets/Apache_NiFi/markdown-img-paste-20180914104516173.png)

  ![](assets/Apache_NiFi/markdown-img-paste-20180914104844258.png)

  ```
  In detail：
  1: 172.21.3.43 (host ip for Apache Livy)
  2: 8998 (Livy default port, can be changed)
  3: spark
  4：KeytabCredentialsService
  ```

- Click on **plus** icon to add the service

  ![](assets/Apache_NiFi/markdown-img-paste-20180914105344782.png)

- Find `LivySessionController` and click **ADD**

- Click on **gear** icon to configure

![](assets/Apache_NiFi/markdown-img-paste-20180914105516219.png)

  Change the name of Controller as LivySessionController_PySpark

  ![](assets/Apache_NiFi/markdown-img-paste-20180914105554293.png)

  ![](assets/Apache_NiFi/markdown-img-paste-20180914105725237.png)

  ```
  In detail：
  1: 172.21.3.43 (host ip for Apache Livy)
  2: 8998 (Livy default port, can be changed)
  3: pysaprk
  4：KeytabCredentialsService
  ```



- Click on **plus** icon to add the service



- Find `LivySessionController` and click **ADD**



- Click on **gear** icon to configure

  ![](assets/Apache_NiFi/markdown-img-paste-20180914110218855.png)

  Change the name of Controller as LivySessionController_SparkR

  ![](assets/Apache_NiFi/markdown-img-paste-20180914110258706.png)

  ![](assets/Apache_NiFi/markdown-img-paste-2018091411040356.png)

  ```
  In detail：
  1: 172.21.3.43 (host ip for Apache Livy)
  2: 8998 (Livy default port, can be changed)
  3: sparkr
  4：KeytabCredentialsService
  ```



- Click on **lightining** icon to enable and save the `LivySessionController`,`LivySessionController_PySpark`,`LivySessionController_SparkR`

  ![](assets/Apache_NiFi/markdown-img-paste-20180914110556918.png)

- Completed

  ![](assets/Apache_NiFi/markdown-img-paste-20180914110811125.png)


### Spark Sample Procedure

- The whole process shown as the following pic:

  ![](assets/Apache_NiFi/markdown-img-paste-2018091411101510.png)


- The configuration of  processor **GetFile**

  ![](assets/Apache_NiFi/markdown-img-paste-20180914111127853.png)

  ```
  In detail：
  1: /home/dataset/sparkTest
  2: code1.txt
  ```

  Content of `code1.txt`：
  ```
  1+2
  ```

- The configuration of  processor **ExtractText**

  Click **plus** icon to add a Property `code1` with its Value as `$`

  ![](assets/Apache_NiFi/markdown-img-paste-20180914111426277.png)

- The configuration of  processor **ExecuteSparkInteractive**

  ![](assets/Apache_NiFi/markdown-img-paste-20180914112305396.png)

  ```
  In detail：
  1: LivySessionController
  2: ${code1}
  ```


- Move the code file `code1.txt` into the following directory `/home/dataset/sparkTest` before test

  ![](assets/Apache_NiFi/markdown-img-paste-20180914112519376.png)

  Start the Livy server

  ![](assets/Apache_NiFi/markdown-img-paste-20180914112716361.png)

  ![](assets/Apache_NiFi/markdown-img-paste-20180914112911901.png)

- Completed：

  ![](assets/Apache_NiFi/markdown-img-paste-20180914113055754.png)

  Log into the Livy server to check the outcome

  ![](assets/Apache_NiFi/markdown-img-paste-2018091411321312.png)

  ![](assets/Apache_NiFi/markdown-img-paste-20180914113341172.png)


### PySpark Sample Procedure


- The whole process shown as the following pic:

  ![](assets/Apache_NiFi/markdown-img-paste-2018091411101510.png)


- The configuration of  processor **GetFile**

  ![](assets/Apache_NiFi/markdown-img-paste-20180914113725733.png)
  ```
  In detail：
  1: /home/dataset/sparkTest
  2: code2.txt
  ```

  Content of `code2.txt`：
  ```
  import random
  NUM_SAMPLES = 100000
  def sample(p):
    x, y = random.random(), random.random()
    return 1 if x*x + y*y < 1 else 0

  count = sc.parallelize(xrange(0, NUM_SAMPLES)).map(sample).reduce(lambda a, b: a + b)
  print "Pi is roughly %f" % (4.0 * count / NUM_SAMPLES)
  ```

- The configuration of  processor **ExtractText**

  Click **plus** icon to add a Property `code2` with its Value as `$`

  ![](assets/Apache_NiFi/markdown-img-paste-20180914114234899.png)


- The configuration of  processor **ExecuteSparkInteractive**

  ![](assets/Apache_NiFi/markdown-img-paste-20180914114342416.png)

  ```
  In detail：
  1: LivySessionController_PySpark
  2: ${code2}
  ```

- Move the code file `code2.txt` into the following directory `/home/dataset/sparkTest` before test

  ![](assets/Apache_NiFi/markdown-img-paste-20180914114447497.png)

  Start the Livy server

- Completed

  ![](assets/Apache_NiFi/markdown-img-paste-20180914114625750.png)

  Log into the Livy server to check the outcome

  ![](assets/Apache_NiFi/markdown-img-paste-20180914114724621.png)

  ![](assets/Apache_NiFi/markdown-img-paste-20180914114751933.png)

### SparkR Sample Procedure

  - The whole process shown as the following pic:

  ![](assets/Apache_NiFi/markdown-img-paste-20180914141610219.png)

  Note: It's different by comparing to example of former Spark and PySpark


  - The configuration of  processor **GetFile**

    ![](assets/Apache_NiFi/markdown-img-paste-20180914115049131.png)

    ```
    In detail：
    1: /home/dataset/sparkTest
    2: code3.txt
    ```

    Content of `code3.txt`：
    ```
    piR <- function(N) {
        x <- runif(N)
        y <- runif(N)
        d <- sqrt(x^2 + y^2)
        return(4 * sum(d < 1.0) / N)
    }

    set.seed(5)
    cat("Pi is roughly ",piR(1000000) )
    ```



  - The configuration of  processor **ExecuteSparkInteractive**

    ![](assets/Apache_NiFi/markdown-img-paste-2018091414274959.png)
  ```
  In detail：
  1: /home/dataset/sparkTest
  2: code content of code3.txt
  ```


  - Move the code file `code3.txt` into the following directory `/home/dataset/sparkTest` before test

    ![](assets/Apache_NiFi/markdown-img-paste-20180914115356781.png)

    Start the Livy server

- Completed

  ![](assets/Apache_NiFi/markdown-img-paste-20180914142925868.png)

  Log into the Livy server to check the outcome

  ![](assets/Apache_NiFi/markdown-img-paste-20180914115708776.png)

  ![](assets/Apache_NiFi/markdown-img-paste-20180914143029264.png)


## Connecting NiFi to Kafka

### Purpose


Configuring NiFi Kafka processor to connect FusionInsight HD Kafka

### Prerequisites

- Installing NiFi 1.7.1 completed

- Installing FusionInsight HD cluster and its client completed

- Configuring Kerberos authentication within NiFi completed



### GetHTTP & PutKafka Procedure


 - The whole process shown as the following pic:

  ![](assets/Apache_NiFi/markdown-img-paste-20180914143620412.png)

 - The configuration of  processor **GetHTTP**

  ![](assets/Apache_NiFi/markdown-img-paste-20180914143715990.png)

  ```
  In detail：
  1: http://vincentarelbundock.github.io/Rdatasets/csv/datasets/iris.csv
  2: iris.csv
  ```

 - The configuration of  processor **PutKafka**
  ![](assets/Apache_NiFi/markdown-img-paste-20180914143959710.png)

  ```
  In detail：
  1： 172.21.3.102:21005,172.21.3.101:21005,172.21.3.103:21005
  2： nifi-kafka-test-demo
  3： nifi
  ```

- Before test：

  Log into the Kafka component within FusionInsightHD client side and create a Topic **nifi-kafka-test-demo**

  ```
  cd /opt/hadoopclient/Kafka/kafka/bin
  kafka-topics.sh --create --topic nifi-kafka-test-demo --zookeeper 172.21.3.101:24002,172.21.3.102:24002,172.21.3.103:24002/kafka --partitions 1 --replication-factor 1
  ```  

  ![](assets/Apache_NiFi/markdown-img-paste-20180914144534988.png)

- Completed：

  ![](assets/Apache_NiFi/markdown-img-paste-20180914144743959.png)

  Log into the kafka component within FusionInsightHD client side to check the outcome

  ```
  cd /opt/hadoopclient/Kafka/kafka/bin
  kafka-console-consumer.sh --zookeeper 172.21.3.101:24002,172.21.3.102:24002,172.21.3.103:24002/kafka --topic nifi-kafka-test-demo --from-beginning
  ```

  ![](assets/Apache_NiFi/markdown-img-paste-20180914144952375.png)

  ![](assets/Apache_NiFi/markdown-img-paste-20180914145017685.png)


### ConsumeKafka_0_11 Procedure


  - The whole process shown as the following pic:

  ![](assets/Apache_NiFi/markdown-img-paste-20180914150029910.png)


  - The configuration of  processor **ConsumeKafka_0_11**

  ![](assets/Apache_NiFi/markdown-img-paste-20180914145920740.png)

  ```
  1: 172.21.3.101:21005,172.21.3.102:21005,172.21.3.103:21005
  2: PLAINTEXT
  3: KeytabCredentialsService
  4: Kafka
  5: example-metric1
  6: DemoConsumer
  ```


  - The configuration of  processor **PutFile**

  ![](assets/Apache_NiFi/markdown-img-paste-20180914150709577.png)

  - Before test：

    Open the `kafka-examples` which provided by FusionInsightHD client in eclipse, configure the `kafka-examples` so that it can be successfully ran and produce messages to kafka

    ![](assets/Apache_NiFi/markdown-img-paste-20180914151040410.png)

    Note: There must be a producer when testing the NiFi ConsumeKafka_0_11 processor, run `NewProducer.java` within `kafka-examples` at first and then start to test NiFi ConsumeKafka_0_11

  - Completed：

    ![](assets/Apache_NiFi/markdown-img-paste-20180914151449716.png)

    Log into the follow directory `/home/dataset/Kafka` to check the test outcome

    ![](assets/Apache_NiFi/markdown-img-paste-20180914151628975.png)

    ![](assets/Apache_NiFi/markdown-img-paste-20180914151713272.png)
