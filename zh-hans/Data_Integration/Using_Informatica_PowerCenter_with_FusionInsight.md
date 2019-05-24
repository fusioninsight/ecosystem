# Informatica PowerCenter 对接FusionInsight HD

## 适用场景

> Informatica PowerCenter 10.2.0 <--> FusionInsight HD 6.5

## 环境信息

* Informatica Server 10.2.0 Linux
* Informatica PowerCenter Client 10.2.0
* Oracle database 11g
* FusionInsight HD 客户端

## 部署方案
* 一台Linux服务器，部署Informatica Server，并安装FusionInsight HD客户端
* 一台Windows机器，安装Informatica PowerCenter Client

## 环境准备
### 安装FusionInsight HD客户端
  * 安装FusionInsight客户端，安装目录为/opt/hadoopclient

  * 通过FusionInsight HD的管理页面创建一个“人机”用户，具体请参见《FusionInsight HD管理员指南》的 **创建用户** 章节。例如，创建用户developuser，并赋予HDFS,Hive所有权限，下载对应的秘钥文件,将krb5.conf文件上传到客户端节点的`/opt/`目录下

### 在Linux上安装Oracle database 以及 Informatica Server

  * 创建oracle 用户，安装oracle 数据库
  * 创建infa用户，使用`sqlplus / as sysdba`登录至oracle数据库中，执行以下sql语句
    ```sql
    create tablespace rep_data datafile '/u01/app/oracle/oradata/orcl/rep_data_01.dbf' size 512m ;
    create user pwc_user identified by pwc_user default tablespace rep_data temporary tablespace temp;
    create user mdl_user identified by mdl_user default tablespace rep_data temporary tablespace temp;
    create user domain_user identified by domain_user default tablespace rep_data temporary tablespace temp;
    grant dba to  domain_user,pwc_user,mdl_user;
    ```
  * 获取Informatica Server安装包并上传至节点,解压安装包之后，执行`./install.sh`，根据提示进行安装,这里安装目录为`/home/infa/Informatica/10.2.0`.
  * 安装完成后,Informatica Server会自行启动，在浏览器输入ip:6008端口，打开Administrator 管理界面，输入安装时设置的用户名密码进行登录。

### Informatica Server配置
  * 创建PowerCenter 存储库
    - 在管理界面，domain下右键新建一个PowerCenter 存储库

      ![](assets/Using_Informatica_PowerCenter_with_FusionInsight/ded4c.png)
    - 指定名称等信息，下一步

      ![](assets/Using_Informatica_PowerCenter_with_FusionInsight/2db2a.png)
    - 指定数据库信息，完成

      ![](assets/Using_Informatica_PowerCenter_with_FusionInsight/4a1d0.png)
    - 点击右上角按钮启用存储库，并为存储库创建内容

      ![](assets/Using_Informatica_PowerCenter_with_FusionInsight/12a7f.png)

    - 在存储库属性中，修改操作类型为普通，并重启服务
      ![](assets/Using_Informatica_PowerCenter_with_FusionInsight/f60fa.png)

  * 创建PowerCenter 数据集成服务
    - 在管理界面，domain下右键新建一个PowerCenter集成服务

      ![](assets/Using_Informatica_PowerCenter_with_FusionInsight/63b41.png)
    - 指定名称等信息，下一步

      ![](assets/Using_Informatica_PowerCenter_with_FusionInsight/d7ca7.png)
    - 指定存储库信息，点击完成，并启用服务

      ![](assets/Using_Informatica_PowerCenter_with_FusionInsight/1e993.png)

  * 在infa server创建developuser
    - 在安全页签下，创建一个用户，名为developuser，与Hadoop集群用户保持一致
      ![](assets/Using_Informatica_PowerCenter_with_FusionInsight/aa8bb.png)
    - 修改用户的优先级以及用户组
      ![](assets/Using_Informatica_PowerCenter_with_FusionInsight/b2f50.png)

      ![](assets/Using_Informatica_PowerCenter_with_FusionInsight/d9266.png)

  * 在infa Server 进行Hadoop配置
    - 以infa用户登录节点，创建配置文件目录，例如`/opt/pwx-hadoop/conf`
    - 在FusionInsight HD客户端中获取以下配置文件,放至`/opt/pwx-hadoop/conf`目录中，并修改文件权限至775

      ![](assets/Using_Informatica_PowerCenter_with_FusionInsight/22248.png)
    - 执行以下命令进行Kerberos认证,并指定cache文件,infa用户需要对指定的路径有读写权限
      ```
      source /opt/hadoopclient/bigdata_env
      kinit -c /home/infa/krb5cc_developuser developuser
      ```
    - 修改`/opt/pwx-hadoop/conf`目录中的`core-site.xml`文件，添加如下配置
      ```
      <property>
      <name>hadoop.security.kerberos.ticket.cache.path</name>
      <value>home/infa/krb5cc_developuser</value>
      <description>Path to the Kerberos ticket cache. </description>
      </property>
      ```
    - 在Administrator 管理界面，为集成服务创建自定义参数，并重启集成服务

      ![](assets/Using_Informatica_PowerCenter_with_FusionInsight/ace8a.png)

    - 删除
      `/home/infa/Informatica/10.2.0/services/shared/hadoop/hortonworks_2.5/lib/`目录下hive相关的jar包，并将`/opt/hadoopclient/Hive/Beeline/lib`下hive相关的jar包拷贝至该目录，并修改文件权限

      ```
        rm -f /home/infa/Informatica/10.2.0/services/shared/hadoop/hortonworks_2.5/lib/hive*
        cp /home/infa/Informatica/10.2.0/services/shared/hadoop/hortonworks_2.5/lib/hive* /home/infa/Informatica/10.2.0/services/shared/hadoop/hortonworks_2.5/lib
        chown infa:oinstall /home/infa/Informatica/10.2.0/services/shared/hadoop/hortonworks_2.5/lib/hive*
      ```

### PowerCenter Client配置
### PowerCenter Repository Manager配置
  - 获取PowerCenter Client安装包，安装时选取PowerCenter Client,启动PowerCenter Repository Manager，选择菜单栏仓库->配置域，配置完成可以看到之前创建的存储库

      ![](assets/Using_Informatica_PowerCenter_with_FusionInsight/fcffd.png)

      ![](assets/Using_Informatica_PowerCenter_with_FusionInsight/fcf45.png)

  - 双击存储库，输入密码，连接

      ![](assets/Using_Informatica_PowerCenter_with_FusionInsight/a0c96.png)

  - 选择菜单栏文件夹,创建文件夹

      ![](assets/Using_Informatica_PowerCenter_with_FusionInsight/51725.png)

      ![](assets/Using_Informatica_PowerCenter_with_FusionInsight/55ebe.png)

### PowerCenter Designer配置
  - 打开PowerCenter Designer，右键刚才创建的文件夹，点击open，打开配置界面

      ![](assets/Using_Informatica_PowerCenter_with_FusionInsight/e55a6.png)
  - 点击菜单栏Sources->import from databases，在ODBC数据源中创建sitDSN，填写数据库相关信息，测试连接

      ![](assets/Using_Informatica_PowerCenter_with_FusionInsight/1b673.png)

      ![](assets/Using_Informatica_PowerCenter_with_FusionInsight/d8d7d.png)

      ![](assets/Using_Informatica_PowerCenter_with_FusionInsight/b5374.png)
  - 选择刚才创建的数据源，填入数据库用户名密码，连接，可以看到数据库中的表

      ![](assets/Using_Informatica_PowerCenter_with_FusionInsight/761e8.png)
  - 选择target designer，拖入source中的表

      ![](assets/Using_Informatica_PowerCenter_with_FusionInsight/97651.png)
  - 双击表，设置数据类型为Flat File

      ![](assets/Using_Informatica_PowerCenter_with_FusionInsight/9e503.png)

  - 在mapping设置页面，创建新的mapping，拖入source和target表，并连线

      ![](assets/Using_Informatica_PowerCenter_with_FusionInsight/165bd.png)

### 打开PowerCenter Workflow Manager
  - 在菜单栏选择task,新建一个task,命名并选择刚才新建的map

      ![](assets/Using_Informatica_PowerCenter_with_FusionInsight/4ddd4.png)

      ![](assets/Using_Informatica_PowerCenter_with_FusionInsight/21d6e.png)

  - 新建一个workflow，拖入刚才新建的task，并连线

      ![](assets/Using_Informatica_PowerCenter_with_FusionInsight/63436.png)

      ![](assets/Using_Informatica_PowerCenter_with_FusionInsight/89afc.png)

  - 在菜单栏connection中，新建一个application connection,选择Hadoop HDFS Connection

      ![](assets/Using_Informatica_PowerCenter_with_FusionInsight/9412f.png)

  - 具体信息填写如下
      ```
      HDFS Connection URI：hdfs://namenodeip:25000
      Hive URL : jdbc:hive2://172.16.4.21:21066/default;sasl.qop=auth-conf;auth=KERBEROS;principal=hive/hadoop.hadoop.com@HADOOP.COM;user.keytab=/opt/user.keytab;user.principal=developuser
      Hive User Name: developuser
      ```
      ![](assets/Using_Informatica_PowerCenter_with_FusionInsight/acce0.png)

  - 双击刚才创建的task，在mapping选项卡，点击target，设置写入类型为`HDFS Flat Write`，并选择连接为刚才创建的connection，并在properties中进行如下配置

      ![](assets/Using_Informatica_PowerCenter_with_FusionInsight/7f131.png)

      ![](assets/Using_Informatica_PowerCenter_with_FusionInsight/0cecf.png)


  - 保存当前workflow，右键，启动workflow

      ![](assets/Using_Informatica_PowerCenter_with_FusionInsight/cc401.png)

  - 在PowerCenter Workflow Monitor中可以看到任务执行情况

      ![](assets/Using_Informatica_PowerCenter_with_FusionInsight/9e5ed.png)

  - 在HDFS中可以看到导入的数据

       ![](assets/Using_Informatica_PowerCenter_with_FusionInsight/0b939.png)

  - 在task配置中勾选写入Hive表，填入之前创建的表名，运行workflow

       ![](assets/Using_Informatica_PowerCenter_with_FusionInsight/f160d.png)

  - 在Hive中可以看到表中的数据

      ![](assets/Using_Informatica_PowerCenter_with_FusionInsight/b6b1e.png)
