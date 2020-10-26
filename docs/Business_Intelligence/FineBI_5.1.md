# FineBI对接FusionInsight

## 适用场景

> FineBI 5.1 <--> FusionInsight HD 6.5 (Hive)

> FineBI 5.1 <--> FusionInsight MRS 8.0 (Hive)

## 安装FineBI

- 安装FineBI, 以路径`C:\soft\fineBI\FineBI5.1`为例

## 配置JDBC接口对接Hive

- 将对接集群（版本6.5.1）的认证文件下载到`C:\651client`文件夹下，包括user.keytab和krb5.conf

- 在FineBI的bin目录下找到配置文件finebi.vmoptions

  ![](assets/FineBI_5.1/markdown-img-paste-20191017104256220.png)

  并在该文件中添加配置：

  ![](assets/FineBI_5.1/markdown-img-paste-20191017104318937.png)

  jaas.conf文件内容为：

  ![](assets/FineBI_5.1/markdown-img-paste-20191017104347390.png)


- 下载FI HD6.5.1的客户端到本地，补全jar包，找到Hive\Beeline\lib路径可以看到相关的jar包

  ![](assets/FineBI_5.1/markdown-img-paste-20191017105301254.png)

- 找到并进入FineBI相关依赖路径，具体为`C:\soft\fineBI\FineBI5.1\webapps\webroot\WEB-INF\lib`, 需要做以下三个操作
  1.  找到jar包fine-bi-engine-third-5.1.jar，右键使用winRAR打开

    ![](assets/FineBI_5.1/markdown-img-paste-20191017110457789.png)

    进入org/apache目录，找到并删除zookeeper文件夹

    ![](assets/FineBI_5.1/markdown-img-paste-20191017110523906.png)

  2.  删除FineBI自带的zookeeper-3.4.6.jar

    ![](assets/FineBI_5.1/markdown-img-paste-20191017110604549.png)

  3.  将上一步集群客户端Hive\Beeline\lib路径下所有的jar包拷贝到当前文件夹（`C:\soft\fineBI\FineBI5.1\webapps\webroot\WEB-INF\lib`）

- 启动FineBI，找到管理系统 -> 数据连接 -> 新建数据连接 -> 更多数据连接 找到fusioninsight hd 选中点确定

  ![](assets/FineBI_5.1/markdown-img-paste-2019101711071559.png)

- 参考下图配置连接参数

  URL: `jdbc:hive2://172.16.4.21:24002,172.16.4.22:24002,172.16.4.23:24002/;serviceDiscoveryMode=zooKeeper;zooKeeperNamespace=hiveserver2;sasl.qop=auth-conf;auth=KERBEROS;principal=hive/hadoop.hadoop.com@HADOOP.COM;user.principal=developuser;user.keytab=C:/651client/user.keytab`

  ![](assets/FineBI_5.1/markdown-img-paste-20191017110741603.png)

  需要注意的是user.principal和user.keytab需要同实际情况匹配

  点击测试连接测试

  ![](assets/FineBI_5.1/markdown-img-paste-20191017110831692.png)

- 点击创建 -> 添加添加数据库表

  ![](assets/FineBI_5.1/markdown-img-paste-20191017110850520.png)

  选择一张表，如果已经选择配置过则为灰色,选择需要导入的数据列表路径

  ![](assets/FineBI_5.1/markdown-img-paste-20191017110903474.png)

  在数据准备->对应的数据列表路径中找到之前配置好的表test

  ![](assets/FineBI_5.1/markdown-img-paste-20191017110918221.png)
