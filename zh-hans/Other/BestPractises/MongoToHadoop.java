import com.esotericsoftware.minlog.Log;
import com.mongodb.spark.MongoSpark;
import org.apache.hadoop.conf.Configuration;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Clock;
import java.util.Properties;


public class MongoToHadoop {

    private Configuration hadoopConf;
    private String mongoUsername;
    private String mongoHost;
    private String mongoPort;
    private String mongoDB;
    private String mongoCollections;
    private String jceksPath;
    private String mongoPassword;
    private String mongoURI;
    private String MongoColumns;
    private String hiveDB;
    private String hiveTable;
    private String propertyFilePath;
    private String writeMode;
    private String conditions;
    private Properties properties;
    private String sqlWrite;
    private String partColumn;
    private String partValue;
    private boolean result;

    private static final String HADOOP_SECUIRTY_CREDENTIAL_PROVIDER_PATH = "hadoop.security.credential.provider.path";
    private static final String SPARK_MONGODB_INPUT_URL = "spark.mongodb.input.uri";
    private static final String SPARK_MONGODB_OUTPUT_URL = "spark.mongodb.output.uri";

    public static final Logger LOG = Logger.getLogger(MongoToHadoop.class);

    private void setPropertyFilePath(String propertyFilePath, String writeMode) {
        this.propertyFilePath = propertyFilePath;
        this.writeMode = writeMode;
    }

    private void dataProcess() throws IOException {
        initHadoopAuth();
        initMongoParameters();
        getMongoPassFromCredentialsProvider();
        generateMongoURI();
        dataMigrate();
    }


    /**
     * 用于加载hadoop配置信息，并完成kerberos认证
     */
    private void initHadoopAuth() throws IOException {

        properties = new Properties();
        File file = new File(propertyFilePath);
        if (!file.exists()) {
            LOG.error("Configuration File not found");
            throw new IOException("Configuration File not found.");
        }
        FileInputStream fi = new FileInputStream(propertyFilePath);
        properties.load(fi);
        String userPrincipal = properties.getProperty("userPrincipal");
        String userKeytabPath = properties.getProperty("userKeytabPath");
        String krb5ConfPath = properties.getProperty("krb5ConfPath");
        hadoopConf = new Configuration();

        //调用LoginUtil类中的login方法，完成kerberos认证
        LoginUtil.login(userPrincipal, userKeytabPath, krb5ConfPath, hadoopConf);

        fi.close();

    }


    /**
     * 加载mongoDB的配置信息
     */
    private void initMongoParameters() {
        mongoUsername = properties.getProperty("mongoUsername");
        mongoHost = properties.getProperty("mongoHost");
        mongoPort = properties.getProperty("mongoPort");
        mongoDB = properties.getProperty("mongoDB");
        mongoCollections = properties.getProperty("mongoCollections");
        jceksPath = properties.getProperty("jceksPath");
        MongoColumns = properties.getProperty("MongoColumns");
        hiveDB = properties.getProperty("hiveDB");
        hiveTable = properties.getProperty("hiveTable");
        conditions = properties.getProperty("conditions");
        partColumn = properties.getProperty("partColumn");
        partValue = properties.getProperty("partValue");

    }


    /**
     * 在hadoop环境中，通过 hadoop credential create mongoUsername -provider jceks://hdfs/mongo/mongosecret.jceks命令创建jceks文件
     * 命令执行前先需要执行以下命令完成环境变量设置 export HADOOP_CREDSTORE_PASSWORD=huawei
     * 包括后续在执行jar包时，也需要先执行上一步的环境变量设置命令
     */
    private void getMongoPassFromCredentialsProvider() throws IOException {
        hadoopConf.set(HADOOP_SECUIRTY_CREDENTIAL_PROVIDER_PATH, jceksPath);
        char[] pwd = hadoopConf.getPassword(mongoUsername);
        mongoPassword = new String(pwd);
    }

    private void generateMongoURI() {
        mongoURI = String.join("", "mongodb://", mongoUsername, ":", mongoPassword, "@", mongoHost, ":", mongoPort,
                "/", mongoDB, ".", mongoCollections);
    }

    private void dataMigrate() {
        SparkConf sc = new SparkConf()
                .set(SPARK_MONGODB_INPUT_URL, mongoURI)
                .set(SPARK_MONGODB_OUTPUT_URL, mongoURI);
        //Create a configuration class SparkConf, and then create a SparkContext.
        SparkSession spark = SparkSession
                .builder()
                .appName("MigrateFromMongoToHive")
                .config(sc)
                .enableHiveSupport()
                .getOrCreate();
        JavaSparkContext jsc = new JavaSparkContext((spark.sparkContext()));
        Dataset<Row> dataSet = MongoSpark.load(jsc).toDF();

        dataSet.createOrReplaceTempView("tempTable");

        String sqlRead;
        if (conditions.isEmpty()) {
            sqlRead = String.join(" ", "select", MongoColumns, "FROM", "tempTable");
        } else {
            sqlRead = String.join(" ", "select", MongoColumns, "FROM", "tempTable", "where", conditions);
        }

        String subPartString;
        if (partColumn.isEmpty()) {
            subPartString = "";
        } else {
            subPartString = String.join("", "partition(", partColumn, "=", partValue, ")");
        }

        switch (writeMode.toLowerCase()) {
            case "overwrite":
                sqlWrite = String.join(" ", "INSERT OVERWRITE TABLE", hiveDB, ".", hiveTable, subPartString, sqlRead);
                Log.info(sqlWrite);
                break;
            case "append":
                sqlWrite = String.join(" ", "INSERT INTO TABLE", hiveDB, ".", hiveTable, subPartString, sqlRead);
                break;
            default:
                Log.error("Please input a right insert mode, overWrite or append");
        }


        spark.sql(sqlRead + " limit 10").show();
        spark.sql(sqlWrite).show();

        result = true;

        jsc.close();
        spark.stop();

    }


    public static void main(String[] args) throws Exception {
        LOG.info("Reference Command is : " +
                "./bin/spark-submit --class com.huawei.mongo.MongoToHadoop " +
                "--master yarn " +
                "--deploy-mode client " +
                "--jars /root/mongo-spark-connector_2.11-2.3.2.jar,/root/mongo-java-driver-3.8.2.jar   " +
                "--driver-java-options \"-Dlog4j.configuration=file:/root/log4j.properties\" " +
                "/root/MongoToHadoop.jar /root/conf.properties\n");

        Clock clock = Clock.systemUTC();
        long startTime = clock.millis();

        MongoToHadoop readMongo = new MongoToHadoop();
        if (args[0] == null) {
            LOG.info("Please set the config file as parameter.");
        }
        LOG.info("The property file is:" + args[0]);

        if (args[1] == null) {
            Log.error("Please set the mode : append or overWrite");
        }

        readMongo.setPropertyFilePath(args[0], args[1]);
        readMongo.dataProcess();
        long endTime = clock.millis();
        long timeUsage = endTime - startTime;
        if (readMongo.result) {
            LOG.info("Migration succeeded!");
            Log.info("Time usage: " + timeUsage);
        }
    }

}

