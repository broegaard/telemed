FRDS.Broker Library
==============

From thebook
[Flexible, Reliable, Distributed Software (FRDS)](https://leanpub.com/frds),
by *Henrik Bærbak Christensen / Aarhus University / www.baerbak.com*.

Version history is available [here](version.md).

License is [Apache 2](LICENSE).

I only need the Broker Library, what is the dependency?
-----

The Broker library is available in JCenter. 

Get it using Gradle:

    dependencies {
      compile group: 'com.baerbak.maven', name: 'broker', version: '1.2'
    }

What is this repository for?
-----------

The Broker library used in teaching context based on
the book *Flexible, Reliable, Distributed Software*.

This repository serves multiple purposes.

  1. It has the source code of the `frds.broker` library that contains
       central roles for the **Broker** pattern, as well as some
       default implementations for some of these. Folder: *broker*. 
       
  2. It has the source code of the TeleMed system, which is used in
       the FRDS book to show the Broker pattern in action, and
       contains tests of both the Broker and TeleMed implementation.
       Folder: *demo*.
       
  3. It has the source code (and development diary) of the GameLobby
     system, which is used in FRDS to show how to create remote
     objects on the server, and handle multi-object method
     dispatch. Folder: *demo2*.
  
### TeleMed

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

### GameLobby

The GameLobby is a more complex distributed system. The domain is
players that want to create remote games, that friends can join so
distributed play is possible. The game itself is not interesting here,
as the learning goal is to demonstrate code that

  * Create objects on the server side, new servants, and allows the
    clients to bind client proxies to them for remote method calls.
    
  * Implement the invoker to handle multi-type dispatch in a way that
    avoids 'blob' invokers, by creating sub-invokers, one for each
    type of role/servant type in the system.
    
  * Also my [test-driven development diary](demo2/diary.md) is
    included in which I develop the system from scratch in about 14
    hours, including the documentation effort.

How do I run TeleMed?
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

How do I run GameLobby?
---

The GameLobby system is primarily intended to be reviewed through the
test cases, but manual tests can be made using the command line.

Start server

    gradle lobbyServer
    
Note the IP of the running server (I use `10.11.96.127` below).

Let user 'Pedersen' create a game

    csdev@m31:~/proj/broker$ gradle -q lobbyClient -Pop=create -Phost=10.11.96.127 -Pplayer=Pedersen
    LobbyClient: Asked to do operation create for player Pedersen
     Future created, the join token is game-1

And let 'Findus' join the game, using the provided game token `game-1`

    csdev@m31:~/proj/broker$ gradle -q lobbyClient -Pop=join -Ptoken=game-1 -Pplayer=Findus -Phost=10.11.96.127
    LobbyClient: Asked to do operation join for player Findus
     Future joined, available is true
     The Game id is 609833b2-bf8d-421c-beb1-9aac1464aac2
     The Game's 1st player is Pedersen
     The Game's 2nd player is Findus


Credits
===

  Author: *Henrik Baerbak Christensen* / Aarhus University /
    	  [www.baerbak.com](www.baerbak.com)


### Contribution guidelines ###

* Contact Henrik if you want to contribute and he will set things up...





