TeleMed - Case study for 'Software Architecture in Practice'
===

*by Henrik Bærbak Christensen, 2016 - 2020*

Goal
===

  * To define a small but realistic case of a client server system for
    tele medicine

  * To serve as starting point for mandatory exercises in SAiP course

Overview
===

TeleMed is a scaled down tele medicine system, inspired by
the Net4Care research project. 

Please consult resources on the SAiP web page for more detail.

How to I use it?
===

You need Gradle installed and Java8 or later.

Start the telemed server in a shell (in-memory database)

    ./gradlew serverHttp
    
Scenario 1: Next upload a single blood pressure measurement of
(127,76) for patient with id=123456 to the server running on
'localhost' (in another shell)

    ./gradlew homeHttp -Pid=123456 -Psys=127 -Pdia=76 -Phost=localhost
    
To fetch all uploaded measurements for the last week for id=123456

    ./gradlew homeHttp -Pid=123456 -Pop=fetch
    
Scenario 2: As physician, browse all measurements made by patient
id=123456 by pointing your web browser at

  * [localhost:4567/bp/123456](localhost:4567/bp/123456)

If you run the server on a given host with hostname/ip "hostname",
then tell the client it using switch -Phost=(hostname).

Server with database
--------------------

An in-memory database is only interesting for testing. To run the
complete system, you need to start a MongoDB database and connect the
server to it.

Ensure you have docker installed and then start a mongodb container

    docker run -d --name db0 -p 27017:27017 mongo:4.4 

(The default MongoDB port '27017' is mapped into the host.)
    
Next start the server with a connection to it

    ./gradlew serverHttp -Pdb=localhost

If you run the MongoDB on another host, then tell the server using
the -Pdb=(hostname) switch. The portnumber is hardwired, sorry.

You can inspect the contents of the mongodb by

    docker exec -ti db0 mongo
    
which will bring you into the running database's shell

Then use this weird syntax to switch to the xds database and view collections

    use xds
    db.tm16.find().pretty()
    exit
    
Performance Testing
===

First, you need to ensure the 'pe hack' is enabled in the TeleMed
server! You do this by providing setting the switch 'pehack' to
'true' (all lowercase!), like

    ./gradlew serverHttp -Ppehack=true

This switch works both in the in-memory and in the MongoDB variant.
It will ensure that the *server* and not the client will assign
timestamps to recorded, which is important because JMeter will
send measurements with identical timestamps.

Next, you can find a JMeter sample test plan in
'TeleMed-Test-Plan.jmx'. Start JMeter and open it.

Digging into the Code base
===

The TeleMed code base is located in the /broker/telemed/ folder.

I advice you start by taking a look at the learning test

    test/java/telemed/scenario/TestStory1.java

In this JUnit test case, the `setup()` method initializes the
Broker pattern, using an in-memory fake object implementation of
the networking layer, and the test cases demonstrates the Story one
scenario from the slides on TeleMed.

Next, have a look at HTTP based distribution case

    main/java/saip/main/ServerMainHTTP.java
    main/java/saip/main/HomeClientHTTP.java

Context
===

The SAiP related source code is a branch of the 'FRDS.Broker' library,
used in the *Flexible, Reliable, Distributed Software* book, published
on [leanpub.com](leanpub.com), by *Henrik Bærbak Christensen*.

You can find it on [Bitbucket](bitbucket.com).



