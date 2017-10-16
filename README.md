<!-- ex_nonav -->

# FusionInsight生态地图

FusionInsight支持开源标准的Hadoop接口，可以与以下第三方工具进行对接

## 数据可视化

| 第三方工具  | 对接组件    | 对接版本                 |                             相关文档                              |
|:------------|:------------|:-------------------------|:-----------------------------------------------------------------:|
| SAS         | HDFS、Hive  | C60                      |                                TBD                                |
| IBM SPSS    |             |                          |                                TBD                                |
| IBM Cognos  | Hive、Spark | C60                      |                                TBD                                |
| Tableau     | Hive、Spark | C60U10、C60U20 | [文档](Business_Intelligence/Using_Tableau_with_FusionInsight.md) |
| QlikView    | Hive、Spark | C30、C60U10              | [文档](Business_Intelligence/Using_QlikView_with_FusionInsight.md) |
| Oracle BIEE | Hive、Spark | C60U20                   | [文档](Business_Intelligence/Using_Oracle_BIEE_with_FusionInsight.md) |
| 永洪BI      | Hive、Spark | C60U20                   |                                TBD                                |

## 数据集成

| 第三方工具               | 对接组件                               | 对接版本       |                                相关文档                                |
|:-------------------------|:---------------------------------------|:---------------|:----------------------------------------------------------------------:|
| IBM InfoSphere DataStage | HDFS、HBase、Hive、Spark、Kafka、MPPDB | C60U20         |                                  TBD                                   |
| IBM InfoSphere CDC       | HDFS                                   | C50            |                                  TBD                                   |
| Oracle GoldenGate        | HDFS、HBase、Kafka、Flume              | C60U10、C60U20 | [文档](Data_Integration/Using_Oracle_GoldenGate_with_FusionInsight.md) |
| informatica              | HDFS、HBase                            | C50、C60U10    |                                  TBD                                   |
| Talend                   | Hive、HBase、Hive                      | C30、C60U10    |                                  TBD                                   |
| Kettle                   | HDFS、Hive                             | C60U20、C60U20 |      [文档](Data_Integration/Using_Kettle_with_FusionInsight.md)       |
| 普元                     | Hive、HBase、Hive                      | C60U20         |                                  TBD                                   |
| 杭州合众UTL              | HDFS、HBase、Hive、Kafka               | C50            |                                  TBD                                   |

## 集成开发环境

| 第三方工具       | 对接组件                   |                               相关文档                                         |
|:-----------------|:---------------------------|:---------------------------------------------------------------------------------------:|
| RStudio          | Spark、SparkR              | [C60U10、C70](Integrated_Development_Environment/Using_RStudio_with_FusionInsight.md)     |
| Apache Zepplin   | HBase、Hive、Spark、SparkR | [Zepplin0.7.2 <-> C60U20](Integrated_Development_Environment/Using_Zeppelin_0.7.2_with_FusionInsight_HD_C60U20.md)<BR>[Zepplin0.7.3 <-> C70SPC100](Integrated_Development_Environment/Using_Zeppelin_0.7.3_with_FusionInsight_HD_C70SPC100.md)|
| Jypyter Notebook | PySpark、SparkR            | [C60U10](Integrated_Development_Environment/Using_Jupyter_Notebook_with_FusionInsight.md) |
| DBeaver          | Hive、Spark、HBase         | [C60U20](Integrated_Development_Environment/Using_DBeaver_with_FusionInsight.md)     |
| DbVisualizer     | Hive、Spark、HBase         | [C60U20](Integrated_Development_Environment/Using_DbVisualizer_with_FusionInsight.md)    |
| Squirrel         | Hive、Spark、HBase         | [C60U20](Integrated_Development_Environment/Using_Squirrel_with_FusionInsight.md)   |

## SQL分析

| 第三方工具   | 对接组件    |   相关文档       |
|:-------------|:------------|:--------------:|
| Apache Kylin | HBase、Hive | [Kylin1.6.0 <-> C60U20](SQL_Analytics_Engine/Using_Kylin_with_FusionInsight.md) |
| Kyligence    | HBase、Hive |     C60U10        |
| Presto       | Hive、HDFS      | [Presto0.155 <-> C60U20](SQL_Analytics_Engine/Using_Presto0.155_with_FusionInsight_HD_C60U20.md)<BR>[Presto0.184 <-> C70SPC100](SQL_Analytics_Engine/Using_Presto0.184_with_FusionInsight_HD_C70SPC100.md) |

## 数据库

| 第三方工具 | 对接组件 | 对接版本         | 相关文档 |
|:-----------|:---------|:-----------------|:--------:|
| SAP HANA   | Hive     | C50、C60、C60U20 |   TBD    |

## 其他

| 第三方工具           | 对接组件    | 对接版本            |                     相关文档                     |
|:---------------------|:------------|:--------------------|:------------------------------------------------:|
| FUSE                 | HBase、Hive | C50                 |   [文档](Other/Using_FUSE_with_FusionInsight.md) |
| gis-tools-for-hadoop | Hive        | C50、C60U10、C60U20 |   [文档](Other/Using_GIS_Tools_for_Hadoop_with_FusionInsight.md)|
| IBM WAS              | HDFS        | C60U20              |                       TBD                        |
