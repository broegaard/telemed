# README #

*Author: Henrik Baerbak Christensen / Aarhus University / www.baerbak.com*

### What is this repository for? ###

* Quick summary
* Version
* [Learn Markdown](https://bitbucket.org/tutorials/markdowndemo)

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

* Writing tests
* Code review
* Other guidelines

### Who do I talk to? ###

* Repo owner or admin
* Other community or team contact

Diary
===

6-4-2018
---

Cloned the repo with the broken test. Imported into IntelliJ.

Removed all REST oriented code as E17 experience is that REST does not
architecturally match the Broker well.

Renamed 'registerRoutes' in demo to 'start' which was the name adopted
in RSA broker code.

