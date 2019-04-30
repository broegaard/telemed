FRDS.Broker Library
==============

The **FRDS.Broker** library is a teaching oriented implementation of
the *Broker* architectural pattern for distributed remote method
invocation. It defines the central roles of the pattern and provides
implementations of those roles that are not domain/use case
specific. It provides a JSON based (GSon library) *Requestor*
implementation, and implementations of the *ClientRequestHandler* and
*ServerRequestHandler* roles in both a Java socket based and a
Http/URI tunneling based variants. The latter is based upon the
UniRest and Spark-Java libraries.

The **Broker** pattern and the source code is explained in detail in
the book
[Flexible, Reliable, Distributed Software (FRDS)](https://leanpub.com/frds),
by *Henrik Bærbak Christensen / Aarhus University / www.baerbak.com*.

Version history is available [here](version.md).

License is [Apache 2](LICENSE).

I only need the Broker Library, what is the dependency?
-----

The Broker library is available in JCenter. 

Get it using Gradle:

    dependencies {
      compile group: 'com.baerbak.maven', name: 'broker', version: '1.4'
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
       Folder: *telemed*. Note that his project also contains core test
       code for the broker library.
       
  3. It has the source code (and development diary) of the GameLobby
     system, which is used in FRDS to show how to create remote
     objects on the server, and handle multi-object method
     dispatch. Folder: *gamelobby*.
  
  4. It has the source code of an implementation of TeleMed that uses
     REST instead of Broker for communication. Folder: *telemed-rest*.
     
  5. It has the source code of a rudimentary REST server, *PasteBin*,
     used in the FRDS book to illustrate the POST and GET verbs of
     HTTP. Folder: *pastebin*.
  
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

Two variants of the Broker are provided

  * Socket: Socket based Client- and ServerRequestHandler implementations.

  * URI Tunnel: HTTP/URI Tunnel based Client- and ServerRequestHandler
    implementations similar to what most WebService frameworks will
    produce.

### GameLobby 

The GameLobby is a more complex distributed system. The domain is
players that want to create remote games, that friends can join so
play over the internet is possible. The game itself is not interesting
here, as the learning goal is to demonstrate code that:

  * Create objects on the server side, new servants, and allows the
    clients to bind client proxies to them for remote method calls.
    
  * Implement the invoker to handle multi-type dispatch in a way that
    avoids 'blob' invokers, by creating sub-invokers, one for each
    type of role/servant type in the system.
    
  * Also my [test-driven development diary](gamelobby/diary.md) is
    included in which I develop the system from scratch in about 14
    hours, including the documentation effort.
    
### PasteBin

PasteBin is a rudimentary web server that allows storing and
retrieving simple text, similar to a server based clipboard.

PasteBin is explained in the separate [README](pastebin/README.md).

How do I run TeleMed?
---

You first start the TeleMed server, next you invoke the client
multiple times to upload or fetch blood pressure measurements.

To start the TeleMed server, open a shell and issue

    gradle :telemed:serverSocket

To upload blood pressure (123,99) for patient with id=241248 to the
server located at IP localhost, open another shell and issue

    gradle -q :telemed:homeSocket -Pid=241248 -Psys=123 -Pdia=99 -Phost=localhost

To fetch the last week's data for patient with id=87, issue

    gradle -q :telemed:homeSocket -Pop=fetch -Pid=87
    
If you want to use a HTTP URI Tunnel protocol instead, just replace
`serverSocket` by `serverHttp`, and `homeSocket` with `homeHttp`. The
HTTP based version can also be viewed from the web page (last part of
URI must match requested patient id) [http://localhost:4567/bp/87]
    
Review `gradle.properties` for default values for the arguments to the
server and the client.

How do I run GameLobby?
---

The GameLobby system is primarily intended to be reviewed through the
test cases, but manual tests can be made using the command line. The
focus is on the creation of a shared game instance, so no usefull game
behavior is implemented.

Start server

    gradle :gamelobby:lobbyServer
    
Note the IP of the running server (I use `10.11.96.127` below).

Let user 'Pedersen' create a game

    csdev@m31:~/proj/broker$ gradle -q :gamelobby:lobbyClient -Pop=create -Phost=10.11.96.127 -Pplayer=Pedersen
    LobbyClient: Asked to do operation create for player Pedersen
     Future created, the join token is game-1

And let 'Findus' join the game, using the provided game token `game-1` as id:

    csdev@m31:~/proj/broker$ gradle -q :gamelobby:lobbyClient -Pop=join -Pid=game-1 -Pplayer=Findus -Phost=10.11.96.127
    LobbyClient: Asked to do operation join for player Findus
     Future joined, available is true
     The Game id is 63dfc101-29e2-414b-b8a1-3c0bf777eb7e
     The Game's 1st player is Pedersen
     The Game's 2nd player is Findus

Finally, once the game is created clients can make 'moves' (here id is
assigned to the real game id that was provided in the join output):

    csdev@m31:~/proj/broker$ gradle -q :gamelobby:lobbyClient -Pop=move -Pid=63dfc101-29e2-414b-b8a1-3c0bf777eb7e -Phost=10.11.96.127
    LobbyClient: Asked to do operation move for player Pedersen
    The Game id is 63dfc101-29e2-414b-b8a1-3c0bf777eb7e
    The Game's PLAYER IN TURN is Pedersen
    A move was made, and now PLAYER IN TURN is Findus
    
    csdev@m31:~/proj/broker$ gradle -q :gamelobby:lobbyClient -Pop=move -Pid=63dfc101-29e2-414b-b8a1-3c0bf777eb7e -Phost=10.11.96.127
    LobbyClient: Asked to do operation move for player Pedersen
    The Game id is 63dfc101-29e2-414b-b8a1-3c0bf777eb7e
    The Game's PLAYER IN TURN is Findus
    A move was made, and now PLAYER IN TURN is Pedersen

Note: The current oversimplified design does not have a notion of
client identity. Ideally, two clients ought to be running, one
handling Pedersen, and the other Findus, and the server should be able
to tell which is which. This is left as an exercise for the reader :).

How do I run the REST based TeleMed?
---

Start the REST server

    gradle :telemed-rest:serverRest
    
The client just executes a CRUD scenario, most of the data is
hardwired.

    gradle :telemed-rest:demoRest
    
You can avoid the delete by

    gradle :telemed-rest:demoRest -Pdelete=false
    
In addition you can review uploaded observations for a patient, for instance [localhost:4666/bp/for/251248-1234/](localhost:4666/bp/for/251248-1234/).


Credits
===

  Author: *Henrik Baerbak Christensen* / Aarhus University /
    	  [www.baerbak.com](www.baerbak.com)


### Contribution guidelines ###

* Contact Henrik if you want to contribute and he will set things up...





