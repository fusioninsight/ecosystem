# Logstash对接FusionInsight HD ES组件

## 适用场景

> Logstash 6.4.2 <--> FusionInsight HD V100R002C80SPC200 (ElasticSearch组件非安全模式)

### 前提条件

- 已完成FusionInsight HD和客户端的安装。
- FusionInsight HD包含ElasticSearch组件

### 操作步骤

- 登录FusionInsight Manager网页，检查ES组件是否为安全模式，如果是，修改配置使其为非安全模式，完成后点击保存配置重启elasticsearch服务：

  ![](assets/Elasticsearch_related/markdown-img-paste-20181115174917852.png)

- 下载logstash 6.4.2, 网址为：https://www.elastic.co/downloads/past-releases

  ![](assets/Elasticsearch_related/markdown-img-paste-20181115175339258.png)

- 将下载后的**logstash-6.4.2.zip**使用WinSCP导入主机的`/opt/logstash`路径下，使用`unzip logstash-6.4.2.zip`解压安装包

  ![](assets/Elasticsearch_related/markdown-img-paste-20181115175958121.png)

- 到路径`/opt/logstash/logstash-6.4.2/config`下创建一个新的配置文件`logstash-Simple.conf`,内容如下：

```
# Sample Logstash configuration for creating a simple
# Beats -> Logstash -> Elasticsearch pipeline.

input{ stdin{ } }


output {

  stdout{
    codec => dots {}
  }
  elasticsearch {
    hosts => ["http://172.21.3.101:24100"]
    index => "hellow_world"
    #user => "elastic"
    #password => "changeme"
  }
}
```

- 到路径`/opt/logstash/logstash-6.4.2`下，执行命令`bin/logstash -f config/logstash-Simple.conf`根据之前的`logstash-Simple.conf`配置文件的内容来启动logstash, 然后在终端手动输入数字1到6：

![](assets/Elasticsearch_related/markdown-img-paste-20181115181701631.png)

- 登录elasticsearch-head服务器查看结果

  ![](assets/Elasticsearch_related/markdown-img-paste-20181115183039111.png)


# Kibana对接FusionInsight HD ES组件

## 适用场景
> Kibana 6.1.3 <--> FusionInsight HD V100R002C80SPC200 (ElasticSearch组件非安全模式)

### 前提条件

- 已完成FusionInsight HD和客户端的安装。
- FusionInsight HD包含ElasticSearch组件

### 操作步骤

- 登录FusionInsight Manager网页，检查ES组件是否为安全模式，如果是，修改配置使其为非安全模式，完成后点击保存配置重启elasticsearch服务：

  ![](assets/Elasticsearch_related/markdown-img-paste-20181115174917852.png)

- 下载Kibana 6.1.3, 下载网址为：https://www.elastic.co/downloads/past-releases

  ![](assets/Elasticsearch_related/markdown-img-paste-20181116101430780.png)

- 将下载后的**Kibana 6.1.3**使用WinSCP导入主机的`/opt`路径下，使用`tar -xzf kibana-6.1.3-linux-x86_64.tar.gz`解压安装包

  ![](assets/Elasticsearch_related/markdown-img-paste-20181116110006717.png)

- 使用`vi /opt/kibana-6.1.3-linux-x86_64/config/kibana.yml`添加如下配置选项:

  ```
  server.port: 5601
  server.host: "172.16.52.190"
  server.name: "LinuxTest"
  elasticsearch.url: "http://172.21.3.101:24100"
  ```

  ![](assets/Elasticsearch_related/markdown-img-paste-20181116110230773.png)

- 使用`bin/kibana`启动kibana

  ![](assets/Elasticsearch_related/markdown-img-paste-20181116110635370.png)


- 访问kibana登录界面，访问地址格式为http://Kibana服务IP地址:5601

  ![](assets/Elasticsearch_related/markdown-img-paste-20181116110818458.png)

  选择**Dev Tools**

  使用`GET _cat/indices`命令查看集群ES中的indices

  ![](assets/Elasticsearch_related/markdown-img-paste-20181116111117754.png)

  使用`GET hellow_world/_search`命令查看结果

  ![](assets/Elasticsearch_related/markdown-img-paste-20181116111209445.png)

# elasticsearch-head对接FusionInsight HD ES组件

## 适用场景

> elasticsearch-head <--> FusionInsight HD V100R002C80SPC200 (ElasticSearch组件非安全模式)

### 前提条件

- 已完成FusionInsight HD和客户端的安装。
- FusionInsight HD包含ElasticSearch组件

### 操作步骤

- 登录FusionInsight Manager网页，检查ES组件是否为安全模式，如果是，修改配置使其为非安全模式，完成后点击保存配置重启elasticsearch服务：

  ![](assets/Elasticsearch_related/markdown-img-paste-20181115174917852.png)

- 在FusionInsight Manager，选择服务管理->Elasticsearch服务配置->服务配置(选择全部配置)->自定义，在elasticsearch.yml文件中添加下面两个配置项，完成后点击保存配置重启elasticsearch服务

  ```
  http.cors.enabled = true
  http.cors.allow-origin = "*"
  ```

  ![](assets/Elasticsearch_related/markdown-img-paste-20181116113332837.png)

- 使用如下命令下载并且安装**elasticsearch-head**到主机上,并且启动**elasticsearch-head**服务

  ```
  git clone git://github.com/mobz/elasticsearch-head.git
  cd elasticsearch-head
  npm install
  npm run start
  ```

  ![](assets/Elasticsearch_related/markdown-img-paste-20181116113541295.png)

- 访问elasticsearch-head界面，访问地址为http://elasticsearch-head服务IP地址:9100, 输入连接信息为`http://172.21.3.101:24100/`,点击连接:

  ![](assets/Elasticsearch_related/markdown-img-paste-2018111611373686.png)

- 查看结果

  ![](assets/Elasticsearch_related/markdown-img-paste-20181116113832137.png)
