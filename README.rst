===========
metlog-hive
===========

metlog-hive is a set of extensions for Apache Hive to provide richer query support
for JSON structures that Metlog produces. 

Currently we provide a User Defined Table Function (UDTF)
`exjson_tuple` that supports indexing into a JSON structure at an
arbitrary depth.  This is meant to be a drop in replacement for calls
to the standard Hive `json_tuple` UDTF.

More information about how Mozilla Services is using Metlog (including what is
being used for a router and what endpoints are in use / planning to be used)
can be found on the relevant `spec page
<https://wiki.mozilla.org/Services/Sagrada/Metlog>`_.
