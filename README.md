# README #

This README would normally document whatever steps are necessary to
get your application up and running.

### What is this repository for? ###

* Quick summary
* Version
* [Learn Markdown](https://bitbucket.org/tutorials/markdowndemo)

### How do I run it? ###

  Targets :
 
    test:      Run JUnit unit- and integration tests
       Find the output in folder 'TEST-RESULTS'
    	
  === Execution (Socket) ===
    	
    serverSocket:
      Start the TeleMed socket based server 
    
    homeSocket:
      Execute a single operation of the home client
      Defaults to: 'store' bloodpressure (120,70) for Nancy (pid01)
      
      Set parameters to change it to:
      -Dop=fetch  : fetch last week's blood pressure measurements
      -Did=(id)   : do operation on patient with given id
      -Dsys=(s)   : store systolic pressure 's'
      -Ddia=(f)   : store diastolic pressure 'd'
      -Dhost=(h)  : hostname or IP of server (default: localhost)
      
      Examples:

       ant homeSocket -Did=pid02 -Dsys=156 -Ddia=87
            = store blood pressure (156,87) for patient 'pid02'

       ant homeSocket -Dop=fetch -Did=pid02
           = fetch last week's measurements for patient 'pid02'

    ***

    Start the 'server' in one shell, and run 'home' multiple times
      in another to send observations to the server.

  === Execution (HTTP / URI Tunneling) ===

    serverHttp:
      Start the HTTP/URI Tunnel based server, hit ctrl-c to stop it again 

    homeHttp: 
      Execute a 'measurement' and upload from the home
      Defaults to: 'store' bloodpressure (120,70) for 'pid01' on localhost

      The same set of parameters can be applied as for the socket based.
 
    To review uploaded tele observations, browse to
      (host):4567/bp/{patientid}

  === Householding ===

    clean:     Removes all bytecode, clean the project
    javadoc:   Generate JavaDoc (output in doc/index.html)


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

