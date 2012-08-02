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


Installation
------------

The best way to expose the JAR file is via HDFS as all your Hadoop nodes
will then have access to the new plugin.  Otherwise, you're free to
copy the MetlogHive jar file manually to all nodes to their local
filesystem.

Here's an example of dropping the JAR file into HDFS and making the
`exjson_tuple` function available for use ::

    $ hadoop dfs -mkdir /lib/hive
    $ hadoop dfs -put /opt/metlog/hadoop/hive/MetlogHive.jar /lib/hive/MetlogHive.jar
    $ hadoop dfs -ls /lib/hive/
    Found 1 items
    -rw-r--r--   1 vagrant supergroup     816600 2012-07-31 04:30 /lib/hive/MetlogHive.jar
    $ hive
    Hive history file=/tmp/vagrant/hive_job_log_vagrant_201207310430_1529091028.txt
    hive> add jar hdfs:///lib/hive/MetlogHive.jar;
    converting to local hdfs:///lib/hive/MetlogHive.jar
    Added /tmp/vagrant/hive_resources/MetlogHive.jar to class path
    Added resource: /tmp/vagrant/hive_resources/MetlogHive.jar
    hive> create temporary function exjson_tuple as 'org.mozilla.services.json.ExJSONTuple';
    OK
    Time taken: 1.839 seconds
    hive> 

If you have Hive scripts, you should put these two lines into the top of all your scripts.

    add jar hdfs:///lib/hive/MetlogHive.jar;
    create temporary function exjson_tuple as 'org.mozilla.services.json.ExJSONTuple';

This will expose a UDTF that will let you extract bits of a JSON blob and turn them into tuples for Hive.

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


Example Usage
-------------

Sample output ::

    hive> select f1, f2, f3 from metrics m lateral view exjson_tuple(m.metlog_val, "fields.headers.host", "severity", "fields.headers.path") b as f1, f2, f3;
    Total MapReduce jobs = 1
    Launching Job 1 out of 1
    Number of reduce tasks is set to 0 since there's no reduce operator
    Starting Job = job_201207301923_0023, Tracking URL = http://localhost:50030/jobdetails.jsp?jobid=job_201207301923_0023
    Kill Command = /usr/lib/hadoop/bin/hadoop job  -Dmapred.job.tracker=localhost:8021 -kill job_201207301923_0023
    2012-07-31 03:41:14,114 Stage-1 map = 0%,  reduce = 0%
    2012-07-31 03:41:20,135 Stage-1 map = 100%,  reduce = 0%
    2012-07-31 03:41:24,154 Stage-1 map = 100%,  reduce = 100%
    Ended Job = job_201207301923_0023
    OK
    dev-aitc5.services.mozilla.com:443      6     /1.0/11224/apps/
    dev-aitc5.services.mozilla.com:443      6     /1.0/11224/apps/
    dev-aitc5.services.mozilla.com:443      6     /1.0/11224/devices/
    dev-aitc5.services.mozilla.com:443      6     /1.0/11224/devices/
    dev-aitc5.services.mozilla.com:443      6     /1.0/11224/apps/Mnw_2ofOKGhIpXSYLd0LfHSH-BY
    dev-aitc5.services.mozilla.com:443      6     /1.0/11224/apps/
    ...



Building the plugin
-------------------

Requirements:  

    * Java SDK.  (>= 1.6.0.33) 
    * Apache Ant (>= 1.8.2)
    * Apache Ivy (>= 2.3.0-rc1)
    * JUnit      (>= 4.10)

Assuming your Ivy and JUnit jar files are located in ~/.ant/lib, ant
should be able to run all targets in the build.xml file.  Your
~/.ant/lib should look something like this ::

    ~ > ls -l ~/.ant/lib
    total 2872
    -rw-r--r--  1 victorng  staff   1.2M 16 Apr 00:02 ivy-2.3.0-rc1.jar
    -rw-r--r--  1 victorng  staff   247K 30 Sep  2011 junit-4.10.jar
    ~ > 

Building a JAR file ::

    $ ant dist

Running tests ::

    $ ant test

Generating the Javadoc ::

    $ ant javadoc
