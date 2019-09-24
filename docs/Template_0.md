# DBeaver对接FusionInsight

## 适用场景

> DBeaver 6.1.4  <--> FusionInsight HD 6.5.1

## 简介

SQL开发工具，如DbVisualizer、DBeaver、Squirrel是数据库开发的常用选择，虽然这些工具大多不提供原生Hive、SparkSQL、Phoenix的支持，但是通过它们支持的自定义JDBC的能力，我们可以与FusionInsignt提供的Fiber组件的JDBC接口进行对接，实现这Hive、SparkSQL、Phoenix组件的统一SQL查询。

* Fiber架构图

  ![](assets/DBeaver_6.1.4/f647a2a6.png)

本文档主要描述在Window操作系统，DBeaver通过Fiber方式对接FusionInsight HD的Hive、Spark2x、Phoenix组件。

## 准备工作

* 登录FusionInsight Manager创建一个“人机”用户，例如：developuser，具体请参见FusionInsight HD产品文档的`管理员指南->系统设置->权限设置->用户管理->创建用户`章节。给developuser用户授予所有访问权限，包含但不限于Spark2x、Hive、HBase。

* 已完成FusionInsight HD客户端安装，具体请参见FusionInsight HD产品文档的`应用开发指南->安全模式->安全认证->配置客户端文件`章节。

* 已将集群的节点主机名与IP的映射关系加入到windows的hosts文件中`C:\Windows\System32\drivers\etc\hosts`。

* 客户端机器的时间与FusionInsight HD集群的时间要保持一致，时间差小于5分钟。

* Windows上已经安装好jdk1.8或者以上版本，并完成jdk环境变量配置。

* Hive数据库已存在表student，数据类似于：

  ![](assets/DBeaver_6.1.4/1d12517a.png)

  **示例如下：**

  ```
  CREATE TABLE IF NOT EXISTS student(id INT, name STRING, class_id INT);
  INSERT INTO student VALUES (1,'Tom',1);
  INSERT INTO student VALUES (2,'Sandy',2);
  INSERT INTO student VALUES (3,'Benny',3);
  INSERT INTO student VALUES (4,'Tina',1);
  ```

## Fiber认证方式配置

### 操作场景

Fiber的安全认证有kinit和keytab两种方式。具体参数配置说明可参考FusionInsight HD产品文档的`业务操作指南->统一SQL(Fiber)->客户端配置`章节。

### 前提条件

* 已完成准备工作。

* 将FusionInsight HD客户端的Fiber、Hive、Spark2x、HBase客户端文件夹，拷贝至本地新建目录`C:\ecotesting`。假设FusionInsight HD客户端安装于`/opt/hadoopclient`目录，则：

  * 将`/opt/hadoopclient/Fiber`拷贝至本地`C:\ecotesting`目录。

  * 将`/opt/hadoopclient/Hive`拷贝至本地`C:\ecotesting\Fiber`目录。

  * 将`/opt/hadoopclient/Spark2x`拷贝至本地`C:\ecotesting\Fiber`目录。

  * 将`/opt/hadoopclient/HBase`拷贝至本地`C:\ecotesting\Fiber`目录。

  * 将`C:\ecotesting\Fiber\HBase\hbase\lib\phoenix-core-4.13.1-HBase-1.3.jar`拷贝至`C:\ecotesting\Fiber\lib`。

  ![](assets/DBeaver_6.1.4/dc36eb03.png)

* 登录FusionInsight Manager的`系统->用户->更多（developuser）->下载认证凭证`，下载developuser对应的认证凭证。将用户的 **krb5.conf** 和 **user.keytab** 文件拷贝到`C:\ecotesting\Fiber\conf`目录下。

### 操作步骤

#### DBeaver通过Fiber对接Hive

* 进入DBeaver界面，菜单选择`Database->DriverManager`，在弹出的对话框中点击 **New**。

  ![](assets/DBeaver_6.1.4/ca995deb.png)

* 填写基本信息如下：

  ```
  Driver Name：Fiber（自定义）
  Class Name：com.huawei.fiber.FiberDriver
  URL Template：jdbc:fiber://
  Default Port：2345（可随便写）
  Category: Hadoop
  ```
* 点击 **Add File** ，增加`C:\ecotesting\Fiber\lib`所有的jar包。

  ![](assets/DBeaver_6.1.4/5e6156c5.png)

* 点击 **Connection properties**，增加两个属性。点击 **OK**。
  ```
  defaultDriver = hive
  fiberconfig = C:\\ecotesting\\Fiber\\conf\\fiber.xml
  ```

  ![](assets/DBeaver_6.1.4/ae4e8fa4.png)

## FAQ

  * **对接Phoenix时返回Driver: Fiber?**

    **【问题描述】**

    对接Phoenix时，点击 **Test Connection** ，没有正确返回Server和Driver的版本。

    ![](assets/DBeaver_6.1.4/2e09db6f.png)

    **【解决方法】**

    确认是否已指定DBeaver的JDK虚拟机。在DBeaver安装目录下，打开dbeaver.ini设置 **-vm** 参数的值，参数和值之间需要换行。

    **示例如下：**
    ```
    -vm
    C:\Program Files\Java\jdk1.8.0_202\bin

    ```

    ![](assets/DBeaver_6.1.4/5d3dafa2.png)
