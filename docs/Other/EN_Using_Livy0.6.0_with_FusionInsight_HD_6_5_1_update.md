# Connection Instruction between Apache Livy and FusionInsight

## Succeeded Case

> Apache Livy 0.6.0-incubating <--> FusionInsight HD 6.5.1 (Spark2.x)
>
> Apache Livy 0.5.0-incubating <--> FusionInsight HD V100R002C80SPC200 (Spark2.x)

## Deploy the externally verified livy service and submit tasks using session and batch methods

### Scenario Description

In some complex scenarios, access control is required for users who submit tasks. The livy service supports Kerberos SPNEGO authentication for external access. The following is a specific test scenario

- Connection with FI HD cluster: 172.16.6.10-12, three-node deployment

- Apache Livy server side： 172.16.2.118, Install the FI HD client on this node and complete the download and installation of Livy by referring to the previous chapter.

- Client: 172.16.2.119, Submit a task request using the curl command on this node. You need to install an FI HD client and check whether curl supports SPNEGO authentication by using the command 'curl -V'

  ![](assets/Using_Livy_with_FusionInsight/markdown-img-paste-20191205170853463.png)

Two authenticated users are used in this scenario, user developuser, and user livy

- User livy is the user who needs to actually submit a spark task request to the FI HD cluster for the livy service

- User developuser is the user used by the client to submit tasks to the Livy server

- The entire business process is actually the proxy user developuser submits the spark task to the FI HD cluster in the name of the user livy, but before the task is performed, the user developuser needs to pass the Kerberos authentication of the FI HD cluster. In this way, the Apache Livy server access control is implemented

### Kerberos authentication-related configuration

- Log in to the FI HD manager to create the users developuser, livy to be used in the test. And download the user livy authentication information (user.keytab, krb5.conf)

  ![](assets/Using_Livy_with_FusionInsight/markdown-img-paste-20191206111259112.png)


- Log in to kadmin with the FI HD client, and create a new principal for FI HD's Kerberos authentication to the Livy HTTP service. The principal is "HTTP/host-172-16-2-118". host-172-16-2-118 is the hostname of the node where Apache Livy is deployed.

  ![](assets/Using_Livy_with_FusionInsight/markdown-img-paste-20190806114250120.png)

  When you execute the command "kadmin –p kadmin/admin", the initial password is "Admin@123", and the new password must be kept in mind after modification.

  Pass the generated http2.keytab (keytab file name can be customized) authentication file to the /opt path of the livy server and use the "kinit -kt" command to check whether the authentication is successful

  `kinit -kt /opt/http2.keytab HTTP/host-172-16-2-118@HADOOP.COM`

  ![](assets/Using_Livy_with_FusionInsight/markdown-img-paste-20190806114756630.png)

  Use the command "kdestroy" to clear the cached notes when done

- To log in to the cluster, click Service Management-> Yarn-> Service Configuration-> Select All Configuration-> Custom. Add the following configuration under the corresponding parameter file as core-site.xml:

  ![](assets/Using_Livy_with_FusionInsight/markdown-img-paste-2019120609545724.png)

  ```
  hadoop.proxyuser.livy.hosts = *
  hadoop.proxyuser.livy.groups = *
  ```

- Follow the same method above to add the same configuration to the core-site.xml file on both hdfs and hive service:

  ![](assets/Using_Livy_with_FusionInsight/markdown-img-paste-20191206100058854.png)

  ![](assets/Using_Livy_with_FusionInsight/markdown-img-paste-20191206100217816.png)

### Client related checks

- Use "curl -V" command to check if client curl command supports Kerberos Spnego

  ![](assets/Using_Livy_with_FusionInsight/markdown-img-paste-20190805103921157.png)

- Install the corresponding client for FI HD cluster

- Check that client time and cluster time are less than 5 minutes

### Livy server configuration

- Install the corresponding client for FI HD cluster

- Check that client time and cluster time are less than 5 minutes

- Check the livy.conf file configuration

  ![](assets/Using_Livy_with_FusionInsight/markdown-img-paste-20190806115225643.png)

  Note：

  1. "livy.file.local-dir-whitelist=/opt/". This configuration parameter is to use the livy batch method to submit the task locally, you need to open the whitelist of the local path.

  2. launch.kerberos.* related configuration parameters are  required for Livy to actually interact with the FI HD cluster

  3. auth.kerberos.* related configuration parameters are used for external access control to livy web UI



- Check the livy-client.conf file configuration

  ![](assets/Using_Livy_with_FusionInsight/markdown-img-paste-2019080511062850.png)

- Check the livy-env.sh file configuration

  ![](assets/Using_Livy_with_FusionInsight/markdown-img-paste-20190805110815683.png)

- Check spark-blacklist.conf file configuration

  ![](assets/Using_Livy_with_FusionInsight/markdown-img-paste-20190805110944567.png)

- Add the following configuration to the log4j.properties file to adjust the log level (optional)

  ```
  log4j.logger.org.eclipse.jetty=DEBUG
  ```

### Submit tasks using Livy session

The livy session mode corresponds to the spark console interactive mode. The spark task is submitted and ran by using the detailed code


- Log into the livy server and use `bin / livy-server start` to start the livy service

  ![](assets/Using_Livy_with_FusionInsight/markdown-img-paste-20190805112111902.png)

  Open Livy-end livy-root-server.out log to see if livy starts successfully

  ![](assets/Using_Livy_with_FusionInsight/markdown-img-paste-20190805112159413.png)

- Log in to the client (172.16.2.119) and use `kinit developuser` to enter a password to obtain a ticket

  ![](assets/Using_Livy_with_FusionInsight/markdown-img-paste-20190805112312259.png)


- Use the following command to start a pyspark session in livy

  `curl --negotiate -k -v -u developuser : -X POST --data '{"kind": "pyspark"}' -H "Content-Type: application/json" http://host-172-16-2-118:8998/sessions`

  ![](assets/Using_Livy_with_FusionInsight/markdown-img-paste-20190806141351861.png)


- Submit a piece of code for session/0 using the following command

  `curl --negotiate -k -v -u developuser : -X POST -H 'Content-Type: application/json' -d '{"code":"1 + 1"}' http://host-172-16-2-118:8998/sessions/0/statements`

  ![](assets/Using_Livy_with_FusionInsight/markdown-img-paste-20190806141624866.png)

- Use the following command to view the results:

  `curl --negotiate -k -v -u : http://host-172-16-2-118:8998/sessions/0/statements | python -m json.tool`

  ![](assets/Using_Livy_with_FusionInsight/markdown-img-paste-20190806141719681.png)

- Use the following command to close the session

  `curl --negotiate -k -v -u : http://host-172-16-2-118:8998/sessions/0 -X DELETE`

  ![](assets/Using_Livy_with_FusionInsight/markdown-img-paste-20190806141829801.png)

- Log in to the connected cluster's yarn view the results:

  ![](assets/Using_Livy_with_FusionInsight/markdown-img-paste-20190806142026694.png)

- Besides, after the client (172.16.2.119) completes the curl command submission task, you can use klist to view the ticket information:

  ![](assets/Using_Livy_with_FusionInsight/markdown-img-paste-20190806181720594.png)

  You can see that the ticket authentication ticket will be updated HTTP/host-172-16-2-118@HADOOP.COM

### Submit task example using Livy batch(1)

The livy batch mode corresponds to the spark-submit interactive mode. The spark task is submitted and ran by using a compiled jar package or a completed py file.

This example calculates the pi value by submitting a jar package locally using yarn client mode

- Log in to the client (172.16.2.119) and use `kinit developuser` to enter a password to obtain a ticket.

- Find the test jar package spark-examples_2.11-2.1.0.jar in the FI HD client and transfer it to the /opt/ path of the livy server

  ![](assets/Using_Livy_with_FusionInsight/markdown-img-paste-20190805115748160.png)

- Submit the spark task on the client (172.16.2.119) with the following command

  `curl --negotiate -k -v -u developuser : -X POST --data '{"file": "file:/opt/spark-examples_2.11-2.1.0.jar", "className": "org.apache.spark.examples.SparkPi", "args": ["100"]}' -H "Content-Type: application/json" http://host-172-16-2-118:8998/batches`

  ![](assets/Using_Livy_with_FusionInsight/markdown-img-paste-2019080614231861.png)

- Open the livy-root-server.out log to see the results:

  ![](assets/Using_Livy_with_FusionInsight/markdown-img-paste-20190806142412857.png)

- Log in to the yarn to view the results:

  ![](assets/Using_Livy_with_FusionInsight/markdown-img-paste-20190806142447984.png)

### Submit task example using Livy batch(2)

This example calculates the pi value by submitting a py file locally using yarn client mode

- Log in to the client (172.16.2.119) and use `kinit developuser` to enter a password to obtain a ticket.

- Create a py2.py file and upload it to the /opt/ path of the Livy server. The specific content is as follows：

```
import sys
from random import random
from operator import add

from pyspark.sql import SparkSession


if __name__ == "__main__":
    """
        Usage: pi [partitions]
    """
    spark = SparkSession\
        .builder\
        .appName("PythonPi")\
        .getOrCreate()

    partitions = int(sys.argv[1]) if len(sys.argv) > 1 else 2
    n = 100000 * partitions

    def f(_):
        x = random() * 2 - 1
        y = random() * 2 - 1
        return 1 if x ** 2 + y ** 2 <= 1 else 0

    count = spark.sparkContext.parallelize(range(1, n + 1), partitions).map(f).reduce(add)
    print("Pi is roughly %f" % (4.0 * count / n))

    spark.stop()
```

  ![](assets/Using_Livy_with_FusionInsight/markdown-img-paste-20190805122730950.png)

- Submit the spark task on the client (172.16.2.119) with the following command

  `curl --negotiate -k -v -u developuser : -X POST --data '{"file": "file:/opt/pi2.py" }' -H "Content-Type: application/json" http://host-172-16-2-118:8998/batches`

  ![](assets/Using_Livy_with_FusionInsight/markdown-img-paste-20190806142550152.png)

- Open the livy-root-server.out log to see the results:

  ![](assets/Using_Livy_with_FusionInsight/markdown-img-paste-2019080614262941.png)

- Log in to the yarn to view the results:

  ![](assets/Using_Livy_with_FusionInsight/markdown-img-paste-2019080614265792.png)

### Submit task example using Livy batch(3)

This example uses the yarn cluster mode to submit a jar package under the cluster hdfs path and run it to calculate the pi value.

- Log in to the client (172.16.2.119) and use `kinit developuser` to enter a password to obtain a ticket.

- Modify the livy.conf file configuration to:

  ![](assets/Using_Livy_with_FusionInsight/markdown-img-paste-20190806142834644.png)

- Upload the jar package in the /tmp path of the HDFS connected to the FI HD cluster

  ![](assets/Using_Livy_with_FusionInsight/markdown-img-paste-20190805141930586.png)

- Restart Livy

- Submit the spark task on the client (172.16.2.119) with the following command

  `curl --negotiate -k -v -u developuser : -X POST --data '{"file": "/tmp/spark-examples_2.11-2.1.0.jar", "className": "org.apache.spark.examples.SparkPi", "args": ["100"]}' -H "Content-Type: application/json" http://host-172-16-2-118:8998/batches`

  ![](assets/Using_Livy_with_FusionInsight/markdown-img-paste-20190806143100296.png)

- Open the livy-root-server.out log to see the results:

  ![](assets/Using_Livy_with_FusionInsight/markdown-img-paste-20190806143129614.png)

- Log in to the yarn to view the results:

  ![](assets/Using_Livy_with_FusionInsight/markdown-img-paste-20190806143210274.png)

  ![](assets/Using_Livy_with_FusionInsight/markdown-img-paste-20190806143247373.png)

### Submit task example using Livy batch(4)

This example uses the yarn cluster mode to submit a jar package under the local path of the cluster and run it to calculate the pi value.

- Log in to the client (172.16.2.119) and use `kinit developuser` to enter a password to obtain a ticket.

- Because you use the yarn cluster to submit the jar package locally, you do not know in advance which cluster node the worker is in, so put the jar package spark-examples_2.11-2.1.0.jar in the /home path of each cluster node:

  ![](assets/Using_Livy_with_FusionInsight/markdown-img-paste-20190805143623305.png)

  ![](assets/Using_Livy_with_FusionInsight/markdown-img-paste-2019080514364033.png)

  ![](assets/Using_Livy_with_FusionInsight/markdown-img-paste-20190805143653616.png)

- Submit the spark task on the client (172.16.2.119) with the following command

  `curl --negotiate -k -v -u developuser : -X POST --data '{"file": "local:/home/spark-examples_2.11-2.1.0.jar", "className": "org.apache.spark.examples.SparkPi", "args": ["100"]}' -H "Content-Type: application/json" http://host-172-16-2-118:8998/batches`

  ![](assets/Using_Livy_with_FusionInsight/markdown-img-paste-20190806143404351.png)

- Open the livy-root-server.out log to see the results:

  ![](assets/Using_Livy_with_FusionInsight/markdown-img-paste-20190806143436711.png)

- Log in to the yarn to view the results:

  ![](assets/Using_Livy_with_FusionInsight/markdown-img-paste-20190806143513482.png)

  ![](assets/Using_Livy_with_FusionInsight/markdown-img-paste-20190806143541228.png)

### Configure Kerberos Spnego on Windows jump machine to log in livy web UI

The authentication mechanism of the Windows jump machine (172.16.2.111) accessing Livy web UI is the same as that of the client (172.16.2.119) using curl command to access Livy server and submit spark task.

- Product documentation -> Application Development Guide -> Security Mode -> Spark2x Development Guide -> Preparing the Environment -> Preparing for the HiveODBC Development Environment -> Windows Environment -> Finish the first 4 steps

  ![](assets/Using_Livy_with_FusionInsight/markdown-img-paste-20191206093348886.png)

- Configure JCE

  Download the Java Cryptography Extension (JCE) from the java official website, then decompress it to %JAVA_HOME%/jre/lib/security and replace the corresponding file.

  ![](assets/Using_Livy_with_FusionInsight/markdown-img-paste-20190806145516277.png)

- Check if the hostname of the livy server is added to the hosts file:

  ![](assets/Using_Livy_with_FusionInsight/markdown-img-paste-20190806160858384.png)

- Configure Firefox

  Firefox under windows needs to adjust the following parameters by visiting the `about:config` page:

  1. `network.negotiate-auth.trusted-uris` is set to trusted addresses

  2. `network.auth.use-sspi` is set to disable sspi authentication protocol

  ![](assets/Using_Livy_with_FusionInsight/markdown-img-paste-20190806145819801.png)

  ![](assets/Using_Livy_with_FusionInsight/markdown-img-paste-20190806145845785.png)

- Authentication with MIT Kerberos

  ![](assets/Using_Livy_with_FusionInsight/markdown-img-paste-20190806150006300.png)

- Login to Livy's web ui address http://host-172-16-2-118:8998/ui

  ![](assets/Using_Livy_with_FusionInsight/markdown-img-paste-20190806150130275.png)

- Submit the task using the previous sample and check it on Livy web ui

  ![](assets/Using_Livy_with_FusionInsight/markdown-img-paste-2019080615064401.png)

  ![](assets/Using_Livy_with_FusionInsight/markdown-img-paste-20190806150717610.png)

- Check the service ticket generated by MIT Kerberos

  ![](assets/Using_Livy_with_FusionInsight/markdown-img-paste-2019080615091446.png)
