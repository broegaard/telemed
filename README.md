FRDS.Broker Library
==============

From the book *Flexible, Reliable, Distributed Software* (FRDS), by
*Henrik Bærbak Christensen / Aarhus University / www.baerbak.com*,
available at LeanPub.com.

What is this repository for?
-----------

Version 1.0 of the Broker library used in teaching context based on
the book *Flexible, Reliable, Distributed Software*.

This repository serves two purposes.

  1. It has the source code of the `frds.broker` library that contains
       central roles for the **Broker** pattern, as well as some
       default implementations for some of these. 
       
  2. It has the source code of the TeleMed system, which is used in
       the FRDS book to show the Broker pattern in action, and
       contains tests of both the Broker and TeleMed implementation.

The TeleMed system is a small distributed system in which patients
may upload blood pressure measurements to a central medical server.

Its primary goal is to show how the **Broker** pattern is
implemented. Note, it is not a full Broker as in Java RMI as it has
two limitations (more detail in FRDS):
  
  * No *Name Service* is used, so object id must be defined by the
     code base and/or from interaction with the server.
  * It is *pure client/server* relation, so server objects can never
     invoke methods on client side objects (no Observer pattern
     possible).

Two variants of the Broker is provided

  * Socket: Socket based Client- and ServerRequestHandler implementations.

  * URI Tunnel: HTTP/URI Tunnel based Client- and ServerRequestHandler
    implementations similar to what most WebService frameworks will
    produce.


How do I run it?
---

You first start the TeleMed server, next you invoke the client
multiple times to upload or fetch blood pressure measurements.

To start the TeleMed server, open a shell and issue

    gradle serverSocket

To upload blood pressure (123,99) for patient with id=241248 to the
server located at IP localhost, open another shell and issue

    gradle -q homeSocket -Pid=241248 -Psys=123 -Pdia=99 -Phost=localhost

To fetch the last week's data for patient with id=87, issue

    gradle -q homeSocket -Pop=fetch -Pid=87
    
If you want to use a HTTP URI Tunnel protocol instead, just replace
`serverSocket` by `serverHttp`, and `homeSocket` with `homeHttp`. The
HTTP based version can also be viewed from the web page
[http://localhost:4567/bp/pid=87]
    
Review `gradle.properties` for default values for the arguments to the
server and the client.

  Author: *Henrik Baerbak Christensen* / Aarhus University
    	  www.baerbak.com"""


### Contribution guidelines ###

* Contact Henrik if you want to contribute and he will set things up...





