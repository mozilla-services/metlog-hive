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

TODO: document loading the jar file via HDFS so that all Hadoop
cluster nodes have access to the plugin.

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



