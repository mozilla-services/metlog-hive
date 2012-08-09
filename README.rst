===========
metlog-hive
===========

metlog-hive is a set of extensions for Apache Hive to provide richer query support
for JSON structures that Metlog produces. 

Currently we provide a User Defined Table Function (UDTF)
`exjson_tuple` that supports indexing into a JSON structure at an
arbitrary depth with support for both dictionaries and list
structures.  This is meant to be a drop in replacement for calls to
the standard Hive `json_tuple` UDTF.

More information about how Mozilla Services is using Metlog (including what is
being used for a router and what endpoints are in use / planning to be used)
can be found on the relevant `spec page
<https://wiki.mozilla.org/Services/Sagrada/Metlog>`_.


Syntax
------

ExJSONTuple expects (json_blob, json_path1, json_path2, ... , json_pathN)

Each `json_path` is a dotted path notation string and can also use square bracket syntax to address items arrays.

Some examples should clear this up.

Here's a sample JSON blob that has been formatted so that it's easier to see what's going on ::

       {"severity":6,
        "timestamp":"2012-07-18T21:16:45.572517",
        "metlog_hostname":"aitc1.web.mtv1.dev.svc.mozilla.com",
        "fields":{"headers":{"path":"/1.0/11225/devices/",
                "host":"dev-aitc10.services.mozilla.com:443",
                "some_list": [5,10,15],
                "User-Agent":""},
        "threadlocal":{}},
        "metlog_pid":26482,
        "logger":"",
        "type":"wsgi",
        "payload":"",
        "env_version":"0.8"}


We can address the top level keys in this blob using paths like "severity", "timestamp" and "metlog_hostname". 
To address values in the `fields` section, you just have to use dot notation.

        ========================= =============================================
        Path                      Value
        ------------------------- ---------------------------------------------
        severity                       6  
        timestamp                      "2012-07-18T21:16:45.572517"
        metlog_hostname                "aitc1.web.mtv1.dev.svc.mozilla.com"
        fields.headers.path            "/1.0/11225/devices/"
        fields.host                    "dev-aitc10.services.mozilla.com:443"
        fields.some_list[2]            15
        ========================= =============================================


Getting started with JSON in Hive
---------------------------------

For full documentation on using Hive, please refer to the 
documentation at:

    http://hive.apache.org/

Using the exjson_tuple is fairly straight forwrad.  The basic steps
are :
 

 1. copy the MetlogHive.jar file into the HDFS cluster if it doesn't
    already exist. We suggest putting the JAR file into /metlog/lib
 2. create a table in hive where you want to process your JSON data if
    one doesn't already exist
 3. Get your JSON data onto the HDFS cluster
 4. load the data from HDFS into the your Hive table
 5. enable the exjson_tuple function in Hive with a "create temporary
    function" call
 6. run your query


1. Create the DFS directory and put the MetlogHive.jar file into HDFS ::

    $ hadoop dfs -mkdir /metlog/lib
    $ hadoop dfs -put MetlogHive.jar /metlog/lib

2. Create the Hive table if it doesn't already exist:

    $ hive 
    Hive history file=/tmp/vagrant/hive_job_log_vagrant_201208091816_1680191485.txt
    hive> create table metlog_json(json STRING);
    OK
    Time taken: 15.484 seconds

3.  Store JSON onto HDFS ::
    
    $ hadoop dfs -put metrics_hdfs.log /var/log/aitc

4.  Load the data into Hive and do a quick test of the data ::
        
        $ hive
        hive> LOAD DATA INPATH '/var/log/aitc/metrics_hdfs.log' overwrite into table metlog_json;
        Loading data to table default.metlog_json
        Deleted hdfs://localhost/user/hive/warehouse/metlog_json
        OK
        Time taken: 14.087 seconds
        hive> select * from metlog_json limit 5;
        OK
        {"severity":6,"timestamp":"2012-07-18T21:13:25.687518","metlog_hostname":"aitc1.web.mtv1.dev.svc.mozilla.com","fields":{"headers":{"path":"/1.0/11224/apps/","host":"dev-aitc5.services.mozilla.com:443","User-Agent":""},"threadlocal":{}},"metlog_pid":26482,"logger":"","type":"wsgi","payload":"","env_version":"0.8"}
        {"severity":6,"timestamp":"2012-07-18T21:13:26.140094","metlog_hostname":"aitc1.web.mtv1.dev.svc.mozilla.com","fields":{"headers":{"path":"/1.0/11224/apps/","host":"dev-aitc5.services.mozilla.com:443","User-Agent":""},"threadlocal":{}},"metlog_pid":26483,"logger":"","type":"wsgi","payload":"","env_version":"0.8"}
        {"severity":6,"timestamp":"2012-07-18T21:13:26.400760","metlog_hostname":"aitc1.web.mtv1.dev.svc.mozilla.com","fields":{"headers":{"path":"/1.0/11224/devices/","host":"dev-aitc5.services.mozilla.com:443","User-Agent":""},"threadlocal":{}},"metlog_pid":26482,"logger":"","type":"wsgi","payload":"","env_version":"0.8"}
        {"severity":6,"timestamp":"2012-07-18T21:13:26.555777","metlog_hostname":"aitc1.web.mtv1.dev.svc.mozilla.com","fields":{"headers":{"path":"/1.0/11224/devices/","host":"dev-aitc5.services.mozilla.com:443","User-Agent":""},"threadlocal":{}},"metlog_pid":26482,"logger":"","type":"wsgi","payload":"","env_version":"0.8"}
        {"severity":6,"timestamp":"2012-07-18T21:13:27.018063","metlog_hostname":"aitc1.web.mtv1.dev.svc.mozilla.com","fields":{"headers":{"path":"/1.0/11224/apps/Mnw_2ofOKGhIpXSYLd0LfHSH-BY","host":"dev-aitc5.services.mozilla.com:443","User-Agent":""},"threadlocal":{}},"metlog_pid":26482,"logger":"","type":"wsgi","payload":"","env_version":"0.8"}
        Time taken: 1.458 seconds

5. Enable the JAR in Hive and bind the `exjuson_tuple` name to the
   class 

   ::

       hive> add jar hdfs:///metlog/lib/MetlogHive.jar;
       converting to local hdfs:///metlog/lib/MetlogHive.jar
       Added /tmp/vagrant/hive_resources/MetlogHive.jar to class path
       Added resource: /tmp/vagrant/hive_resources/MetlogHive.jar
       hive> create temporary function exjson_tuple as 'org.mozilla.services.json.ExJSONTuple';
       OK
       Time taken: 0.658 seconds
       hive> 
   
6.  You should now be able to run a simple query. ::

        hive> select f1, f2, f3 from metlog_json m lateral view exjson_tuple(m.json, "fields.headers.host", "severity", "fields.headers.path") b as f1, f2, f3 limit 5;
        Total MapReduce jobs = 1
        Launching Job 1 out of 1
        Number of reduce tasks is set to 0 since there's no reduce operator
        Starting Job = job_201208091751_0001, Tracking URL = http://localhost:50030/jobdetails.jsp?jobid=job_201208091751_0001
        Kill Command = /usr/lib/hadoop/bin/hadoop job  -Dmapred.job.tracker=localhost:8021 -kill job_201208091751_0001
        2012-08-09 19:02:14,179 Stage-1 map = 0%,  reduce = 0%
        2012-08-09 19:02:20,245 Stage-1 map = 100%,  reduce = 0%
        2012-08-09 19:02:24,278 Stage-1 map = 100%,  reduce = 100%
        Ended Job = job_201208091751_0001
        OK
        dev-aitc5.services.mozilla.com:443     6    /1.0/11224/apps/
        dev-aitc5.services.mozilla.com:443     6    /1.0/11224/apps/
        dev-aitc5.services.mozilla.com:443     6    /1.0/11224/devices/
        dev-aitc5.services.mozilla.com:443     6    /1.0/11224/devices/
        dev-aitc5.services.mozilla.com:443     6    /1.0/11224/apps/Mnw_2ofOKGhIpXSYLd0LfHSH-BY
        Time taken: 32.534 seconds
        hive> 


Building the plugin
-------------------

This plugin has been built with the following tools.  Lower versions of each package may work, but have not been tested.

    * Java SDK.  (>= 1.6.0.33) 
    * Apache Ant (>= 1.8.2)
    * Apache Ivy (>= 2.3.0-rc1)
    * Ant-JUnit  (>= 1.8.0)

Assuming your Ivy and JUnit jar files are located in ~/.ant/lib, ant
should be able to run all targets in the build.xml file.  Your
~/.ant/lib should look something like this ::

    ~ > ls -l ~/.ant/lib
    total 2872
    -rw-r--r--  1 victorng  staff   1.2M 16 Apr 00:02 ivy-2.3.0-rc1.jar
    -rw-r--r--  1 victorng  staff   247K 30 Sep  2011 ant-junit.jar

    ~ > 

Building a JAR file ::

    $ ant

Running tests ::

    $ ant test

Generating the Javadoc ::

    $ ant javadoc
