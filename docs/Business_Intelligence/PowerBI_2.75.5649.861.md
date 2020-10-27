# PowerBI对接FusionInsight

## 适用场景

> Power BI 2.75.5649.861 <--> FusionInsight HD 6.5 (Hive/Spark2x/FTP-Server)

> Power BI 2.75.5649.861 <--> FusionInsight MRS 8.0 (Hive/FTP-Server)

## 简介

## 准备工作

* 登录FusionInsight Manager创建一个“人机”用户，例如：developuser，具体请参见FusionInsight HD产品文档的`管理员指南->系统设置->权限设置->用户管理->创建用户`章节。给developuser用户授予所有访问权限，包含但不限于Hive、Spark2x。

* 已完成FusionInsight HD客户端安装，具体请参见FusionInsight HD产品文档的`应用开发指南->安全模式->安全认证->配置客户端文件`章节。

* 客户端机器的时间与FusionInsight HD集群的时间要保持一致，时间差小于5分钟。

* Hive数据库已存在表student：

  **示例如下：**

  ```
  CREATE TABLE IF NOT EXISTS student(id INT, first_name STRING, last_name STRING, subject_id INT, score FLOAT);
  INSERT INTO student VALUES (1,'Tom','Zhang',1,80);
  INSERT INTO student VALUES (2,'Sandy','Li',2,75);
  INSERT INTO student VALUES (3,'Benny','Chow',3,76);
  INSERT INTO student VALUES (4,'Tina','Wang',1,60);
  INSERT INTO student VALUES (5,'Tracy','Zhang',1,80);
  INSERT INTO student VALUES (6,'Andy','Li',2,79);
  INSERT INTO student VALUES (7,'Manson','Chow',3,86);
  INSERT INTO student VALUES (8,'Aurora','Wang',1,90);
  ```

* 本地已存在Subject.xlsx，内容如下所示：

  ![](assets/PowerBI_2.75.5649.861/7e9b7ca4.png)

* 从<https://www.microsoft.com/en-us/download/details.aspx?id=58494>下载对应操作系统的Power BI Desktop并安装。本文版本为 **PBIDesktopSetup_x64.exe**。

  ![](assets/PowerBI_2.75.5649.861/dd0afc0e.png)

  >说明：只在本地创建报表，不需要注册账号。如果需要发布报表与他人共享，则需要注册账号。

## 配置Windows的kerberos认证

* 从<http://web.mit.edu/kerberos/dist/#kfw-4.0>下载对应操作系统架构的MIT Kerberos并安装。本文版本为 **kfw-4.1-amd64.msi**。

* 设置Kerberos的配置文件。登录FusionInsight Manager的`系统->用户->更多（developuser）->下载认证凭证`，下载developuser对应的认证凭证。将krb5.conf文件重命名为 **krb5.ini** 放在`C:\ProgramData\MIT\Kerberos5`目录下。

  >说明：`C:\ProgramData`一般属于隐藏文件夹，在“文件夹和搜索选项->查看”中设置“显示隐藏的文件、文件夹或驱动器”或者使用搜索功能即可解决问题。

* 设置Kerberos票据的缓存文件

  * 在本地创建存放票据的目录，例如`C:\temp`。

  * 设置Windows的系统环境变量，变量名为`KRB5CCNAME`，变量值为`C:\temp\krb5cache`。

    ![](assets/PowerBI_2.75.5649.861/052e626c.png)

* 重启机器让新增的环境变量生效。

* 在Windows上进行认证

  * 使用上述创建的用户名密码登录，用户名的格式为：用户名@Kerberos域名。

  * 打开MIT Kerberos，单击 **get Ticket** ，在弹出的MIT Kerberos: Get Ticket窗口中，**Pricipal** 输入用户名`developuser@HADOOP.COM`，**Password** 输入密码，单击 **OK**。

    ![](assets/PowerBI_2.75.5649.861/e4d0691e.png)

    >说明：票据过期后需要重新获取。

## 配置Hive数据源

Power BI中配置Hive数据源，对接Hive的ODBC接口。

* 从<https://www.microsoft.com/en-us/download/details.aspx?id=40886>下载Microsoft Hive ODBC Driver并安装。本文版本为 **HiveODBC64.msi**。

* 配置ODBC驱动

  * 创建DSN(Data Source Name)：选择 **开始** -> **Microsoft Hive ODBC Driver** -> **64-bit ODBC Administrator**。

  * 选择 **User DSN** -> **Add** -> **Microsoft Hive ODBC Driver** -> **Finish**

    **配置示例如下（其余选项为默认值）：**

    ```
    Data Source Name: ms_hive_odbc，可自定义。
    Host(s): 172.16.4.21，Hive Service主节点
    Port：21066，Hive Service端口
    Database: default
    Mechanism：Kerberos
    Host FQDN：hadoop.hadoop.com
    Service Name：hive
    Realm：留空
    Thrift Transport: SASL
    SSL Options: 取消勾选“Enable SSL”
    ```

    ![](assets/PowerBI_2.75.5649.861/0256e8d8.png)

    > 说明：Advanced Options不需要进行配置默认的参数即可连接成功。

  * 点击 **Test** 按钮测试连接，如返回“SUCCESS”，则表示ODBC连接Hive成功。

    ![](assets/PowerBI_2.75.5649.861/56fef659.png)

## Power BI对接Hive

  * Power BI启动后点击 **Get data** 或者 **home->Get Data->More**。

    ![](assets/PowerBI_2.75.5649.861/05d32f8c.png)

  * 在搜索框输入 **odbc** 后选择 **ODBC**，点击 **Connect**。

    ![](assets/PowerBI_2.75.5649.861/7927572d.png)

  * “Data source name (DSN)” 选择 **ms_hive_odbc**，点击 **OK**。

    ![](assets/PowerBI_2.75.5649.861/2dd77c9d.png)

  * 选择 **Windows**，点击 **Connect**。

    ![](assets/PowerBI_2.75.5649.861/f0692fac.png)

  * 勾选 **default** 数据库的表 **student**，点击 **Load**。

    ![](assets/PowerBI_2.75.5649.861/38277cc4.png)

  * 选择 **Data** 视图即可预览表的数据。

    ![](assets/PowerBI_2.75.5649.861/750d4a32.png)

## 配置Spark数据源

* 从<https://www.microsoft.com/en-us/download/details.aspx?id=49883>下载Microsoft Spark ODBC Driver并安装。本文版本为 **SparkODBC64.msi**。

* 配置ODBC驱动

  * 创建DSN(Data Source Name)：选择 **开始** -> **Microsoft Spark ODBC Driver** -> **64-bit ODBC Administrator**。

  * 选择 **User DSN** -> **Add** -> **Microsoft Spark ODBC Driver** -> **Finish**

    **配置示例如下（其余选项为默认值）：**

    ```
    Data Source Name: ms_spark2x_odbc，可自定义。
    Spark Serve Type: SparkThriftServer(Spark1.1 and later)
    Host(s): 172.16.4.22，Spark2x的JDBCServer2x主节点
    Port：22550，为属性hive.sever2.thrift.port的值
    Database: default
    Mechanism：Kerberos
    Host FQDN：hadoop.hadoop.com
    Service Name：spark2x
    Realm：留空
    Thrift Transport: SASL
    SSL Options: 取消勾选“Enable SSL”
    ```

    ![](assets/PowerBI_2.75.5649.861/87b2b684.png)

    > 说明：Advanced Options不需要进行配置默认的参数即可连接成功。

  * 点击 **Test** 按钮测试连接，如返回“SUCCESS”，则表示ODBC连接Spark2x成功。

    ![](assets/PowerBI_2.75.5649.861/f3ad6e67.png)

## Power BI对接Spark2x

Power BI对接Spark2x有两种方式。可以选择通过Spark ODBC对接，或者通过Power BI提供的Spark方式对接。

### ODBC

  * Power BI启动后，点击 **Get data** 或者 **home->Get Data->More**。

    ![](assets/PowerBI_2.75.5649.861/05d32f8c.png)

  * 在搜索框输入 **odbc** 后选择 **ODBC**，点击 **Connect**。

    ![](assets/PowerBI_2.75.5649.861/7927572d.png)

  * “Data source name (DSN)” 选择 **ms_spark2x_odbc**，点击 **OK**。

    ![](assets/PowerBI_2.75.5649.861/6b22da4b.png)

  * 选择 **Windows**，点击 **Connect**。

    ![](assets/PowerBI_2.75.5649.861/33bb1f20.png)

  * 勾选 **default** 数据库的表 **student**，点击 **Load**。

    ![](assets/PowerBI_2.75.5649.861/85f0727f.png)

  * 选择 **Data** 视图即可预览表的数据。

    ![](assets/PowerBI_2.75.5649.861/28f7ac97.png)

### Spark

* Power BI启动后，点击 **Get data** 或者 **home->Get Data->More**。

  ![](assets/PowerBI_2.75.5649.861/05d32f8c.png)

* 在搜索框输入 **spark** 后选择 **Spark**，点击 **Connect**。

  ![](assets/PowerBI_2.75.5649.861/7b1b4631.png)

* “Server”输入Spark2x的JDBCServer2x主节点IP，例如 **172.16.4.22**，“Protocol”选择 **Standard**，点击 **OK**。

  ![](assets/PowerBI_2.75.5649.861/ae4e8d31.png)

* 点击 **Windows**，选择 **Use my current credentials**，“Realm”输入 **HADOOP.COM**，“Host FQDN”输入 **hadoop.hadoop.com**，“Service Name”输入 **spark2x**，点击 **Connect**。

  ![](assets/PowerBI_2.75.5649.861/7d15a68b.png)

* 勾选表 **student**，点击 **Load**。

  ![](assets/PowerBI_2.75.5649.861/defc3151.png)

* 选择 **Data** 视图即可预览表的数据。

  ![](assets/PowerBI_2.75.5649.861/28f7ac97.png)

## Power BI对接FTP-Server

* 登录FusionInsight Manger，修改FTP-Server的配置 **ftp-enabled=true** 保存后，点击 **更多->重启** 重启FTP-Server。

  ![](assets/PowerBI_2.75.5649.861/730efbf4.png)

* 登录FusionInsight客户端，创建文件powerbi_hdfs.txt并上传至HDFS文件系统。

  ```
  vi /opt/powerbi_hdfs.txt
  hdfs dfs -put /opt/powerbi_hdfs.txt /tmp
  ```

  ![](assets/PowerBI_2.75.5649.861/657562b3.png)

* Power BI启动后，点击 **Get data** 或者 **home->Get Data->More**。

  ![](assets/PowerBI_2.75.5649.861/05d32f8c.png)

* 在搜索框输入 **web** 后选择 **Web**，点击 **Connect**。

  ![](assets/PowerBI_2.75.5649.861/39ade030.png)

* 选择 **Basic**，“URL”输入 **ftp://172.16.4.21:22021/tmp/powerbi_hdfs.txt** 。

  ![](assets/PowerBI_2.75.5649.861/c286a361.png)

* 点击 **FTP**，输入FusionInsight用户名 **developuser** 和对应的密码，点击 **Connect**。

  ![](assets/PowerBI_2.75.5649.861/a5126ff3.png)

* 点击 **Load** 加载数据。

  ![](assets/PowerBI_2.75.5649.861/96ff67a6.png)

* 选择 **Data** 视图即可预览数据。

  ![](assets/PowerBI_2.75.5649.861/2463b0e7.png)

## Power BI调整和合并多个数据源。

使用Power BI调整和合并从Hive/Spark、Excel导入的多个数据源，输出报表。以下以调整、合并Hive和Excel数据源为例。调整、合并Spark、FTP-Server和Excel数据源操作类似。

* 导入Excel数据源。

  * 点击 **home->Get Data->Excel** 导入本地文件Subject.xlsx。

    ![](assets/PowerBI_2.75.5649.861/9a172993.png)

  * 勾选 **Subject**，点击 **Load**。

    ![](assets/PowerBI_2.75.5649.861/a8a77e7c.png)

    ![](assets/PowerBI_2.75.5649.861/650f6c5a.png)

* 点击 **home->Edit Queries** 进入Power Query编辑器进行调整和组合操作。

  ![](assets/PowerBI_2.75.5649.861/8da30a5d.png)

  * **合并列：** 在Power Query编辑器中，按下Ctrl键选中查询student的first_name和last_name列，然后点击 **Transform->Merge Columns** 将这两列合并，并命名为 **name**。

    ![](assets/PowerBI_2.75.5649.861/ec2d4f96.png)

    ![](assets/PowerBI_2.75.5649.861/98a4b30a.png)

  * **合并查询：**

    * 在Power Query编辑器中，选中查询student，点击 **home->Merge Queries**，

      ![](assets/PowerBI_2.75.5649.861/c4198556.png)

    * 选中 **student.subject_id** 和 **Subject.id**，“Join Kind”选择 **Left Outer**，点击 **OK**。

      ![](assets/PowerBI_2.75.5649.861/c18060e9.png)

    * 点击![](assets/PowerBI_2.75.5649.861/f849d648.png)按钮可展开隐藏的列。

      ![](assets/PowerBI_2.75.5649.861/a9ae1b90.png)

      ![](assets/PowerBI_2.75.5649.861/8e86d7c6.png)

   * **删除列：** 点击 **home->Remove Cloumns** 可将不需要的列删除。将id、subject_id、subject.id、subject.description删除。

   * **重命名列：** 双击列名“subject.name”重命名为“subject_name”。

   * 点击 **Close & Apply** 关闭Power Query编辑器并应用修改。

      ![](assets/PowerBI_2.75.5649.861/575b506c.png)

* 创建报表。选择 **报表** 视图。依次勾选查询student的 **Subject_name**、**score**。“值”选择 **Average of score**，并点击图表右上角的 **...** 按钮选择 **Show data**。报表显示如下：

  ![](assets/PowerBI_2.75.5649.861/f78552d1.png)
