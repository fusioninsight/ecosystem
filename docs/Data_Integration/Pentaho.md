# Pentaho对接FusionInsight

## 适用场景

> Pentaho EE 7.1 <--> FusionInsight HD V100R002C70SPC200 (HDFS/Hive)
>
> Pentaho EE 8.0 <--> FusionInsight HD V100R002C60U20 (HDFS/Hive)

## Kerberos支持能力说明

Pentaho(7.0-9.0)目前仅仅在企业版（EE）支持Kerberos认证的Hadoop, Pentaho社区版（CE）不支持Kerberos认证的Hadoop，相关答复参考以下链接：https://forums.pentaho.com/threads/230953-Is-Kerberos-auth-Enterprise-only/

Kettle 6.1，所以虽然代码上没有支持Kerberos认证，但是可以通过手动在OS层面进行Kerberos认证来连接安全集群，6.1以后的CE版本由于架构调整，无法通过读取OS上的Kerberos认证信息连接安全集群。
