Redis - using Secondary Indices
===============================

*This is a copy from the E20* branch.

How to get started
----

Start the redis on port 6379 (only works on localhost)

    ./startredis.sh

Next, connect the serverHttp to it

    ./gradlew serverHttp -Pdb=redis

This should start the server, now use the homeHttp client as usual

Inspecting the redis
----------

You simply fire commands using telnet

    telnet localhost 6379

To see all keys, just issue

    keys *

now you can inspect individual measurement using

    get pid@....

Secondary indices
-------

Is explained in the RedisXDSAdapter code:

 Primary storage of observations is simple (key,value),
 where key = (pid)+SEPARATOR+(timestamp).

 Example: SET pid001@08112018 (hl7)

 Secondary storage allows queries for given pid and
 time interval (start, end). The secondary index
 is a sorted set, where insertion ZADDs an entry
 under key pid with score (timestamp) and member (key)

 Example: ZADD pid001 08112018 pid001@08112018

 Queries are then made using ZRANGEBYSCORE

 Example: ZRANGEBYSCORE pid001 07112018 08112018

 Note, real long unix epoch timestamps are used,
 the above shown are just for clarity.



