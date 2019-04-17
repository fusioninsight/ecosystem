## This Instruction will cover the FI solutions for the following four Application Cases:

Interactive Query

Offline Processing

Real-Time Processing

Real-Time Retrieval

GraphBase

DEMO2.0


## FI development Preparation

### Purpose

Download the sample code of FI development

Prepare the Maven Environment for development



### Prerequisites

**Git Bash** need to be installed if you are a windows user

### Procedure


**Download the SampleCode**

- come to the following URL `https://github.com/fusioninsight/fusioninsight_hd_examples/tree/V100R002C80` to download the source code, choose V100R002C80 as the Branch

  ![](assets/FI_development_instruction/markdown-img-paste-20190122175422482.png)

- click **Clone or download** button, choose Clone with HTTPS and copy the URL by click on the button

  ![](assets/FI_development_instruction/markdown-img-paste-20190122180046168.png)

- go to the code folder, for example, `C:\FI_HD_solutions_216`, right click and choose **Git Bash Here**

  ![](assets/FI_development_instruction/markdown-img-paste-20190211095600661.png)

- type the following command `git clone https://github.com/fusioninsight/fusioninsight_hd_examples.git` to download the sample code

  ![](assets/FI_development_instruction/markdown-img-paste-20190211100106131.png)

- come into downloaded folder **fusioninsight_hd_examples** by using command `cd fusioninsight_hd_examples/`

- Switch the branch into V100R002C80 by using command `git checkout V100R002C80`

  ![](assets/FI_development_instruction/markdown-img-paste-20190211100230668.png)

- check the sample code folder

  ![](assets/FI_development_instruction/markdown-img-paste-20190211100303567.png)


**Prepare the Maven Environment for development**

- create **.m2** folder for **setting.xml** configuration file

  right click on the desktop and choose  **Git Bash Here **, type in the following command `mkdir ~/.m2`

  ![](assets/FI_development_instruction/markdown-img-paste-20190123174917410.png)

  check the folder at `C:\Users\Administrator`

  ![](assets/FI_development_instruction/markdown-img-paste-2019012317495444.png)

  copy the **setting.xml** file where located at `C:\FI_HD_solutions\fusioninsight_hd_examples\setting.xml` into the created **.m2** folder

  ![](assets/FI_development_instruction/markdown-img-paste-20190123175439204.png)

  If you are not a user within Huawei internal internet, open the **settings.xml** file and delete the proxies part shown as bellow and save it

  ![](assets/FI_development_instruction/markdown-img-paste-20190123175652341.png)

  Change the value of localRepository from `D:\apache-maven\localRepo` into `C:\apache-maven\localRepo` if you only have one hard disk

  ![](assets/FI_development_instruction/markdown-img-paste-20190123182039864.png)


## Interactive Query


### Case Instruction

**Scenarios Description**

In a public security project, FusionInsight obtains fund flow information of bank accounts, analyzes transaction data to identify the accounts with abnormal fund flows, and further determines whether money laundering exists.

**Data Planning**

Data source design:

- Bank transaction information: Bank account, account name, ID number, transaction date, transaction amount, transaction type (online or offline banking transfer), target account, target account name, target account ID number, and remarks

Theme library design:

- Account information: Bank account, account name, and ID number

- Transaction information library: Bank account, transaction date, transaction amount, transaction type, target account, and remarks

- Money-laundering suspicion library: Bank ID and identification date

Query:

Operators can execute SQL statements to query information, for example, the transaction times, transaction limit, and transaction object of an account in a specified period.

**Preset Data**

Bank transaction information

- Import the bank account, account name, ID number, transaction date, transaction amount, transaction type (online or offline banking transfer), target account, target account name, target account ID number, and remarks to HDFS.

  The following is an example, and the data is separated by pipe characters (|):

  **1112|jack|23265656|2018-12-03|500|InterBank|154564654|dsas|4856445445|bei**


**Development Idea**



Data Source: Including streaming data, batch files, database etc... In this case, transaction data were loaded into hdfs of FI HD, and transfer these data into SparkSQL by **kafka-SparkStreaming** as the streaming format

Data collection: In general, data were processed and loaded in real-time by using Kafka and Spark streaming

Interactive Query: Using SparkSQL

In this case, we choose **HDFS, Kafka, SparkStreaming, SparkSQL** to become the required components within FI HD

![](assets/FI_development_instruction/markdown-img-paste-20190124161932729.png)

1. Put the transaction data into HDFS

2. load the data from HDFS into SparkStreming by using Kafka

3. SparkStreaming receive the data from Kafka and send it to SparkSQL

4. Process the data by using SaprkSQL, and save the processed data into other Storage Medium, for example, HDFS or Hive


**Configuration Preparation**

required configuration files:

krb5.conf：kerberos configuration file

user.keytab：kerberos configuration file

hdfs-site.xml：HDFS configuration file

core-site.xml：HDFS configuration file

spark-defaults.conf：Spark configuration file

producer.properties：kafka producer configuration file

consumer.properties：kafka consumer configuration file


**Location of Configuration files**

Configuration files need to located at the resource directory of the solution project.

**Module design**

Sample solution code mainly relate two class

1. com.huawei.bigdata.kafkaTospark.HDFSToSpark.HdfsToSpark

  read data from HDFS and insert the record into Kafka queue

2. com.huawei.bigdata.kafkaTospark.HDFSToSpark.KafkaStream：

  consume the record from kafka

  receive the data from SparkStreaming

  create the table by SparkSQL

3. com.huawei.bigdata.kafkaTospark.LoginUtil.\*：

  KERBEROS login

**Sample code description**

The sample path is: ..\solutions\interactiveQuery\InteractiveQueryJavaSparkSQL

```
public static void main(String[] args) throws Exception,Throwable
 {
      UserLogin();
​
      SparkSession spark = SparkSession.builder().master("local").appName("spark core").getOrCreate();
      Dataset data = spark.read().textFile("/zwl/KafkaToSparkSql/test.txt");
      JavaRDD<String> rdd =data.javaRDD();
      Properties property = new Properties();
      property.load(new FileInputStream(HdfsToSpark.class.getClassLoader().getResource("producer.properties").getPath()));
      property.put("acks", "all"); //After the server saves successfully, it returns a response. 0: no server is required, 1: a copy, all: all copies
      property.put("retries", 1); //Number of retries after a message fails to be sent
      property.put("batch.size", 16384); //The threshold for sending messages to the same partition in batches. When this value is reached, it will be sent.
      property.put("linger.ms", 10);//Interval of each message sent
      property.put("buffer.memory", 33554432);//Save the cache size of the message that will be sent to the server in the future.
      //The four parameters of the sending interval, the number of partitions, the threshold for each partition batch, and the buffer size need to be considered in combination with hardware configuration, system response delay, and bandwidth usage.
      property.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer"); //Determine the serialization method based on the data type
      property.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer"); //Determine the serialization method based on the data type
       KafkaProducer<String,String> producer = new KafkaProducer<String,String>(property);
      for(String t: rdd.collect()){
         producer.send(new ProducerRecord<String, String>("test-topic","inputdata", t));
          try {sleep(1000);} catch (Exception e) {LOG.error(e.toString());}
          LOG.info("The Producer have send {} messages"+t);
     }
  spark.stop();
 }
  public static void UserLogin() throws Exception{
      Configuration conf = new Configuration();
      conf.addResource(new Path(HdfsToSpark.class.getClassLoader().getResource("core-site.xml").getPath()));
      conf.addResource(new Path(HdfsToSpark.class.getClassLoader().getResource("hdfs-site.xml").getPath()));
​
      if ("kerberos".equalsIgnoreCase(conf.get("hadoop.security.authentication")))
     {
          //Authentication related, security mode required, normal mode can be deleted
          String PRNCIPAL_NAME = "lyysxg";//Need to be modified to actually add users to the manager
          String KRB5_CONF = HdfsToSpark.class.getClassLoader().getResource("krb5.conf").getPath();
          String KEY_TAB = HdfsToSpark.class.getClassLoader().getResource("user.keytab").getPath();
          System.setProperty("java.security.krb5.conf", KRB5_CONF); //Specify the kerberos configuration file to the JVM
          LoginUtil.setJaasFile("lyysxg",KEY_TAB);
          LoginUtil.setKrb5Config(KRB5_CONF);
          LoginUtil.setZookeeperServerPrincipal("zookeeper/hadoop.hadoop.com");
          LoginUtil.login(PRNCIPAL_NAME,KEY_TAB,KRB5_CONF,conf);
     }
 }
 ```

 Receive data via SparkStreamming, consume kafka queue messages, and create tables via SparkSQL:

 ```

  public static void main(String[] args) throws Exception {
      HdfsToSpark.UserLogin();
​
      Properties properties = new Properties();
      properties.load(new FileInputStream(KafkaStream.class.getClassLoader().getResource("consumer.properties").getPath()));
      String brokers = "189.211.69.32:21007,189.211.68.235:21007,189.211.68.223:21007";
      String topics = "test-topic";
      String batchTime = "3";
      String groupId = "testGroup";
      SparkConf sparkConf = new SparkConf().setAppName("KafkaToESAndSparkStream").setMaster("local[2]");
      JavaStreamingContext jsc = new JavaStreamingContext(sparkConf, new Duration(Long.parseLong(batchTime) * 1000));
​
      String[] topicArr = topics.split(",");
      Set<String> topicSet = new HashSet<String>(Arrays.asList(topicArr));
      Map<String, Object> kafkaParams = new HashMap();
      kafkaParams.put("bootstrap.servers", brokers);
      kafkaParams.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
      kafkaParams.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
      kafkaParams.put("group.id", groupId);
      // kafkaParams.put("enable.auto.commit", "true");
      // kafkaParams.put("auto.commit.interval.ms", "100"); //Offset auto-commit interval
      // Configure the certification of kafka consumer. Run the kafkaUtils.createDirectStream code to authenticate data before it can consume data.
      kafkaParams.put("security.protocol", "SASL_PLAINTEXT");
      kafkaParams.put("sasl.kerberos.service.name", "kafka");
​
      //Set the PreferConsistent mode to evenly allocate partitions.
      LocationStrategy locationStrategy = LocationStrategies.PreferConsistent();
      //Customize the consumer. The Subscribe method submits a list of parameters and the processing of the kafka parameters.
      ConsumerStrategy consumerStrategy = ConsumerStrategies.Subscribe(topicSet, kafkaParams);
      JavaInputDStream<ConsumerRecord<String, String>> messages = KafkaUtils.createDirectStream(jsc, locationStrategy, consumerStrategy);
      JavaDStream<String> lines = messages.map(new Function<ConsumerRecord<String, String>, String>() {
          @Override
          public String call(ConsumerRecord<String, String> tuple2) {
              return tuple2.value();
         }
     });
      JavaDStream<String> filter = lines.filter(new Function<String, Boolean>() {
          @Override
          public Boolean call(String s) throws Exception {
              Boolean t = !s.equalsIgnoreCase("") || s.length() > 1;
              return t;
         }
     });
      CreateTable(filter);
​
      jsc.start();
      jsc.awaitTermination();
      Dataset<Row> result =spark.sql("select * from Acounct");
      List<String> results = result.javaRDD().map(new Function<Row, String>()
     {
          @Override
          public String call(Row row)
         {
              return row.getString(0) + "," + row.getLong(1);
         }
     }).collect();
​
      spark.stop();
 }
```


### Development Procedure

**Import the project**

- open IntelliJ IDEA and click on Import Project by click on the pom.xml file within the solution sample code folder

  ![](assets/FI_development_instruction/markdown-img-paste-20190124170135230.png)


- click on File -> Project Structure -> SDKs -> + -> choose JDK

  choose the installed directory of your JDK

  ![](assets/FI_development_instruction/markdown-img-paste-20190124170450707.png)


**Compile the project**

- Click on view -> Tool windows -> maven -> maven window shows off

  ![](assets/FI_development_instruction/markdown-img-paste-20190124170900608.png)


- open lifeCycle -> double click compile to compile the project

  ![](assets/FI_development_instruction/markdown-img-paste-20190124171100151.png)

  Maven Compile outcome

  ![](assets/FI_development_instruction/markdown-img-paste-20190124120941289.png)

- Copy the following configuration files into resource directory from FI HD client:

  krb5.conf：kerberos configuration file

  user.keytab：kerberos configuration file

  hdfs-site.xml：HDFS configuration file

  core-site.xml：HDFS configuration file

  spark-defaults.conf：Spark configuration file

  producer.properties：kafka producer configuration file

  consumer.properties：kafka consumer configuration file

- Run KafkaStream.java at first and then run HDFSToSpark.java

  check the outcome

  ![](assets/FI_development_instruction/markdown-img-paste-20190124171516850.png)

  ![](assets/FI_development_instruction/markdown-img-paste-20190124171550524.png)



## Real-Time Retrieval


**Scenarios Description**

The police need to collect the real-time data from the Hotel, Internet bar, and Bayonet etc... And then save the data into a Person Info System. Identify the person by ID, driver license, car plate number quickly if needed in some situations.


![](assets/FI_development_instruction/markdown-img-paste-20190128104157146.png)


For Example: Take a person's name and timeline as the principal key, query this person's basic info and web activity and related hotel, trace info


**Data source summary**

Real-time data from Hotel, Internet bar, and Bayonet. Data format include text, pic, and video. (For the convenience, all the sample data are txt format)

1. Hotel data: name, ID, age, sex, Hotel Address, Check-in date, check out date, partner

2. Internet bar data: name, ID, age, sex, Internet Bar Address, check in data, duration

3. Bayonet check: name, ID, age, sex, Bayonet address, check the date, cate(self-driving, ride, walk)

**Development Design**

From the business scenario side, it is a typical real-time retrieval situation with the following characteristics

- high concurrency (50- 100 concurrency)

- high Query speed (within 1 sec)

- high volume of processed data (PB level)

- structured and unstructured data (text, video, pic)

- relative simple query (80% for principal key)

- full-text search

- real-time retrieval

**real-time retrieval** mainly means that data was written in realtime, using simple query(mainly about principal key) to search within a large volume of data

We can take the following scheme:


1. The customer's data is sudden and unstable of peaks and valleys, collect peak elimination data by Kafka is a good choice

  The Kafka component features message persistence, high throughput, distributed, multi-client support, real-time, etc., suitable for offline and online message consumption, such as regular message collection, website activity tracking, aggregate statistical system operation data (monitoring data). Data collection scenarios for Internet services that collect large amounts of data, such as logs.

2. Query response time can be within seconds by using a combination of Spark streaming and Kafka to consume the data and Batch Processing

  Spark Streaming is also a stream computing engine. Receive real-time input data streams, which can be split into batch data and processed by SparkCore to get the final desired data or results.

3. Create indexed key data by using Elasticsearch (ES) components for fast full-text search via a simple RESTful API approach

  Elasticsearch can build full-text indexing, support real-time retrieval, nodes can be extended to hundreds, and can handle structured or unstructured data at the PB level. High reliability, good retrieval performance, and efficient and can support simple full-text search operations through a simple RESTful API. It is ideal for full-text search, result content recommendation, analysis, and statistical aggregation.

4. Use HBase components to store data, using Java's API interface for quick queries based on primary keys:

  HBase is a highly reliable, high performance, column-oriented, scalable distributed storage system. HBase is suitable for storing large table data (tables can reach billions of rows and millions of columns), and read and write access to large table data can reach real-time levels.

**Solution architecture**

![](assets/FI_development_instruction/markdown-img-paste-20190128121126550.png)

1. Use Kafka's producer client to write the read source data (hotel accommodation, Internet bar access, card gate identity check data) to the message queue;

2. The data in the message queue is read out by Kafka's consumer and SparkStreaming and converted into DStream data format (for example, the key is the key value of each value in the hotel, Internet bar or bayonet message, and the raw data of each line is value). After batch processing, etc., the cleaned data is finally stored in HBase, and the index of key data is also created on the ES;

3. Simulate query operations based on query conditions through ES Rest API and HBase


**Module design**

The sample code mainly involves 4 classes, which implement the following functions:

1. com.huawei.bigdata.esandhbase.example.DataProducer：

  - Read real-time source data and insert Kafka message queue


2. com.huawei.bigdata.esandhbase.example.KafkaStreaming：

  - Consume Kafka queue message
  - Clean the data
  - Obtain the ES client and index the key basic data of the person
  - Connect to HBase, create a table, and save data to the table.


3. com.huawei.bigdata.esandhbase.example.ESSearch：

  - Simulated query operation: Quickly retrieve basic information of relevant personnel through ES according to name conditions


4. com.huawei.bigdata.esandhbase.example.HbaseQuery：

  - Simulated query operation: Quickly retrieve basic information of relevant personnel through ES according to name conditions


**Sample code description**

preparation

The configuration files in the sample code for this path../solutions/realtimeRetrieval/RealtimeRetrieval_java/data


Modify the configuration files in the resources directory, including:

1. consumer.properties

2. producer.properties

3. es-example.properties

4. jaas.conf：


Modify the inputPath, checkPath, topic, bootstrap.servers, EsServerHost, keyTab, and other related information involved in these four files.

Replace the cluster configuration files in the resources directory, including:

1.          core-site.xml

2.          hbase-site.xml

3.          hdfs-site.xml

4.          user.Keytab

5.          krb5.conf

6.          spark-defaults.conf

7.          spark-env.sh

**Code sample**

The code snippet is just a demonstration. For the specific code, see the com.huawei.bigdata.esandhbase.example.DataProducer class:

```
​
public static void main(String args[]) throws Exception {

       // Load the client configuration file downloaded from the cluster and connect to the server.
       producerProps.load(new FileInputStream(DataProducer.class.getClassLoader().getResource("producer.properties").getPath()));
       //Producer business code
       //Configure the customized configuration parameters of the current Producer.
       producerProps.put("acks", "all"); //After the server saves successfully, it returns a response. 0: no server is required, 1: a copy, all: all copies
       producerProps.put("retries", 1); //Number of retries after the producer failed to send
       producerProps.put("batch.size", 16384); //The threshold for sending messages to the same partition in batches. When this value is reached, it will be sent.
       producerProps.put("linger.ms", 100);//Interval of each message sent
       producerProps.put("buffer.memory", 33554432);//Save the cache size of the message that will be sent to the server in the future.
       //The four parameters of the transmission interval, the number of partitions, the threshold for each partition batch, and the buffer size need to be considered in combination with hardware configuration, system response delay, and bandwidth usage.
       producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer"); //Determine the serialization method based on the data type
       producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer"); //Determine the serialization method based on the data type
       // Protocol type: The current support configuration is SASL_PLAINTEXT or PLAINTEXT
       producerProps.put("security.protocol",  "SASL_PLAINTEXT");
       // Service Name
       producerProps.put("sasl.kerberos.service.name", "kafka");
​
       //Create a producer based on the configuration file, the process of creation will be Kerberos authentication. The authentication process is implemented internally by the Kafka API.
       KafkaProducer producer = new KafkaProducer<Integer, String>(producerProps);
       //Produce news to testTopic
       String filePath = producerProps.getProperty("inputPath");
       String topic = producerProps.getProperty("topic");
       for (int m = 0; m < Integer.MAX_VALUE / 2; m++) {
           File dir = new File(filePath);
           File[] files = dir.listFiles();
           if (files != null){
               for(File file : files){
                   if(file.isDirectory()){
                       System.out.println(file.getName() + "This is a directory!");
                  }else{
                       if (file.getName().contains("hotel")){
                           BufferedReader reader = null;
                           reader = new BufferedReader(new FileReader(filePath+file.getName()));
                           String tempString = null;
                           while ((tempString = reader.readLine()) != null) {
                               //Blank line judgment
                               if (!tempString.isEmpty()) {
                                   ProducerRecord producerRecord = new ProducerRecord<String, String>(topic, "hotel", tempString);
                                   LOG.info("hotel_info:" +tempString);
                                   producer.send(producerRecord);
                              }
                          }
​
                           reader.close();
                      }
                       else if(file.getName().contains("internet")){
                           BufferedReader reader = null;
                           reader = new BufferedReader(new FileReader(filePath+file.getName()));
                           String tempString = null;
                           while ((tempString = reader.readLine()) != null) {
                               //Blank line judgment
                               if (!tempString.isEmpty()) {
                                   ProducerRecord producerRecord = new ProducerRecord<String, String>(topic, "internet", tempString);
                                   LOG.info("internet_info:" +tempString);
                                   producer.send(producerRecord);
                              }
                          }
                           reader.close();
​
                      }else{
                           BufferedReader reader = null;
                           reader = new BufferedReader(new FileReader(filePath+file.getName()));
                           String tempString = null;
                           while ((tempString = reader.readLine()) != null) {
                               //Blank line judgment
                               if (!tempString.isEmpty()) {
                                   ProducerRecord producerRecord = new ProducerRecord<String, String>(topic, "bayonet", tempString);
                                   LOG.info("bayonet_info:" +tempString);
                                   producer.send(producerRecord);
                              }
                          }
                           reader.close();
                      }
                  }
              }
          }
           try
          {
               Thread.sleep(3000);
          }
           catch (InterruptedException e)
          {
               e.printStackTrace();
          }
      }
       producer.close();
  }
```


The following code snippet is for demonstration purposes only. See the com.huawei.bigdata.esandhbase.example.KafkaStreaming class for specific code:

```
public static void main(String[] args)throws Exception {

       //Load spark configuration
       //Create a Streaming boot environment.
       SparkConf sparkConf = new SparkConf().setAppName("KafkaToESAndHBase").setMaster("local[2]");
       //The streaming context, which is executed every few seconds to get the bulk data and then process the data
       JavaStreamingContext jsc = new JavaStreamingContext(sparkConf, new Duration(Long.parseLong(batchTime) * 1000));
       //Configure the CheckPoint directory for Streaming. It will check enough information to some fault-tolerant storage systems such as hdfs so that it can recover quickly if something goes wrong.
       jsc.checkpoint(properties.getProperty("checkPath"));
​
       //Get a list of topics used by kafka
       String[] topicArr = topics.split(",");
       Set<String> topicSet = new HashSet<String>(Arrays.asList(topicArr));
       Map<String, Object> kafkaParams = new HashMap();
       kafkaParams.put("bootstrap.servers", brokers);
       kafkaParams.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
       kafkaParams.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
       kafkaParams.put("group.id", groupId);
       kafkaParams.put("enable.auto.commit", "true");
       kafkaParams.put("auto.commit.interval.ms", "100"); //Offset auto-commit interval
​
       // Configure the certification of kafka consumer. Run the kafkaUtils.createDirectStream code to authenticate data before it can consume data.
       kafkaParams.put("security.protocol","SASL_PLAINTEXT");
       kafkaParams.put("sasl.kerberos.service.name","kafka");
​
       //Set the PreferConsistent mode to evenly allocate partitions.
       LocationStrategy locationStrategy = LocationStrategies.PreferConsistent();
       //Customize the consumer. The Subscribe method submits a list of parameters and the processing of the kafka parameters.
       ConsumerStrategy consumerStrategy = ConsumerStrategies.Subscribe(topicSet, kafkaParams);
​
       //Receive data from Kafka and generate the corresponding DStream (the DStream operation will eventually be converted to the underlying RDD operation)
       JavaInputDStream<ConsumerRecord<String, String>> messages = KafkaUtils.createDirectStream(jsc, locationStrategy, consumerStrategy);
​
       messages.foreachRDD(
           new VoidFunction<JavaRDD<ConsumerRecord<String, String>>>() {
               @Override
               public void call(JavaRDD<ConsumerRecord<String, String>> consumerRecordJavaRDD) throws Exception {
                   //Use the foreachPartition method to work concurrently on each partition
                   consumerRecordJavaRDD.foreachPartition(
                       new VoidFunction<Iterator<ConsumerRecord<String, String>>>() {
                            @Override
                            public void call(Iterator<ConsumerRecord<String, String>> consumerRecordIterator) throws Exception {
                                 hbaseAndESWrite(consumerRecordIterator);
                            }
                      }
                    );
              }
          }
      );
       // Spark Streaming start
       jsc.start();
       jsc.awaitTermination();
​
       if (table != null) {
           try {
               table.close();
          } catch (IOException e) {
               e.printStackTrace();
          }
      }
       if (admin != null) {
           try {
               admin.close();
          } catch (IOException e) {
               e.printStackTrace();
          }
      }
       if (conn != null) {
           try {
               // Close the HBase connection.
               conn.close();
          } catch (IOException e) {
               e.printStackTrace();
          }
      }
       //After completing the Elasticsearch operation, you need to call "restClient.close()" to close the requested resource.
       if( restClient!=null) {
           try {
               restClient.close();
               LOG.info("Close the client successful in main.");
          } catch (Exception e1) {
               LOG.error("Close the client failed in main.",e1);
          }
      }
  };
​
```

### Development Procedure

**Import the project**

- open IntelliJ IDEA and click on Import Project by click on the pom.xml file within the solution sample code folder

  ![](assets/FI_development_instruction/markdown-img-paste-20190124170135230.png)


- click on File -> Project Structure -> SDKs -> + -> choose JDK

  choose the installed directory of your JDK

  ![](assets/FI_development_instruction/markdown-img-paste-20190124170450707.png)

**Compile the project**

- Click on view -> Tool windows -> maven -> maven window shows off

  ![](assets/FI_development_instruction/markdown-img-paste-20190124170900608.png)


- open lifeCycle -> double click compile to compile the project

  ![](assets/FI_development_instruction/markdown-img-paste-20190124171100151.png)

  Maven Compile outcome

  ![](assets/FI_development_instruction/markdown-img-paste-20190124120941289.png)


- replace and modify the configuration files covered in the former section:

  1. consumer.properties

  2. producer.properties

  3. es-example.properties

  4. jaas.conf：

  5.          core-site.xml

  6.          hbase-site.xml

  7.          hdfs-site.xml

  8.          user.Keytab

  9.          krb5.conf

  10.          spark-defaults.conf

  11.          spark-env.sh

  ![](assets/FI_development_instruction/markdown-img-paste-20190128143651428.png)

- start KafkaStreaming first and then start DataProducer

  check the outcome on the terminal

  ![](assets/FI_development_instruction/markdown-img-paste-20190310172701808.png)

  ![](assets/FI_development_instruction/markdown-img-paste-20190310172802542.png)

- check the query outcomes by start ESSearch and HbaseQuery

  ![](assets/FI_development_instruction/markdown-img-paste-2019031017351804.png)

  ![](assets/FI_development_instruction/markdown-img-paste-20190310174608964.png)


## Real-Time Processing

**Scenarios Description**

On the day of the Double Eleven Shopping Festival, the volume of transactions surged, and an e-commerce company needed to display the total sales of various kinds of commodities in real time on the dashboard.

- Real-time data collection: Real-time access to trading orders generated by APP, PC, and other channels.

- Data real-time processing: Screen out the orders that are successfully paid, and statistically rank the total sales of various commodities.

- The results show in real time: the total sales statistics of various kinds of commodities are displayed.

  ![](assets/FI_development_instruction/markdown-img-paste-20190128150153393.png)

**Data source description**

Transaction information: product category, order payment success tag, price, order time

For example:

Electronic products,failure,128,2018-11-11 00:00:01

clothing,success,79,2018-11-11 00:00:02

food,success,179,2018-11-11 12:10:24

**Demand analysis and component selection**

From the perspective of business scenarios, this is a typical real-time stream processing  requirement with the following characteristics:

- Processing time is extremely high, in milliseconds

- Processing a huge amount of data, hundreds of megabits per second

- Occupy more computing resources

- Easy to generate computing resources to preempt

- Relatively simple task

- Data does not save into hard disk, storage is not large

Real-time stream processing usually refers to a scenario where a real-time data source is quickly analyzed to trigger the next action. Real-time data requires extremely high analysis and processing speed, and the data processing scale is huge. The CPU and memory requirements are very high, but usually, the data does not fall to the ground, and the storage capacity is not high. Real-time processing, usually through Spark Streaming or Flink tasks.


We can take the following scheme:

1. Message middleware: Message middleware caches real-time data and supports high-throughput message subscriptions and publishing.

  - Kafka: Distributed messaging system that supports the production and distribution of messages, as well as multiple forms of message caching for efficient and reliable message production and consumption.

2. Distributed Stream Computing Engine: Quickly analyze real-time data.

  - SparkStreaming: A Spark-based stream processing engine that supports stream processing analysis within seconds.

  - Flink: A new generation of stream processing engine that supports millisecond-level stream processing analysis.

  *Stream computing engine, preferred to use Flink*

3. Data cache display: The results of the stream processing analysis are cached to meet the access requirements of the stream processing application.

  - Redis: Provides high-speed key/value storage query capability for stream processing result data and is saved in caches.


**Solution architecture**

![](assets/FI_development_instruction/markdown-img-paste-20190128151247731.png)

1. Use FlinkKafkaProducer to write the acquired real-time transaction data to the message queue;

2. The FlinkKafkaConsumer reads the data in the message queue and converts it into a DataStream data structure, and performs a series of analysis and filtering calculation operations such as map, filter, keyBy, reduce, etc., and then stores the result data (product type, total sales) in Redis.

3. Check the total sales statistics of various products through JedisCluster


**Module design**

The Sample code mainly involves 2 classes, which implement the following functions:

1. com.huawei.bigdata.flink.examples.WriteIntoKafka：

  - Read real-time transaction data and write to the message queue


2. com.huawei.bigdata.flink.examples.ProcessDataAndSinkToRedis：

  - Consume kafka queue message

  - Analyze, filter, and calculate data

  - Get the JedisCluster client and store the result data (product type, total sales) in Redis

  - Check the total sales statistics of various products through JedisCluster

**Sample code description**

The following code snippet is for demonstration purposes only. See the com.huawei.bigdata.flink.examples.WriteIntoKafka class for specific code:

```
public static void main(String[] args) throws Exception
{
   //StreamExecutionEnvironment executes the context of the stream program. The environment provides methods to control job execution (such as setting parallelism or fault tolerance/checkpoint parameters) and interacting with the outside world (data access).
   // Construct the execution environment, the degree of parallelism of operations 1
   StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
   env.setParallelism(1);
   ParameterTool paraTool = ParameterTool.fromArgs(args);
   //SimpleStringGenerator generating data stream
   DataStream<String> messageStream = env.addSource(new SimpleStringGenerator());
   //Kafka producers produce news to testTopic
   messageStream.addSink(new FlinkKafkaProducer010<>(paraTool.get("topic"), new SimpleStringSchema(), paraTool.getProperties()));
   env.execute();
}
public static class SimpleStringGenerator implements SourceFunction<String> {
   //Product Type: Electronics, Apparel, Food, Cosmetics, Medicine
   static final String[] TYPE = {"Electronic products", "clothing","food", "cosmetic", "medicine"};
   //Payment success identifier
   static final String[] PAY_RESULT = {"success", "failure"};
   static final int COUNT = TYPE.length;
   boolean running = true;
   Random rand = new Random(47);
   @Override
   //Rand randomly generates order data: product type, payment result identification, price, order time
   public void run(SourceContext<String> ctx) throws Exception {
       while (running) {
           int i = rand.nextInt(COUNT);
           String payResult = PAY_RESULT[rand.nextInt(2)];
           //Randomly generated price 0-100
           int price = rand.nextInt(100);
​
           SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//Set date format
           String time = df.format(new Date());// New Date() is the current system time, but also the current timestamp.
           String data = TYPE[i] + "," +  payResult + "," + price + "," + time;
           ctx.collect(data);
           System.out.println(data);
           Thread.sleep(1000);
      }
  }
   @Override
   public void cancel() {
       running = false;
  }
}
```

The following code snippet is for demonstration purposes only. See the com.huawei.bigdata.flink.examples.ProcessDataAndSinkToRedis class for specific code:

```
public static void main(String[] args) throws Exception
{
   //StreamExecutionEnvironment executes the context of the stream program. The environment provides methods to control job execution (such as setting parallelism or fault tolerance/checkpoint parameters) and interacting with the outside world (data access).
   // Construct the execution environment, the degree of parallelism of operations 1
   StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
   env.setParallelism(1);
   ParameterTool paraTool = ParameterTool.fromArgs(args);
   //windowTime set window time worth size 1, (time unit is processed later, can be set: day, hour, minute, second, millisecond)
   final Integer windowTime = paraTool.getInt("windowTime", 1);
​
   DataStream<String> messageStream = env.addSource(new FlinkKafkaConsumer010<>(paraTool.get("topic"), new SimpleStringSchema(), paraTool.getProperties()));
   messageStream.map(new MapFunction<String, TransactionRecord>()
  {
       @Override
       public TransactionRecord map(String value) throws Exception
      {
           return getRecord(value);
      }
  }).assignTimestampsAndWatermarks(new Record2TimestampExtractor()).filter(new FilterFunction<TransactionRecord>()
  {
       @Override
       public boolean filter(TransactionRecord value) throws Exception
      {
           return value.payResult.equals("success");
      }
  }).keyBy(new TransactionRecordSelector()).window(TumblingEventTimeWindows.of(Time.seconds(windowTime))).reduce(new ReduceFunction<TransactionRecord>()
  {
       @Override
       public TransactionRecord reduce(TransactionRecord value1, TransactionRecord value2) throws Exception
      {
           value1.price += value2.price;
           return value1;
      }
  }).addSink(new RedisSink());
​
   env.execute();
}
//Insert the result of the flink calculation into redis
private static class RedisSink extends
       RichSinkFunction<TransactionRecord> {
   private transient JedisCluster client;
   private String redisKey = "RedisSinkTest";
​
   //The open method is an initialization method that is executed before the invoke method and executed once.
   public void open(Configuration parameters)throws Exception
  {
       super.open(parameters);
       if(client != null){
           System.out.println("Redis already connected......");
           return;
      }
       //Create a JedisCluster instance by specifying the IP and port number of one or more instances in the cluster.
       // Note that the configuration in the Const interface is changed to the IP and the corresponding port of the environment.
       Set<HostAndPort> hosts = new HashSet<HostAndPort>();
       hosts.add(new HostAndPort(Const.IP_1, Const.PORT_1));
       hosts.add(new HostAndPort(Const.IP_2, Const.PORT_2));
       // add more host...
​
       // Connection, request timeout duration, time unit ms
       int timeout = 5000;
       //JedisCluster encapsulates various operations for java access to the Redis cluster, including initializing connections, requesting redirects, and more.
       client = new JedisCluster(hosts, timeout);
​
       System.out.println("JedisCluster init success");
  }
​
   //Update data in real time and get a print display
   public  void invoke(TransactionRecord transactionRecord, SinkFunction.Context context) throws Exception {
       try {
​
           //Add an increment transactionRecord.price to the value of a transaction's transactionRecord.type
           client.zincrby(redisKey,transactionRecord.price, transactionRecord.type);
​
           //Get ranking data
           Set<String> setValues = client.zrevrange(redisKey, 0, -1);
           List<String> result = new ArrayList(setValues);
           StringBuffer sb = new StringBuffer();
           for (int i = 0; i < result.size(); i++) {
               sb.append("Top" +(i+1)+ " : "+ result.get(i) + "-" + client.zscore(redisKey, result.get(i)) + " ");
          }
           System.out.println(sb.toString());
​
      }catch (Exception e){
           e.printStackTrace();
      }
  }
​
   //Close() is the method of tear down, executed when destroyed, closes the connection
   public void close()throws Exception
  {
       super.close();
       if(client != null){
           System.out.println("Jediscluster close!!!");
           client.close();
      }
  }
}

```

### Development Procedure

**Import the project**

- open IntelliJ IDEA and click on Import Project by click on the pom.xml file within the solution sample code folder

  ![](assets/FI_development_instruction/markdown-img-paste-20190124170135230.png)


- click on File -> Project Structure -> SDKs -> + -> choose JDK

  choose the installed directory of your JDK

  ![](assets/FI_development_instruction/markdown-img-paste-20190124170450707.png)

**Compile the project**

- Click on view -> Tool windows -> maven -> maven window shows off

  ![](assets/FI_development_instruction/markdown-img-paste-20190124170900608.png)


- open lifeCycle -> double click compile to compile the project

  ![](assets/FI_development_instruction/markdown-img-paste-20190124171100151.png)

  Maven Compile outcome

  ![](assets/FI_development_instruction/markdown-img-paste-20190124120941289.png)

- Modify the IP and port number required to create a JedisCluster instance in com.huawei.bigdata.flink.examples.utils.Const.

  ![](assets/FI_development_instruction/markdown-img-paste-20190128160853121.png)

- click on File -> Project structure

  ![](assets/FI_development_instruction/markdown-img-paste-20190128154304926.png)

  Click on Artifacts -> + -> JAR -> Empty

  ![](assets/FI_development_instruction/markdown-img-paste-20190128154405544.png)

  Type the name of the artifact, for example, realtimeprocessing2, right click on **'RealTimeProcessingJava' Compile output** and choose **Put into Output Root**

  ![](assets/FI_development_instruction/markdown-img-paste-20190128154651721.png)

  choose Main Class as com.huawei.bigdata.flink.examples.WriteIntoKafka and click on OK

  ![](assets/FI_development_instruction/markdown-img-paste-20190128155944227.png)

  click on Build -> Build Artifacts -> realtimeprocessing2 -> Build

  ![](assets/FI_development_instruction/markdown-img-paste-20190128155155531.png)

  ![](assets/FI_development_instruction/markdown-img-paste-20190128155259777.png)

  check the compiled jar file:

  ![](assets/FI_development_instruction/markdown-img-paste-20190128155411188.png)

- Repeat all the steps of former bullet point with new artifact name as realtimeprocessing3 and Main Class as com.huawei.bigdata.flink.examples.ProcessDataAndSinkToRedis

  ![](assets/FI_development_instruction/markdown-img-paste-20190128155637832.png)

  check the compield jar file:

  ![](assets/FI_development_instruction/markdown-img-paste-2019012816003325.png)

- Reference product documentation of FI HD

    - Please navigate and read Application Development Guide -> Security Mode -> Flink Development Guide -> Preparing for Environment to prepare the Flink Environment for solution testing

    ![](assets/FI_development_instruction/markdown-img-paste-20190128160353627.png)

    - come to the flink linux client (eg: 172.22.17.41:/opt/hadoopclient/Flink/flink/bin) and type in the following command to generate password as Huawei@123

      ```
      sh generate_keystore.sh Huawei@123
      ```

    - open the flink-conf.yaml file by using command `vi /opt/hadoopclient/Flink/flink/conf/flink-conf.yaml` and make adjustments as follows:

    ![](assets/markdown-img-paste-20190312200147834.png)

    ![](assets/markdown-img-paste-20190312201540393.png)

    - copy the flink.keystore, flink.truststore files into **client's /opt directory**  and also copy to **cluster nodes' /opt directory** that match the configuration parameters shown on former pic

    - check the flink cluster by the following commands

    ```
    cd /opt/hadoopclient/Flink/flink
    bin/yarn-session.sh -n 3 -s 3 -jm 1024 -tm 1024
    ```

    ![](assets/markdown-img-paste-20190312212554833.png)

- Load realtimeprocessing2.jar and realtimeprocessing3.jar into FI HD client /opt directory by using WinSCP Tool(172.21.3.116)

  ![](assets/FI_development_instruction/markdown-img-paste-20190128161107294.png)

- Come into 172.21.3.116, Type in the following command for Kerberos authentication

  ```
  source /opt/hadoopclient/bigdata_env
  kinit
  ```

- First to start Flink cluster by type in the following command:

  ```
  cd /opt/hadoopclient/Flink/flink
  bin/yarn-session.sh -n 3 -s 3 -jm 1024 -tm 1024
  ```

  ![](assets/FI_development_instruction/markdown-img-paste-20190128162606200.png)

- Second is to open a new terminal and type in the following command to write data into kafka:

  ```
  cd /opt/hadoopclient/Flink/flink
  bin/flink run --class com.huawei.bigdata.flink.examples.WriteIntoKafka /opt/realtimeprocessing2.jar --topic testTopicFlink --bootstrap.servers 172.21.3.115:21005
  ```

  ![](assets/FI_development_instruction/markdown-img-paste-20190128162809133.png)

- Third is to open a new terminal and type in the following command to process the data and sink into redis:

  ```
  cd /opt/hadoopclient/Flink/flink
  bin/flink run --class com.huawei.bigdata.flink.examples.ProcessDataAndSinkToRedis /opt/realtimeprocessing3.jar  --topic testTopicFlink --bootstrap.servers 172.21.3.115:21005 --security.protocol PLAINTEXT --sasl.kerberos.service.name kafka
  ```

  ![](assets/FI_development_instruction/markdown-img-paste-20190128162920270.png)

- Check the outcome

  - log into FI Manager and find Yarn component

    ![](assets/FI_development_instruction/markdown-img-paste-20190128163133763.png)

  - click on Yarn component and click on ResourceManager

    ![](assets/FI_development_instruction/markdown-img-paste-20190128163207723.png)

  - check the status of flink cluster and click on ApplicationMaster

    ![](assets/FI_development_instruction/markdown-img-paste-20190128163314378.png)

  - Click on Task Manager and choose one of the tasks, click on Stdout to check the outcome

    ![](assets/FI_development_instruction/markdown-img-paste-20190128163455918.png)

    input:

    ![](assets/FI_development_instruction/markdown-img-paste-2019022009381748.png)

    output:

    ![](assets/FI_development_instruction/markdown-img-paste-20190220094003768.png)

    Redis Output on the backend:

    Login Redis shell backend on linux client by typing the following commands

    ```
    cd /home/redis_client/redisclient
    source bigdata_env
    cd Redis
    bin/redis-cli -h 172.21.3.101 -p 22400 -c --realm hadoop.com
    ```

    ![](assets/FI_development_instruction/markdown-img-paste-20190310190829780.png)

    check the dashboard by typing the following commands

    ```
    zrange RedisSinkTest 0 -1 withscores
    ```

    ![](assets/FI_development_instruction/markdown-img-paste-20190310190920502.png)

## Offline Processing

**Scenarios Description**

In order to achieve accurate marketing and advertising push, a certain XX e-commerce platform needs to image all users based on shopping data and product browsing records for nearly half a year.

- The e-commerce platform has 1000,000+ registered users

- The sales data record for the first half of the year is 1 billion, and the user's product browsing record is 50 billion.

**customer demand**

- Within six months, the purchase amount is greater than 10,000 yuan and marked as a gold medal user, and the latest products of the same kind are pushed according to the shopping record.

- A discount advertisement of the items which are viewed over 10 times but not bought is pushed to the user. If the user has browsed the record and has generated a shopping record for the same item, then no push is required.

- For users who purchase less than 1000 yuan, the coupon amount is 20% of the purchased amount, and the minimum coupon amount is 10 yuan.

**Customer-provided data**

- User information table:

  User ID, user name, registration time

  For Example:

  500001, liming, 2018-03-25

- Shopping information:

  User ID, product name, product category, item amount, and date of purchase.

  For Example:

  4154121, Feike razor, electrical, 98, 2018-03-25, 1507776195710

- Product browsing information:

  User ID, product name, browsing duration, browsing date

  For Example:

  500001, Haier refrigerator, 20, 2018-03-25

- Portrait information:

  - Gold users: user ID, user name, purchase amount

  - Advertising users: user ID, user name, promotional items

  - Coupon push user: user ID, user name, purchase amount, coupon amount


**Design and analysis**


According to the scenario and customer needs, you can get the following information:

- Handling large amounts of data, at the PB level.

- Real-time requirements are not high.

- Support multiple tasks to execute simultaneously.

- Occupy more computing storage resources.

- Support for querying data.

**Solution architecture**

- For data storage, we chose the distributed file system HDFS.

  Reason: Ten billion records, may reach PB level storage. HDFS is superior to storing files at the PB level.

- It is required to extract the data that the amount of consumption is greater than 10,000+ or less than 1000. This requires logical operations and data mining. We can choose Spark-SparkSQL or MapReduce-Hive.

  Reason: Spark-SparkSQL is based on memory. When Spark performs data calculation, it writes the intermediate result into memory. In SparkSQL, the Spark processing result is mapped into a table and stored in memory. Based on memory, it will greatly improve the speed of the data query.

  MapReduce-Hive is disk based. MR writes intermediate results to HDFS, which causes multiple I/O reads and writes. Hive maps the results of the MR into a table and stores it on disk. For the later query, speed has a great impact.

  Spark/SparkSQL is preferred when batch processing, but MapReduce/Hive can be used when there is an inventory application. Both modes can be used simultaneously.

  Here, we use the components of HDFS-Spark-SparkSQL

![](assets/markdown-img-paste-20190313094517302.png)


1. Save user e-commerce data to HDFS via Flume. (The specific code refers to Flume's single component example);

2. Spark loads data from HDFS and logically processes the data. For example, screen out users who spend more than 10,000 yuan.

3. After processing is complete, SparkSQL generates the data and stores it in memory.

4. SparkSQL provides an external interface for customers to query data

**Configuration files preparation**

In the sample code, we added the certification code. This is done for the convenience of testing locally. If the package is run on the server, the authentication code can be deleted


Krb5.conf: The user's configuration file for finding the verification component of Kerberos.

User.keytab: User profile for Kerberos authenticate.

Hdfs-site.xml: HDFS configuration file.

Core-site.xml: HDFS configuration file.

Spark-defaults.conf: Configuration file of Spark.

Use the Flume component to store data in the specified path of HDFS: /hacluster/myfile.

**Module design**

1. Read the shopping data information and the user registration information table, and add the same user name and the consumption amount item of the user ID.

2. The data is filtered, the generated gold medal user table whose consumption amount is greater than 10000, and the generated coupon push user table smaller than 1000.

3. Read the shopping information and browse the information table, and delete the information that has been viewed more than 10 times and has been purchased.

4. Combine the filtered information with the user registration information table to generate an advertisement push user table.

**Sample code explanation**

```
public static void main(String[] args) throws Exception
{
   //Used to authenticate and connect to HDFS. If you package it, you can delete this method.
   UserLogin();
​
   //Create an object to manipulate Spark. After Spark2.0, SparkSession was used instead of SparkConf and SparkContext.
   //appName is the name of the application to be easily found on Yarn.
   //Master is the specified mode of operation. If you use cluster, you need to delete the master, or you will get an error.
   SparkSession spark = SparkSession.builder().appName("spark core").master("local").getOrCreate();
   //Read files from HDFS, the path is the path on HDFS
   Dataset dealDataRDD = spark.read().textFile("/myfile/shooppingTable.txt");//Shopping data: user ID, product name, product category, product amount, date of purchase
   String path1 = "/myfile/userTable.txt";//Location on HDFS
   Dataset userDataRDD = spark.read().textFile(path1);//User information: user ID, user name, registration time
   String skimrecordPath="/myfile/browsingTable.txt";
   Dataset skimRecordRDD = spark.read().textFile(skimrecordPath);//User ID, product name, browsing duration, browsing date.
   //Convert the original data of the purchase to the form of K-V.
   JavaPairRDD<Integer, Tuple4<String ,String,Integer,String>> mapDealDataRDD = MapDealDataRDD( dealDataRDD);
   //Convert the user's original data into a K-V form
   JavaPairRDD <Integer,Tuple2<String ,String>> mapUserDataRDD = MapUserDataRDD(userDataRDD);
   //Call the method, get the time six months ago - converted to long, easy to compare
   final long   beforeTimeMillis = DateToBefor.BeforeTime();
   //Make a right connection and combine the shopping information table with the user information for easy screening.
   JavaPairRDD<Integer, Tuple2<Tuple4<String,String,Integer,String>,Optional<Tuple2<String,String>>>> joinDataDU = mapDealDataRDD.leftOuterJoin(mapUserDataRDD);
   //Filter out data items with a shopping date greater than half a year, and delete the columns we don't need. Only the (user ID, user name, spending amount) is left here.
   JavaPairRDD<Tuple2<Integer, String>, Integer> moneyCountInfo = MoneyCountInfo(joinDataDU,beforeTimeMillis);
   //The data items of the same user ID and user name are accumulated. Easy to filter and build gold medal user table
   JavaPairRDD<Tuple2<Integer ,String>,Integer> countSkimRDD = CountSkimRDD(skimRecordRDD);
   //Used to generate gold users
   GoldInfo(spark,moneyCountInfo);
   //User generated coupon push
   DiscountInfo(spark,moneyCountInfo);
   //Used to generate ad promotion users
   //4 parameters: the first operation Spark, the second shopping information table, used to filter the shopping information that has been viewed 10 times but has been bought, and the third parameter is used to obtain browsing information with more than 10 browsing times. Fourth parameter user information table
   SpreadInfo(spark,mapDealDataRDD,countSkimRDD,mapUserDataRDD);
   Dataset<Row> femaleTimeInfo = spark.sql("select * from DiscountInfo");
   List<String> result = femaleTimeInfo.javaRDD().map(new Function<Row, String>() {
       public String call(Row row) {
           return row.get(2)+","+row.get(3)+","+row.get(0)+","+row.get(1);
      }
  }).collect();
   System.out.println(result.size());
   for(int i =0;i<result.size();i++) {
       System.out.println(result.get(i));
  }
   spark.stop();
}
```

### Development Procedure

**Import the project**

- open IntelliJ IDEA and click on Import Project by click on the pom.xml file within the solution sample code folder

  ![](assets/FI_development_instruction/markdown-img-paste-20190124170135230.png)


- click on File -> Project Structure -> SDKs -> + -> choose JDK

  choose the installed directory of your JDK

  ![](assets/FI_development_instruction/markdown-img-paste-20190124170450707.png)

**Compile the project**

- Click on view -> Tool windows -> maven -> maven window shows off

  ![](assets/FI_development_instruction/markdown-img-paste-20190124170900608.png)


- open lifeCycle -> double click compile to compile the project

  ![](assets/FI_development_instruction/markdown-img-paste-20190124171100151.png)

  Maven Compile outcome

  ![](assets/FI_development_instruction/markdown-img-paste-20190124120941289.png)

- Run the Main class and see the outcome:

  ![](assets/FI_development_instruction/markdown-img-paste-20190310193834555.png)


## DEMO2.0
### Scenarios Description
- The police need to collect the real-time vehicle info data from all the monitoring camera where located on the street, save the cleaned structured data into FusionInsight HD and Libra for later query
- In some emergency situation, the police have the suspicion vehicles' plate numbers and need to set the alarm control so that to obtain suspicion vehicles' info in real-time

### Sample model

This example currently uses **Kafka** as the input end, simulates two Topic, and for each topic, 500 cameras' data are simultaneously written. The data is written to **HBase** after consumption by spark streaming, and the corresponding index data is written to **solr** or **ElasticSearch**. Each data record is required to compare with the pre-store redis data which are the suspicion vehicles' plate numbers. If the data collides, and the successful result of the collision matching is written into the kafka cache again, which can be consumed subsequently by other third-party tools. On the other hand, the JSON data structure extracts the important fields to form the thematic data and stores them in the LirbA. The upper layer can support the analog multi-channel concurrent access to the LirbA. ElasticSearch, HBase.

### Solution architecture:

![](assets/FI_development_instruction/markdown-img-paste-20190311125530434.png)


DEMO2.0 involves the following **data flow**

- ->Kafka->SparkStreaming->LibrA（Thematic data）

- ->Kafka->SparkStreaming->HBase->Solr/ES

- ->Kafka->SparkStreaming->Redis（Data collision）->kafka

Data Source (this Demo uses JSON structure data)

```
{  
   "SubscribeNotificationListObject":{  
      "SubscribeNotificationObject":[  
         {  
            "SubscribeID":"通知主题",
            "NotificationID":"650100010000042017040112010100001",
            "TriggerTime":"20171122205938",
            "InfoIDs":"201708220100000013200000010120170330120000000010100001",
            "MotorVehicleObjectList":{  
               "MotorVehicleObject":[  
                  {  
                     "VehicleRoof":"***",
                     "RearviewMirror":"",
                     "SideOfVehicle":"",
                     "StorageUrl2":"",
                     "DisappearTime":"",
                     "FilmColor":"",
                     "StorageUrl3":"",
                     "StorageUrl1":"",
                     "VehicleClass":"",
                     "VehicleShielding":"",
                     "PlateClass":"",
                     "SafetyBelt":"",
                     "RightBtmY":"",
                     "RightBtmX":"",
                     "IsDecked":"",
                     "WheelPrintedPattern":"",
                     "PlateNo":"陕A4861",
                     "VehicleRearItem":"",
                     "VehicleDoor":"",
                     "BrandReliability":"",
                     "VehicleStyles":"",
                     "VehicleFrontItem":"",
                     "LeftTopY":"",
                     "LeftTopX":"",
                     "NumOfPassenger":"",
                     "NameOfPassedRoad":"",
                     "VehicleModel":"",
                     "VehicleHood":"",
                     "StorageUrl4":"",
                     "StorageUrl5":"",
                     "PlateColor":"",
                     "PlateNoAttach":"",
                     "VehicleTrunk":"",
                     "PlateDescribe":"",
                     "PlateCharReliability":"",
                     "AppearTime":"",
                     "VehicleWindow":"",
                     "VehicleWidth":"",
                     "Calling":"",
                     "PassTime":"1552132640565",
                     "Direction":"",
                     "CarOfVehicle":"",
                     "MarkTime":"",
                     "HasPlate":"",
                     "HitMarkInfo":"",
                     "LaneNo":"",
                     "VehicleHeight":"",
                     "Speed":"",
                     "MotorVehicleID":"",
                     "DeviceID":"",
                     "SourceID":"YAAA104276717",
                     "IsCovered":"",
                     "IsModified":"",
                     "IsSuspicious":"",
                     "DescOfFrontItem":"",
                     "VehicleBrand":"",
                     "PlateReliability":"",
                     "DrivingStatusCode":"",
                     "VehicleColorDepth":"",
                     "VehicleLength":"",
                     "InfoKind":"",
                     "DescOfRearItem":"",
                     "TollgateID":"",
                     "UsingPropertiesCode":"",
                     "VehicleChassis":"",
                     "VehicleColor":"",
                     "IsAltered":"",
                     "VehicleWheel":"",
                     "VehicleBodyDesc":"",
                     "Sunvisor":""
                  }
               ]
            },
            "Title":"650100010000032017040112010100001"
         }
      ]
   }
}
```
The backend client supports the API concurrent query Solr/ES/hbase/mppdb

### Brief description of each process

1. Data flow: **-> Kafka-> SparkStreaming-> LibrA**

  Project Code：
  - com.huawei.bigdata.mppdb.SparkToMppdb (class for whole process)
  - com.huawei.bigdata.mppdb.ConnectionManager(class for Libra database connection)

  Brief Introduction: JSON data is produced from kafka. After streaming consuming by spark streaming, some fields are extracted and stored in LibrA. Subsequent use of MPPDB for topic analysis. The current DEMO topic data contains fields such as **plateNo**, **sourceID** and **passTime**.

  ![](assets/FI_development_instruction/markdown-img-paste-20190311144659981.png)

2. Data flow: **->Kafka->SparkStreaming->Redis (data collision) ->kafka **
    and **->Kafka->SparkStreaming->HBase->Solr/ES**

    Project Code:
    - com.huawei.bigdata.spark.SparkAlarmControlDemo (class for whole process)

    - com.stk.bigdata.sparkstreaming.DataHander (class for data processing)

  Brief Introduction:

  - The JSON data is produced from kafka. After stream consumption by sparkstreaming, the corresponding license plate number is obtained through JSON interpretation, and then is collided with the data which was pre-insert in **Redis**. If the matching is successful, the data is written to the kafka cache(new topic) again, which is convenient for third-party tools to use.

  - Data is aggregated to HBase for storage, and index data is stored in solr/es for easy full-text search and real-time query

  - Pre-given data exists in the Redis database to is used for subsequent data collisions. In order to improve the performance of data comparison, the data key value of the redis data inventory is saved as the vehicle plate number.

    ![](assets/FI_development_instruction/markdown-img-paste-20190311150228787.png)


### Installation and deployment

**Precondition**

  - One FI HD (spark , Solr/ES, HBase, kafka, redis)
  - One FI Libra

  Note: FI HD and FI Libra should be within the same network segment

**Procedure**

  - Check **Kafka** topic:

    Topic of this project are **scenetest1**, **scenetest2**, **scenetest-control**.  **scenetest1** and **scenetest2** are used for kafka business data generation and consumption. **scenetest-control** is used for data collision and stored in kafka

    Eg: check kafka consume data by using the following command:

    Note: config/consumer.properties should be configured before the start

    ```
    bin/kafka-console-consumer.sh --topic scenetest-control --bootstrap-server 172.21.3.101:21005,172.21.3.102:21005,172.21.3.103:21005 --consumer.config config/consumer.properties
    ```

  - Create **hbase** table: use the following command

    ```
    hbase shell
    create 'MotorVehicleObjectList ', 'cf' to create the hbase table
    ```


  - Create solr index name with all required fields

    - login into the solr admin site by FI manager

      ![](assets/FI_development_instruction/markdown-img-paste-20190311152500526.png)

    - add new collection named **MotorVehicleObjectList**

      ![](assets/FI_development_instruction/markdown-img-paste-20190311152655323.png)

    - add all required fields

      Eg: add one field **VehicleRoof**

      ![](assets/FI_development_instruction/markdown-img-paste-20190311153107138.png)

  - Create a **redis cluster**, see the product documentation for details(FusionInsight HD Product Documentation > Service Operation Guide > Redis > Redis Cluster Management). After the cluster is created, a certain amount of suspicion vehicles' plate numbers needs to be saved to redis for data collision by using the sample code of redis

  Insert suspicion vehicles' plate numbers by using redis sample code:

  ![](assets/FI_development_instruction/markdown-img-paste-20190311153727438.png)

  Outcome:

  ![](assets/FI_development_instruction/markdown-img-paste-20190311153901335.png)

  - Create an account for secure authentication. The **developuser** user is used by default in this DEMO, and has the permissions of kafka, HBase, solr, redis, and zookeeper. After the user is created, you need to download the authentication information (keytab, krb) from the FI manager for subsequent deployment.


  - Create a LibrA database and corresponding database tables, bind user passwords, and create HD cluster access whitelists. This DEMO uses the default **postgres** database, using the **jack** as user, password **Bigdata@123** Connect to the database

    - Login mppdb01 server（172.21.5.101）

    - login with **omm** user by using command `su - omm`

    - `source /opt/huawei/Bigdata/mppdb/.mppdbgs_profile`

    - `gs_guc set -Z coordinator -N all -I all -h "host all jack 172.21.3.43/32 sha256"` (add to whitelists)

    - `gs_guc set -N all -I all -Z coordinator -c "listen_addresses = '*'"` (Configure the listen address)

    - `gs_om -t stop && gs_om -t start` (restart mppdb)

  - Configuring and importing the sample project

    - Extract the obtained demo sample code into the unzipped directory.

    - Copy the previously prepared user.keytab and krb5.conf files to the "conf" directory of the sample project.

    - Import the sample project into the Eclipse development environment.

    - Open Eclipse and select "File > New > Java Project".

    - Check “Use default location” and click “Browse”.

    - The Browse for Folder dialog is displayed:

    - Select the demo sample code to extract the directory and click the "OK" button.

    - Click the "Finish" button in the "New Java Project" window.

    - Import the sample project dependency package:

    - Left click to select the demo sample project, click on the navigation bar "project" and select "Properties".

    - Click on “Java Build Pass”, select the Libraries menu, then select the jar with the red cross, click on “Remove” on the left, and click on “Apply”.

      ![](assets/FI_development_instruction/markdown-img-paste-20190311155800286.png)

    - Click on “Add External JARs...”, select the demo sample project /lib in the upper navigation path of the new dialog box, select all jar packages, click “Open” below, then click “Apply”, then close.

      ![](assets/FI_development_instruction/markdown-img-paste-20190311155901726.png)

    - Set Eclipse's text file encoding format into UTF-8 to solve garbled display problems

  - Configuration parameters (developuser, topics, brokers, etc. and database connection parameters, etc.)

    - The conf/props/parameter.properties file is used to store the IP ports of each service, the parameters required for the database connection, and the parameters required to connect the services can be got here.

    - The parameters such as batchTime, topics, brokers, batchSize, etc. can be reset based on the "Introduction to each parameter" table on former section

### Project Deployment and running

- Create a project run directory on the linux jump(172.21.3.43), for example `/opt/client/sence-bigdata-demo`

- Compile the sample project code and generate the corresponding jar package, the example is **demo.jar**

  - right click on the project and choose Export

    ![](assets/FI_development_instruction/markdown-img-paste-20190311160337590.png)

  - choose JAR file and click on next

    ![](assets/FI_development_instruction/markdown-img-paste-20190311160424531.png)

  - set JAR file directory

    ![](assets/FI_development_instruction/markdown-img-paste-20190311160537883.png)

  - click on next until the last page and click on Finish to generate the jar file

    ![](assets/FI_development_instruction/markdown-img-paste-20190311160659280.png)

  - check the jar file:

    ![](assets/FI_development_instruction/markdown-img-paste-20190311160810606.png)

- Copy the lib library, conf directory with configuration files, and demo.jar package into the Linux environment `/opt/client/sence-bigdata-demo` directory by WinSCP:

  ![](assets/FI_development_instruction/markdown-img-paste-20190311161049670.png)

- Start the kafka data generation process, as shown in the following command:

  ```
  java -cp /opt/sence-bigdata-demo/*:/opt/sence-bigdata-demo/lib/* com.huawei.bigdata.kafka.producer.ProducerMultThread
  ```

  ![](assets/FI_development_instruction/markdown-img-paste-20190311161343304.png)

- Start->Kafka->SparkStreaming->LibrA process, as shown in the following command:

  ```
  java -cp /opt/sence-bigdata-demo/*:/opt/sence-bigdata-demo/lib/* com.huawei.bigdata.mppdb.SparkToMppdb
  ```

  check outcome on Linux

  ![](assets/FI_development_instruction/markdown-img-paste-20190311161440348.png)

  check data on LibrA by data studio

  ![](assets/FI_development_instruction/markdown-img-paste-20190311161624635.png)

- Start->Kafka->SparkStreaming->Redis (data collision)->kafka and ->Kafka->SparkStreaming->HBase->Solr process, as shown in the following command:

  ```
  java -cp /opt/sence-bigdata-demo/*:/opt/sence-bigdata-demo/lib/* com.huawei.bigdata.spark.SparkAlarmControlDemo
  ```
  check the outcome on Linux

  ![](assets/FI_development_instruction/markdown-img-paste-20190311161846596.png)

  check the HBase data:

  ```
  hbase shell
  scan 'MotorVehicleObjectList'
  ```

  ![](assets/FI_development_instruction/markdown-img-paste-20190311162145469.png)

  check the ES data by ES-head tool:

  ```
  cd /usr/elasticsearch-head
  npm run start
  ```

  ![](assets/FI_development_instruction/markdown-img-paste-20190311162615459.png)

  ![](assets/FI_development_instruction/markdown-img-paste-2019031116273700.png)

  check the Solr data by login into Solr Admin site:

  ![](assets/FI_development_instruction/markdown-img-paste-20190311162952777.png)

- Check the data collision on kafka client backend

  ```
  cd /opt/hadoopclient/Kafka/kafka

  bin/kafka-console-consumer.sh --topic scenetest-control --bootstrap-server 172.21.3.101:21005,172.21.3.102:21005,172.21.3.103:21005 --consumer.config config/consumer.properties
  ```

  ![](assets/FI_development_instruction/markdown-img-paste-2019031116394003.png)


## GraphBase

**Brief introduction**

- In the Internet age, with the development of network technology, enterprises have accumulated more and more data. With the continuous increase of data sets, the performance of traditional relational database query will be worse, especially for some special business scenarios, so there is an urgent need for a new solution to deal with this crisis. In order to solve complex relationship problems, the graph database came into being.

- Graph database refers to storing and querying data in the data structure of “graph”. The graph contains nodes and relationships. Nodes and relationships can have labels and attributes, and edges can have directions. FusionInsight GraphBase is a distributed graph database based on FusionInsight HD. It is based on HBase's distributed storage mechanism. It can support massive data of tens of billions of nodes and billions of relationships. It provides Spark-based data import and Elasticsearch-based indexing mechanism. There is a wide range of applications in areas such as analysis and financial anti-fraud.

The system has the following characteristics:
- Fully distributed, the Hadoop ecosystem is seamlessly integrated.
- Tens of billions of side, second level query.
- Provides an easy-to-use Rest interface for easy data query analysis.
- Provides powerful Gremlin graph traversal capabilities for complex business logic.
- Support offline batch import and real-time stream import, and deeply optimize import performance.

**Application**

In the era of explosive data growth, traditional relational databases can't cope with fast query corresponding requests. Graph data modeling can effectively improve the query and analysis performance of data, and it is widely used in various applications.

Products Recommendation

- According to the customer's own attributes and behavioral characteristics, predict whether the customer is willing to handle related business and provide the personalized business recommendation for the customer.

Relationship analysis
- In the field of public security, the data is analyzed and processed through a relational analysis engine to provide relationship data analysis and query services between different individuals.

Social analysis
- In the social field, you can find friends with specific hobbies, or find friends you may know through your friends' friends.

**Interface Type Introduction**

REST API Interface

- REST APIs are developed using the Java language that is simple and easy. Therefore, you are advised to use the Java language to develop upper-layer applications.

- For details, see GraphBase in FusionInsight HD 6.5.RC2 API Documentation.

Gremlin API Interface

- Gremlin is the graph traversal language of Apache TinkerPop. Gremlin is a functional, data-flow language that enables users to succinctly express complex traversals on (or queries of) their application's property graph. Every Gremlin traversal is composed of a sequence of (potentially nested) steps. A step performs an atomic operation on the data stream. For details about Gremlin, visit http://tinkerpop.apache.org/docs/3.3.2/reference/#traversal.

- Gremlin has the following three types of basic operations:
  - map-step: converts objects in a data flow.
  - filter-step: filters objects in a data flow.
  - sideEffect-step: calculates data flows.

**Architecture of GraphBase**

![](assets/FI_development_instruction/markdown-img-paste-20190311203310358.png)

Access layer:

- Gremlin API: Introducing the open source Apache TinkerPop Gremlin component to provide an open source standard diagram interactive query language interface

- REST API: Provide a full set of interfaces including graph query, graph modification, graph management and Huawei enhanced online analysis graph algorithm;

- Load balancing of multi-instance GraphServer is provided by Load Balancer.

Computing layer:
- Provide graph database core engine, including data management, metadata management, etc.

- Backend storage and indexing interface adaptation layer;

Storage layer:
- Distributed KV storage: Provides massive graph data storage capabilities

- Search engines provide secondary indexing, full-text search, fuzzy search and other capabilities.

**Relationship with components**

GraphBase stores business data and metadata in HBase to support massive data; stores external index data in Elasticsearch for full-text search and fuzzy matching; uses spark to realize batch and real-time import of data; and MapReduce uses index reconstruction And batch deletion capabilities; using ZooKeeper to achieve distributed coordination of multiple instances of the computing engine.

![](assets/FI_development_instruction/markdown-img-paste-20190311203532596.png)

**Basic Concepts**
Like most graph databases, GraphBase uses property graphs for modeling. Based on the property graph models, GraphBase has the following basic concepts:

- Vertex: A vertex is also called a node, which is used to specify an entity object in the real world, such as a person.

- Vertex label: A vertex label indicates a node type that specifies the type of an entity object in the real world. Example: person. In GraphBase, a node has only one vertex label. The default value is the vertex.

- Edge: An edge, also known as a relationship that is used to specify the connections between two entity objects (vertices in a graph) in the real world. For example, the relationship between two persons is a friend. Edges in GraphBase are unidirectional, which starts from a vertex and ends at another. Therefore, a bidirectional edge has an incoming and an outgoing edge.

- Edge label: An edge label specifies the type of relationship in the real world. For example, the relationship is a friend.

- Property: A property describes some attribute of either a vertex or an edge in the format of key-value pair. Property key is used to describe the key in the key-value pair, property value describes a specific value. For example, a property key is a name, the property value is Zhang San.


**Typical Application Scenario**

In the scenario, there are eight real-world entities, four persons and four mobile phones. There are eight relationships between the entity objects, including friend, knows, call, and has.

![](assets/FI_development_instruction/markdown-img-paste-20190311203640981.png)

**Graph Modeling Scenario**

The purpose of graph modeling is to map the real world relationships to GraphBase using a graph:

- Vertex: maps persons and mobile phones in the real world to GraphBase using vertices.

- Vertex label: defines the types of persons and mobile phone in the real world in GraphBase. That is, people are labeled as a person, and mobile phones are defined as a phone in the graph.

- Edge: maps the relationships between people and people, people and mobile phones, and mobile phones and mobile phones in the real world to GraphBase using edges.

- Edge label: defines the relationships between entity objects in the real world in GraphBase. The definition of these relationships in this scenario are as follows:
  - The friend relationship between people is defined as the edge label friend.
  - The acquaintance relationship is defined as the edge label knows.
  - The ownership between mobile phones and people is defined as has.
  - The communication relationship between mobile phones is defined as the edge label call.

- Property: defines the feature information of each entity object in the real world in GraphBase. The name and age of a person are defined as the property name and age, respectively, and the mobile phone number is defined as the property telephone.

- New property: Considering that GraphBase supports the value properties obtained by the evaluation system, the relationship weight property definition is added to indicate the importance of relationships. That is, the weight property is defined. It is considered that the value property has been obtained.

![](assets/FI_development_instruction/markdown-img-paste-20190311203758931.png)

**Development Guidelines**

Based on the preceding scenario description, the basic operation process of creating a GraphBase service and querying data using the created service are as follows:

- Take node 1 > node 2 in the scenario as an example, there three elements included: node 1, node 2, and edge 10.
  1. Create a schema using the two nodes, see instructions provided in Creating a Schema.

    You need to create two vertex labels, person and phone, and an edge label, has.

    You need to create the properties of all nodes and edges: name, age, telephone, and weight.

  2. (Optional) Create indexes for properties to improve query efficiency. For details, see Creating an Index.

  3. Create vertices and edges. Create vertex 1, vertex 2, and the edge 10. For details about how to create vertices and edges, see Creating and Querying a Vertex or an Edge.

  4. Query data.

    Method 1: Invoke a REST API for the query.

    Invoke the full graph query interface to query vertices and edges. For details, see Performing a Full Graph Query.

    Method 2: Perform data query using Gremlin statements. For details, see the Gremlin API Development Guide.

    You can create other nodes and edges in the same way.

  - If the data volume is large, you can import the data in batches. The procedure is as follows:

    1. Create a schema.

      Compile an XML file. For details, see Compiling a Schema File (XML).

      Create a schema by uploading the XML file. For details, see Creating a Schema by Uploading an XML File.

    2. Import data.

      Compile a data file (CSV), data description file (DESC), and a graph mapping rule file (.mapper). For details, see Preparing Data Files.

        Import data in real time or in batches. For details, see Importing Data.

    3. Query data.

      Method 1: Invoke a REST API for query.

      Invoke the full graph query interface to query vertices and edges. For details, see Performing a Full Graph Query.

      Method 2: Perform data query using Gremlin statements. For details, see the Gremlin API Development Guide.

  - During data query, except vertex and edge query, you can query paths between vertices and perform line expansion queries. For details, see Performing a Path Query and Performing a Line Expansion Query.


  **Development Preparation**
  1. Install GraphBase

    - Get the GraphBase package. Obtain the following file from the specified address: FusionInsight_GraphBase_V100R002C80SPC300_RHEL.tar.gz

    - Upload the package.

      - Use the WinSCP tool to log in to the active management node of the FusionInsight HD cluster as the root user.

      - Select FusionInsight_GraphBase_V100R002C80SPC300_RHEL.tar.gz and drag it to the /opt/ directory of the management node and wait for the upload.

    - Use the Putty tool to log in to the active management node as the root user. Run the following command to switch to the omm user.  “su - omm”

    - Run the register_pack.sh script to inject the GraphBase into the existing FusionInsight cluster. Command format: ./register_pack.sh with GraphBase installation package full path

    - Login FI HD manager

    - Click Service Management > Add Service > Add Service Only > Next > GraphBase

  2. Download and install the GraphBase client

      - Log in to the FusionInsight Manager, choose Service Management > GraphBase, and click Download Client. Select the client type as the full client and save it in the /tmp/FusionInsight-Client/ directory on the server.

      - Enter the corresponding directory of the master node: cd /tmp/FusionInsight-Client/

      - Unzip the package. Extract the obtained software package and generate the "*.tar.gz" and "*.tar.gz.sha256" files by the following command tar -xvf FusionInsight_GraphBase_Client.tar

      - Verify the package. Run the sha256sum command to check. For example, the sha256 file is "FusionInsight_GraphBase_ClientConfig.tar.sha256".

      - Unzip the package by using command tar -xvf FusionInsight_GraphBase_ClientConfig.tar and install the client by using command ./install.sh /opt/graphbaseClient

  3. Prepare development user

    - Log in to FusionInsight Manager, choose System Settings > User Management, and click Add User to create a new user. Here is graphtest. The user group is selected according to the actual requirements. The primary group selects graphbaseadmin and gives all permissions.

    - Use the newly created graphtest user to log in to FusionInsight Manager again. You need to change the password for the first login.

    - Download the graphtest user authentication file on the System Settings > User Management interface


**Development Procedure**

REST API example (data volume is small)

- download and unzip graphbase sample code

  ![](assets/FI_development_instruction/markdown-img-paste-20190311210022514.png)

- import the project **graphbase-core-example**

  ![](assets/FI_development_instruction/markdown-img-paste-2019031121043673.png)

  ![](assets/FI_development_instruction/markdown-img-paste-20190311210525852.png)

  click Next until the finish

  ![](assets/FI_development_instruction/markdown-img-paste-20190311210640777.png)

- configure the graphbase.properties file, shown as follows

  ![](assets/FI_development_instruction/markdown-img-paste-20190311210951487.png)

- run GraphBaseRestExample.java and see the outcome

  ![](assets/FI_development_instruction/markdown-img-paste-20190311211214436.png)


Gremlin API example (data volume is large and need to import data in batches)

- import the project **gremlin-demo4j** by pom.xml

  ![](assets/FI_development_instruction/markdown-img-paste-20190311211536660.png)

- make adjustments on pom.xml file, shown as follows

  ![](assets/FI_development_instruction/markdown-img-paste-20190311212520380.png)

- maven clean

  ![](assets/FI_development_instruction/markdown-img-paste-20190311211930667.png)

- maven compile to download the related jar files

  ![](assets/FI_development_instruction/markdown-img-paste-20190311212312683.png)

- Open former **graphbase-core-example** and use it to create graph and index

only keep the following code in **GraphBaseRestExample.java**

```
/***************************** Create Graph and Build Its Schema ******************************/
RestApi api = new RestApi(client);
String graphName = "graphbase3";
/* Create graph */
api.createGraph(graphName);
// Create schema in uploading schema.xml way
/*File file = FileUtils.getFile(System.getProperty("user.dir") + File.separator + SCHEMA_FILE);
 *api.addSchema(file, graphName);
 */
/* Create Schema for Graph
 * Includes: adding vertex labels, edge labels, and property keys, and then building indexes based on properties
 */
/* add vertex labels */
api.addVertexLabel("person", graphName);
api.addVertexLabel("phone", graphName);
/* query vertex labels */
api.queryVertexLabel("person", graphName);
api.queryAllVertexLabel(graphName);
/* add edge labels */
EdgeLabel edgeLabel = new EdgeLabel();
edgeLabel.setName("friend");
api.addEdgeLabel(edgeLabel, graphName);
edgeLabel = new EdgeLabel();
edgeLabel.setName("knows");
api.addEdgeLabel(edgeLabel, graphName);
edgeLabel = new EdgeLabel();
edgeLabel.setName("call");
api.addEdgeLabel(edgeLabel, graphName);
edgeLabel = new EdgeLabel();
edgeLabel.setName("has");
api.addEdgeLabel(edgeLabel, graphName);
/* query edge labels */
api.queryEdgeLabel("friend", graphName);
api.queryAllEdgeLabel(graphName);
/* add property keys */
PropertyKey propertyKey = new PropertyKey();
propertyKey.setDataType(DataType.String);
propertyKey.setName("name");
api.addPropertyKey(propertyKey, graphName);
propertyKey = new PropertyKey();
propertyKey.setDataType(DataType.Integer);
propertyKey.setName("age");
api.addPropertyKey(propertyKey, graphName);
propertyKey = new PropertyKey();
propertyKey.setDataType(DataType.String);
propertyKey.setName("telephone");
api.addPropertyKey(propertyKey, graphName);
propertyKey = new PropertyKey();
propertyKey.setDataType(DataType.Float);
propertyKey.setName("weight");
api.addPropertyKey(propertyKey, graphName);
/* query property key */
api.queryPropertyKey("name", graphName);
/* query the whole property key */
api.queryAllPropertyKey(graphName);
/* add index. Wherever practicable, creating schema shall be completed prior to this. */
addIndex(api, graphName);
// rebuild index
//api.reCreateGraphIndex("name_index", graphName);
//api.reCreateGraphIndex("age_index", graphName);
//api.reCreateGraphIndex("telephone_index", graphName);
//api.reCreateGraphIndex("weight_index", graphName);
/* query the whole indexes */
api.queryAllIndex(graphName);

```

- transfer graph data into hdfs with the following directory `/user/graphtest` by using **Hue**

  ![](assets/FI_development_instruction/markdown-img-paste-20190311220458324.png)


- use the following commands to upload graph data
  note: Spark1.5 version is needed for data uploading
```
source /opt/hadoopclient/Spark/component_env
kinit graphtest
cd /home/graphbase_client/graphbaseClient/GraphBase/graphwriter
bin/graphWriter.sh graphbase3 /user/graphtest/Person.csv /user/graphtest/Person.csv.mapper /user/graphtest/Person.desc
```

  check the  outcome on Linux client backend

  ![](assets/FI_development_instruction/markdown-img-paste-20190311220831888.png)

  check the outcome on yarn

  ![](assets/FI_development_instruction/markdown-img-paste-20190311220920255.png)

- save the related user.keytab and krb5.conf files into project's conf directory, shown as follows

  ![](assets/FI_development_instruction/markdown-img-paste-20190311221453396.png)

- add the hosts ip into **remote-objects.yaml**, shown as follows

  ![](assets/FI_development_instruction/markdown-img-paste-20190311221717581.png)

- change the user_principal into **graphtest**, graphName into **graphbase3** and run **GremlinClusterClientDemo.java**

  check the outcome

  ![](assets/FI_development_instruction/markdown-img-paste-20190311221741306.png)
