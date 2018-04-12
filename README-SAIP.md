TM16 - Case study for 'Software Architecture in Practice'
===

*by Henrik Bærbak Christensen, 2016 - 2018*

Goal
===

  * To define a small but realistic case of a client server system for
    tele medicine

  * To serve as starting point for mandatory exercises in SAiP course

Overview
===

TM16 is a scaled down tele medicine system, inspired by
the [Net4Care research project](www.net4care.org) . 

Please consult resources on the SAiP web page for more detail.

How to I use it?
===

You need Gradle installed and thus Java8 or later.

Start the telemed server in a shell (in-memory database)

    gradle serverHttp
    
Scenario 1: Next upload a single blood pressure measurement of
(127,76) for patient with id=123456 to the server running on
'localhost' (in another shell)

    gradle homeHttp -Pid=123456 -Psys=127 -Pdia=76 -Phost=localhost
    
To fetch all uploaded measurements for the last week for id=123456

    gradle homeHttp -Pid=123456 -Pop=fetch
    
Scenario 2: As physician, browse all measurements made by patient
id=123456 by pointing your web browser at

  * [localhost:4567/bp/123456](localhost:4567/bp/12345)
  

### Server with database

An in-memory database is only interesting for testing. To run the
complete system, you need to start a MongoDB database and connect the
server to it.

Ensure you have docker installed and then start a mongodb container

    docker run -d --name db0 -p 27017:27017 mongo --noprealloc --smallfiles
    
    
Next start the server with a connection to it

    gradle serverHttp -Pdb=localhost
    

Context
===

The SAiP related source code is a branch of the 'FRDS.Broker' library,
used in the *Flexible, Reliable, Distributed Software* book, published
on [leanpub.com](leanpub.com), by *Henrik Bærbak Christensen*.

You can find it on [Bitbucket](bitbucket.com).



