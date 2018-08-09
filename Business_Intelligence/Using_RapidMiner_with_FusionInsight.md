# RapidMiner对接FusionInsight

## 适用场景

> Rapidminer Studio 8.2.001 <-> FusionInsight HD V100R002C80SPC200

##准备工作

  * 下载安装RapidMiner Studio, 当前最新版本为8.2.001,下载地址 <https://rapidminer.com/>
  * 修改本地host文件，路径为C:\Windows\System32\drivers\etc，加入集群各个节点IP与主机名对应关系，保存文件。
  * 设置Kerberos的配置文件

    在FusionInsight Manager创建一个角色与“人机”用户，具体请参见《FusionInsight HD 管理员指南》的创建用户章节。角色根据业务需要授予Spark，Hive，HDFS的访问权限，并将用户加入角色。例如，创建用户“developuser”并下载对应的keytab文件user.keytab以及krb5.conf文件。

  * 准备FusionInsight客户端配置文件以及jar包

    * 在集群的Manager中，选择服务->下载客户端->完整客户端

      ![](assets/Using_Rapidminer_with_FusionInsight/img001.png)

    * 解压后，进入HDFS，Hive，Yarn组件的config目录，找到如下的配置文件，复制到一个文件夹里，例如命名为config。

      ![](assets/Using_Rapidminer_with_FusionInsight/img002.png)

      打开hdfs-site.xml文件，将以下属性以及对应的value删除：
         ```
         dfs.client.failover.proxy.provider.hacluster
         ```
      打开core-site.xml文件，修改以下属性的value
         ```
         fs.defaultFS         
         ```
      修改为 namenodeIP:dfs.namenode.rpc.port的形式，例如
         ```
         172.21.3.116:25000
         ```
    * （可选）进入Spark组件的Jar包目录“\FusionInsight_Services_ClientConfig\Spark2x\FusionInsight-Spark2x-2.1.0.tar.gz\spark\jars”，将所有jar包复制出来，保存在jars文件夹里。

##集群配置

  * 配置UDP端口绑定

    - 下载安装UDP端口绑定工具uredir，下载地址<https://github.com/troglobit/uredir>
    - 编译安装完成后，分别上传至KDC服务所在的主备节点，进入uredir执行文件所在目录，执行以下命令进行端口绑定,其中IP为所在节点IP
      ```
      ./uredir IP 88 IP 21732
      ```

  * 配置Radoop依赖jar包
    - 在Radoop文档中心，下载Radoop依赖jar包，下载地址<https://docs.rapidminer.com/latest/radoop/installation/operation-and-maintenance.html>,下载与安装的RapidMiner版本对应的jar包。

      ![](assets/Using_Rapidminer_with_FusionInsight/img003.png)

    - 将jar包上传至集群每个节点相同的路径下，例如/usr/local/lib/radoop/

    - 在集群主节点和备节点，分别上传Radoop的jar包至以下路径
      - Hive服务端的lib路径"/opt/huawei/Bigdata/FusionInsight_HD_V100R002C80SPC200/install/FusionInsight-Hive-1.3.0/hive-1.3.0/lib"，
      - Mapreduce服务端的lib路径："/opt/huawei/Bigdata/FusionInsight_HD_V100R002C80SPC200/install/FusionInsight-Hadoop-2.7.2/hadoop/share/hadoop/mapreduce/lib"

  * 创建Radoop UDF函数

    - 在主节点执行如下命令：
        ```
        #cd /opt/hadoopclient
        #source bigdata_env
        #kinit developuser
        ```
        输入developuser用户密码，执行beeline，进入Hive

    -   Hive中创建数据库，例如创建数据库rapidminer,执行以下命令：
        ```
        create database rapidminer；
        use rapidminer；
        DROP FUNCTION IF EXISTS r3_add_file;
        DROP FUNCTION IF EXISTS r3_apply_model;
        DROP FUNCTION IF EXISTS r3_correlation_matrix;
        DROP FUNCTION IF EXISTS r3_esc;
        DROP FUNCTION IF EXISTS r3_gaussian_rand;
        DROP FUNCTION IF EXISTS r3_greatest;
        DROP FUNCTION IF EXISTS r3_is_eq;
        DROP FUNCTION IF EXISTS r3_least;
        DROP FUNCTION IF EXISTS r3_max_index;
        DROP FUNCTION IF EXISTS r3_nth;
        DROP FUNCTION IF EXISTS r3_pivot_collect_avg;
        DROP FUNCTION IF EXISTS r3_pivot_collect_count;
        DROP FUNCTION IF EXISTS r3_pivot_collect_max;
        DROP FUNCTION IF EXISTS r3_pivot_collect_min;
        DROP FUNCTION IF EXISTS r3_pivot_collect_sum;
        DROP FUNCTION IF EXISTS r3_pivot_createtable;
        DROP FUNCTION IF EXISTS r3_score_naive_bayes;
        DROP FUNCTION IF EXISTS r3_sum_collect;
        DROP FUNCTION IF EXISTS r3_which;
        DROP FUNCTION IF EXISTS r3_sleep;
        CREATE FUNCTION r3_add_file AS 'eu.radoop.datahandler.hive.udf.GenericUDFAddFile';
        CREATE FUNCTION r3_apply_model AS 'eu.radoop.datahandler.hive.udf.GenericUDTFApplyModel';
        CREATE FUNCTION r3_correlation_matrix AS 'eu.radoop.datahandler.hive.udf.GenericUDAFCorrelationMatrix';
        CREATE FUNCTION r3_esc AS 'eu.radoop.datahandler.hive.udf.GenericUDFEscapeChars';
        CREATE FUNCTION r3_gaussian_rand AS 'eu.radoop.datahandler.hive.udf.GenericUDFGaussianRandom';
        CREATE FUNCTION r3_greatest AS 'eu.radoop.datahandler.hive.udf.GenericUDFGreatest';
        CREATE FUNCTION r3_is_eq AS 'eu.radoop.datahandler.hive.udf.GenericUDFIsEqual';
        CREATE FUNCTION r3_least AS 'eu.radoop.datahandler.hive.udf.GenericUDFLeast';
        CREATE FUNCTION r3_max_index AS 'eu.radoop.datahandler.hive.udf.GenericUDFMaxIndex';
        CREATE FUNCTION r3_nth AS 'eu.radoop.datahandler.hive.udf.GenericUDFNth';
        CREATE FUNCTION r3_pivot_collect_avg AS 'eu.radoop.datahandler.hive.udf.GenericUDAFPivotAvg';
        CREATE FUNCTION r3_pivot_collect_count AS 'eu.radoop.datahandler.hive.udf.GenericUDAFPivotCount';
        CREATE FUNCTION r3_pivot_collect_max AS 'eu.radoop.datahandler.hive.udf.GenericUDAFPivotMax';
        CREATE FUNCTION r3_pivot_collect_min AS 'eu.radoop.datahandler.hive.udf.GenericUDAFPivotMin';
        CREATE FUNCTION r3_pivot_collect_sum AS 'eu.radoop.datahandler.hive.udf.GenericUDAFPivotSum';
        CREATE FUNCTION r3_pivot_createtable AS 'eu.radoop.datahandler.hive.udf.GenericUDTFCreatePivotTable';
        CREATE FUNCTION r3_score_naive_bayes AS 'eu.radoop.datahandler.hive.udf.GenericUDFScoreNaiveBayes';
        CREATE FUNCTION r3_sum_collect AS 'eu.radoop.datahandler.hive.udf.GenericUDAFSumCollect';
        CREATE FUNCTION r3_which AS 'eu.radoop.datahandler.hive.udf.GenericUDFWhich';
        CREATE FUNCTION r3_sleep AS 'eu.radoop.datahandler.hive.udf.GenericUDFSleep';
        ```

##RapidMiner配置

  * 在RapidMiner中，菜单选择Connections->Manage Radoop Connections
  * 在弹出的对话框中选择New Connections->Import Hadoop Configuration Files，选择配置文件所在文件夹，点击Import Configuration

    ![](assets/Using_Rapidminer_with_FusionInsight/img004.png)

  * 导入成功后点击Next，进入连接配置窗口，根据左侧菜单栏，进行如下填写：

    * Global：
      - Hadoop Version：Other（Hadoop 2X line）
      - Additional Libraries Directory：Spark组件的jars包
      - Client Principal： Kerberos用户名@HADOOP.com
      - Keytab File: 从Manager下载的keytab文件
      - KDC Address： 集群KDC所在服务器IP
      - REALM： HADOOP.COM
      - Kerberos Config File: 从Manager下载的krb5配置文件

      ![](assets/Using_Rapidminer_with_FusionInsight/img005.png)

    * Hadoop：
      - 在左上角搜索框中搜索split，在搜索结果中取消勾选mapreduce.input.fileinputformat.split.maxsize参数

        ![](assets/Using_Rapidminer_with_FusionInsight/img006.png)

      - 搜索classpath，在搜索结果中取消勾选mapreduce.application.classpath参数

        ![](assets/Using_Rapidminer_with_FusionInsight/img007.png)

    * Spark：
      - Spark Version：Spark2.1
      - Spark Archive（or libs）Path: local:///opt/huawei/Bigdata/FusionInsight_Spark2x_V100R002C80SPC200/install/FusionInsight-Spark2x-2.1.0/spark/jars
      - Spark Resource Allocation Policy：Static，Default Configuration
      - Advanced Spark Parameters：添加spark.driver.extraJavaOptions和spark.executor.extraJavaOptions两个参数

        ![](assets/Using_Rapidminer_with_FusionInsight/img008.png)

      - 参数value在Manager，Services->Spark2X Configuration->所有配置，搜索extraJavaOptions，选择Spark2x->SparkResource2x中的这两个参数值，将其中使用的所有“./”相对路径替换为服务端Spark配置文件所在的绝对路径，例如“/opt/huawei/Bigdata/FusionInsight_Spark2x_V100R002C80SPC200/1_21_SparkResource2x/etc”

        ![](assets/Using_Rapidminer_with_FusionInsight/img009.png)

    * Hive：
      - Hive Version：Hive Server2
      - Hive Server Address：Hive 服务所在节点IP
      - Hive Port： 21066
      - Database Name： 在Hive中创建的Radoop Function所在的数据库名称
      - Customer database for UDFs: 同Database Name

        ![](assets/Using_RapidMiner_with_FusionInsight/img010.png)

    * 点击OK->Proced Anyway->Save

##测试连接

  * 点击Configure,在Global页面，点击Test，Test Results显示如下，表明Global测试成功

    ![](assets/Using_Rapidminer_with_FusionInsight/img011.png)

  * 在Hadoop页面，点击Test，Test Results显示如下，表明Hadoop测试成功

    ![](assets/Using_Rapidminer_with_FusionInsight/img012.png)

  * 在Spark页面，点击Test，Test Results显示如下，表明Spark测试成功

    ![](assets/Using_Rapidminer_with_FusionInsight/img013.png)

  * 在Hive页面，点击Test，Test Results显示如下，表明Hive测试成功

    ![](assets/Using_RapidMiner_with_FusionInsight/img014.png)

  * 在Manage Radoop Connections 窗口，选中所创建的连接，点击Full test进行完整测试，Test Results显示如下，表明完整测试通过

    ![](assets/Using_RapidMiner_with_FusionInsight/img015.png)

##Radoop样例运行
  * 在RapidMiner Studio 主页面，Help->Tutorials->User Hadoop->Rapidminer Radoop
    - 根据Tutorials的指导运行样例，运行结果如下：

      ![](assets/Using_Rapidminer_with_FusionInsight/img016.png)

##FAQ
  * 测试连接时，提示ICMP port unreachable/Error retrieving Hive object list问题
    - 检查集群中端口绑定程序是否正常运行，绑定的端口是否正确。RapidMiner在测试时，会与集群的88端口连接进行Kerberos认证，而FusionInsight平台对端口进行了规划，Kerberos认证使用的端口是21732。

  * 测试Spark时，提示GSS initiate failed
    - 检查本地host文件是否添加了集群IP与主机名的对应关系。

  * 测试Spark时，将各种版本都测试了一遍，最后提示Spark test failed
    - 检查添加的两个Advanced Parameters是否填写正确，其value值中的绝对路径对于每个集群是不一样的，当集群重装后需要修改该值。
