Broker Library
==============

From the book *Flexible, Reliable Distributed Software*, by *Henrik
Bærbak Christensen / Aarhus University / www.baerbak.com*

What is this repository for?
-----------

Version 1.0 of the Broker library used in teaching context based on
the book *Flexible, Reliable Distributed Software*.

This repository serves two purposes.

    1. It has the source code of the `frds.broker` library that
       contains central roles for the **Broker** pattern, as well as
       some default implementations for some of these. It also
       contains the delivery code for publishing the library on the
       JCenter repository.
    2. It has the source code of the TeleMed system, which is used in
       the FRDS book to show the Broker pattern in action.

The TeleMed system is a small distributed system in which patients
may upload blood pressure measurements to a central medical server.

Its primary goal is to show how the **Broker** pattern is implemented.

Three variants of the Broker is provided

  * Socket: Socket based Client- and ServerRequestHandler implementations.

  * URI Tunnel: HTTP/URI Tunnel based Client- and ServerRequestHandler
    implementations similar to what most WebService frameworks will
    produce.


### How do I run it? ###

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
HTTP based version can also be viewed from the web page [http://localhost:4567/bp/pid=87]
    

  Author: Henrik Baerbak Christensen / Aarhus University
    	  www.baerbak.com"""

### How do I get set up? ###

* Summary of set up
* Configuration
* Dependencies
* Database configuration
* How to run tests
* Deployment instructions

### Contribution guidelines ###

* Contact Henrik if you want to contribute and he will set things up...

### Who do I talk to? ###

* Repo owner or admin
* Other community or team contact

### Internal - publishing ###

* `./gradlew :broker:bintrayUpload` and hit the publish button.

Diary
===

ToDo
----

[] Include status code in IPCException so it can be used at app level
[] Review library code to include more appropriate logging.
[] Change the header to be to FRDS, not FRS
[] Run JaCoCo and verify coverage
[] homeHttp fails for empty record of measurements

6-4-2018
---

Initiated repo with the frs.broker library from RSA/TS17D; next added
demo code from frsproject/broker.

Cloned the repo with the broken test. Imported into IntelliJ.

Removed all REST oriented code as E17 experience is that REST does not
architecturally match the Broker well.

Renamed 'registerRoutes' in demo to 'start' which was the name adopted
in RSA broker code.

9-04-2018
---

Made all test cases run.

Next action: Do a Diff with all previous variant.

Did 'frsproject/broker'. All changes are OK, nothing missing.

Did 'frsproject/breakthrough'. Our new broker has logging and has LICENS
which the breakthrough has not. OK.

Did 'book/src/broker'. Some comment stuff (missing year in copyright),
but no essential stuff is missing!

THUS - removing the stuff from the aforementioned repos.

    Book chapter/broker: REMOVED.
    frsproject/broker: REMOVED.
    
Regarding the 'frsproject' code base I

    gradle :broker:jar
    
and copy the jar file to the respective projects one by one.





