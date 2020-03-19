# Logstash对接Kafka安全模式

## 适用场景

> Logstash 6.7.1 <--> FusionInsight HD 6.5 (Kafka)

## 环境说明

logstash主机：172.16.2.124

FI HD集群：172.16.10.131-133

## 认证相关配置

- 登陆集群manager下载认证用户的配置文件，user.keytab和krb5.conf并将这两个文件放到logstash安装主机的`/opt`路径下

- 准备认证的jaas.conf文件，放置在logstash安装主机的`/opt`路径下，内容为：
  ```
  KafkaClient {
  com.sun.security.auth.module.Krb5LoginModule required
  useKeyTab=true
  principal="developuser@HADOOP.COM"
  keyTab="/opt/user.keytab"
  useTicketCache=false
  serviceName="kafka"
  storeKey=true
  debug=true;
  };
  Client {
  com.sun.security.auth.module.Krb5LoginModule required
  useKeyTab=true
  principal="developuser@HADOOP.COM"
  keyTab="/opt/user.keytab"
  useTicketCache=false
  storeKey=true
  debug=true;
  };
  ```

- 登陆logstash安装主机`/opt/logstash/logstash-6.7.1/config`路径，编辑配置文件`jvm.options`

  ![](assets/Logstash/markdown-img-paste-20200316162843721.png)

  如图增加配置项：
  ```
  -Djava.security.auth.login.config=/opt/jaas.conf
  -Dkerberos.domain.name=hadoop.hadoop.com
  -Djava.security.krb5.conf=/opt/krb5.conf
  ```

- 找到logstash安装路径下kafka相关jar包路径

  ![](assets/Logstash/markdown-img-paste-20200316163036434.png)

  按照上图：

  1. kafka input的jar包路径:`/opt/logstash/logstash-6.7.1/vendor/bundle/jruby/2.5.0/gems/logstash-input-kafka-8.3.1/vendor/jar-dependencies/org/apache/kafka/kafka-clients/2.1.0/`

  2. kafka output的jar包路径：`/opt/logstash/logstash-6.7.1/vendor/bundle/jruby/2.5.0/gems/logstash-output-kafka-7.3.2/vendor/jar-dependencies/org/apache/kafka/kafka-clients/2.1.0/`

  分别登陆到这两个路径下，将对应FI HD kafa客户端的 kafka-client jar包 `kafka-clients-1.1.0.jar`拷贝到input , output的路径下

  ```
  cp /opt/hadoopclient/Kafka/kafka/libs/kafka-clients-1.1.0.jar /opt/logstash/logstash-6.7.1/vendor/bundle/jruby/2.5.0/gems/logstash-input-kafka-8.3.1/vendor/jar-dependencies/org/apache/kafka/kafka-clients/2.1.0/

  cp /opt/hadoopclient/Kafka/kafka/libs/kafka-clients-1.1.0.jar /opt/logstash/logstash-6.7.1/vendor/bundle/jruby/2.5.0/gems/logstash-output-kafka-7.3.2/vendor/jar-dependencies/org/apache/kafka/kafka-clients/2.1.0/
  ```

  参考下图命令，将input,output路径下原来的jar包注释掉，并将拷贝过来的`kafka-clients-1.1.0.jar`名字修改为`kafka-clients-2.1.0.jar`,否则在启动logstash的时候会报错

  ![](assets/Logstash/markdown-img-paste-20200316164006321.png)

  ![](assets/Logstash/markdown-img-paste-20200316164227601.png)


## logstash kafka input用例

- 在logsatash安装路径config下新建配置文件`logstash-21007input.conf`内容为：

```
  # Sample Logstash configuration for creating a simple
  # Beats -> Logstash -> Elasticsearch pipeline.


  input {
          kafka {
  	bootstrap_servers => "172.16.10.131:21007"
          codec => plain
          topics => "logstashtest21007input"
  	security_protocol => "SASL_PLAINTEXT"
  	sasl_mechanism => "GSSAPI"
  	sasl_kerberos_service_name => "kafka"
  	jaas_path  => "/opt/jaas.conf"
        }

  }


  output {

  stdout{ codec => plain }

  }
```

- 登陆集群kafka客户端，创建topic，名字为：`logstashtest21007input`

- 使用命令`bin/logstash -f config/logstash-21007input.conf`启动logstash kafka input任务

- 登陆kafka客户端，使用命令`./bin/kafka-console-producer.sh --broker-list 172.16.10.131:21007,172.16.10.132:21007,172.16.10.133:21007 --topic logstashtest21007input --producer.config config/producer.properties`起一个kafka生产者，并输入一些数据：

  ![](assets/Logstash/markdown-img-paste-20200316165907771.png)

- 在logstash端检查结果：

  ![](assets/Logstash/markdown-img-paste-20200316170115426.png)


## logstash kafka output用例

- 在logsatash安装路径config下新建配置文件`logstash-21007output.conf`内容为：

```
# Sample Logstash configuration for creating a simple
# Beats -> Logstash -> Elasticsearch pipeline.

input { stdin{} }

output {
        kafka {
	bootstrap_servers => "172.16.10.131:21007"
        codec => json
        topic_id => "logstashtest21007"
	security_protocol => "SASL_PLAINTEXT"
	sasl_mechanism => "GSSAPI"
	sasl_kerberos_service_name => "kafka"
	jaas_path  => "/opt/jaas.conf"
      }

}

```

- 登陆集群kafka客户端，创建topic，名字为：`logstashtest21007`

- 使用命令`bin/logstash -f config/logstash-21007output.conf`启动logstash kafka output任务

- 登陆kafka客户端，使用如下命令起一个消费者：`bin/kafka-console-consumer.sh --topic logstashtest21007 --bootstrap-server 172.16.10.131:21007,172.16.10.132:21007,172.16.10.133:21007 --consumer.config config/consumer.properties`

- 在logstash端手动输入数据：

  ![](assets/Logstash/markdown-img-paste-20200316170750100.png)

- 去kafka客户端检查结果：

  ![](assets/Logstash/markdown-img-paste-20200316170833539.png)

## 关于使用ARM服务器使用logstash遇到的问题

在使用arm服务器使用logstash的时候，使用命令`bin/logstash -f config/logstash-sample.conf`
尝试启动logstash的时候遇到问题：

![](assets/Logstash/markdown-img-paste-2020031716485585.png)


```
[ERROR][org.logstash.Logstash    ] java.lang.IllegalStateException: Logstash stopped processing because of an error: (LoadError) load error: ffi/ffi -- java.lang.NullPointerException: null
```

参考社区：
1. https://github.com/elastic/logstash/issues/10888
2. https://github.com/elastic/logstash/issues/10755
3. https://gist.github.com/alexalouit/a857a6de10dfdaf7485f7c0cccadb98c
4. https://github.com/mew2057/CAST/blob/6c7f7d514b7af3c512635ec145aa829c535467dc/csm_big_data/config-scripts/logstashFixupScript.sh

可以得知问题原因为：logstash启动的时候读取的相关jar包jruby-complete-9.2.6.0.jar（以logstash 6.7.1为例）有配置问题。具体为jar包编译的时候对应aarch64-linux路径下缺少配置文件platform.conf



解决办法：

说明：解决办法参考社区，将logstash安装路径里的jar包jruby-complete-9.2.6.0.jar里面的路径`META-INF/jruby.home/lib/ruby/stdlib/ffi/platform/aarch64-linux`下的配置文件`types.conf`内容复制并保存为新的文件名`platform.conf`。 具体操作步骤如下：

1. 首先先在logstash安装路径中找到相关jar包的位置，比如：`/opt/logstash/logstash-6.7.1/logstash-core/lib/jars/jruby-complete-9.2.6.0.jar`

2. 使用winSCP工具将该jar包导出到windows本地

3. 右键点击该jar包，选择使用winRAR工具打开

  ![](assets/Logstash/markdown-img-paste-2020031616031209.png)

4. 首先登陆到路径`jruby-complete-9.2.6.0.jar\META-INF\jruby.home\lib\ruby\stdlib\ffi\platform`下

  ![](assets/Logstash/markdown-img-paste-20200316160543752.png)

  登陆路径`x86-linux`,选中platform.conf文件使用`Ctrl + C`复制该配置文件

  ![](assets/Logstash/markdown-img-paste-20200316160720760.png)

  登陆到路径`aarch64-linux`使用`Ctrl + V`将上一步复制的配置文件粘贴到该路径下

  ![](assets/Logstash/markdown-img-paste-20200316160949855.png)

  双击`type.conf`文件，将该配置文件的内容复制，并替换到`platform.conf`文件中并保存，使得两个文件的内容一致

  ![](assets/Logstash/markdown-img-paste-20200316161250516.png)

5. 将修改好的jar包使用winSCP工具重新导入到logstash的`/opt/logstash/logstash-6.7.1/logstash-core/lib/jars/`中，并替换之前的jar包

  ![](assets/Logstash/markdown-img-paste-20200316161535433.png)

6. 重新使用命令`bin/logstash -f config/logstash-sample.conf`启动成功

  ![](assets/Logstash/markdown-img-paste-20200316161719213.png)

  注意：可忽略该报错，因为没有填入有效es信息
