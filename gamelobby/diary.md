Development Diary for multi-object Broker / Demo2
=================================================

*Note: The diary refers to 'demo2' which has been renamed to
'telemed-rest' after the diary was written.*


Goal: To develop 'demo2' which uses multiple objects.

Day 1
-----

Subgoal: Develop the domain implementation.

Process: I used TDD to drive three abstractions into place using
small steps in a Story 1 test case:

      @Test
      public void shouldHandleStory1() {
        // Get the game lobby singleton
        GameLobby lobby = GameLobbyServant.getInstance();
        assertThat(lobby, is(not(nullValue())));

        // Ask lobby to create a game for a beginner (playerLevel = 0).
        // A Future is returned, that is a placeholder for a future
        // game that will eventually be able to return a live game.

        FutureGame player1Future = lobby.createGame("Pedersen", 0);
        assertThat(player1Future, is(not(nullValue())));

        // Get the token for my fellow players to enter when wanting
        // to join my game. The token may appear on a web site
        // next to player 1's name so player 2 can see it; or
        // some other mechanism must be employed by the two players
        // for player 2 to get hold of the token.
        String joinToken = player1Future.getJoinToken();
        assertThat(joinToken, is(not(nullValue())));

        // As a second player has not yet joined, the game
        // is not yet created
        assertThat(player1Future.isAvailable(), is(false));

        // Second player - wants to join the game using the token
        FutureGame player2Future = lobby.joinGame("Findus", joinToken);
        assertThat(player2Future, is(not(nullValue())));

        // Now, as it is a two player game, both players see
        // that the game has become available.
        assertThat(player1Future.isAvailable(), is(true));
        assertThat(player2Future.isAvailable(), is(true));

        // And they can manipulate it; not really interesting as
        // our focus it just the multiple objects exchanged.
        Game gameForPlayer1 = player1Future.getGame();
        assertThat(gameForPlayer1.getPlayerName(0), is("Pedersen"));
        assertThat(gameForPlayer1.getPlayerName(1), is("Findus"));

        // Our second player sees the same game state
        Game gameForPlayer2= player2Future.getGame();
        assertThat(gameForPlayer2.getPlayerName(0), is("Pedersen"));
        assertThat(gameForPlayer2.getPlayerName(1), is("Findus"));
      }

Time: About 3.5 hours

Day 2
-----

Subgoal 1: TDD the two missing roles of Broker (ClientProxies +
Invokers) into existence. I simply copy the above test case,
comment out everything except the first statement that must trigger a
remote call, create the client proxy for it, use the
LocalMethodCallClientRequestHandler and create the invoker to accept
the incoming call. All these abstractions I make verbose, that is,
simply print tracing information on stdout. This way my first step is
just to set up the proxy+invoker, and ensure the call chain is
correct.

### Iteration 1 

So, the first step entails creating GameLobbyJSONInvoker
and GameLobbyProxy as just temporary test stubs. commit: e62519c.

### Iteration 2

Added first remote call

    FutureGame player1Future = lobbyProxy.createGame("Pedersen", 0);
    assertThat(player1Future, is(not(nullValue())));

which of course fails.

So I code the first part of the broker chain methods using 'obvious
implementation':

    @Override
    public FutureGame createGame(String playerName, int playerLevel) {
      FutureGame game =
        requestor.sendRequestAndAwaitReply("none", "gamelobby_create_game_method",
                FutureGame.class, playerName, playerLevel);
      return game;
    }

Obvious? Well, yes, as the requestor only has one method and the
parameters are dictated. The only weird one is perhaps the objectId =
"none" but this is because there only IS one lobby object on the
server side, and thus no object id is needed - it will simply be
ignored by the Invoker.

Run test. Null pointer exception in StandardJSONRequestor.

    java.lang.NullPointerException
        at frds.broker.marshall.json.StandardJSONRequestor.sendRequestAndAwaitReply(StandardJSONRequestor.java:56)
        at gamelobby.client.GameLobbyProxy.createGame(GameLobbyProxy.java:41)
        at gamelobby.client.TestClientScenario.shouldHandleStory1OnClient(TestClientScenario.java:64)


Commit 10837fd.

Why? The invoker returns null, of course.

So, drive code into the invoker. Getting inspiration from the TeleMed
invoker code I end up with a first draft:

    @Override
    public ReplyObject handleRequest(String objectId, String operationName, 
                                     String payload) {
      ReplyObject reply = null;

      FutureGame game = lobby.createGame("Pedersen", 0);

      reply = new ReplyObject(HttpServletResponse.SC_CREATED,
              gson.toJson(game));

      return reply;
    }

Run test - now GSON complains that FutureGame has no default
constructor. This happens often - the marshalling and Broker code has
some requirements over and above single process OO programming.  So, I
have to make that - of course it is the FutureGameServant that needs
one.

BOOM. I return object references, right? We cannot do that; we need to
return record types with a unique ID (identifying the object on the
server side), and make the requestor create a corresponding client
proxy that communicate on that particular object id.

So how to proceed? First, review the returned reply object;

In LocalMethodCallClientRequestHandler

    @Override
    public ReplyObject sendToServer(RequestObject requestObject) {
      lastRequest = requestObject;
      System.out.println("--> "+ requestObject);
      // The send to the server can be mimicked by a direct method call
      lastReply = invoker.handleRequest(requestObject.getObjectId(), 
          requestObject.getOperationName(), 
          requestObject.getPayload());
      System.out.println("--< "+ lastReply);
      return lastReply;
    }

which for the test case prints

    --> RequestObject{operationName='gamelobby_create_game_method', payload='["Pedersen",0]', objectId='none', versionIdentity=1}
    --< ReplyObject [payload={"joinToken":"42","firstPlayer":"Pedersen"}, errorDescription=null, responseCode=201]

So everything is fine, except the StandardJSONRequestor in the Broker
library part (project 'Broker') cannot deserialize the replyobject. 

The first thing is to provide an Object ID of the servant object as we
need an id that can bind the Servant and the ClientProxy object together.

I need a *Child Test*, so I add test case in the server test cases:

    @Test
    public void shouldCreateUniqueObjectIdForFutureGame() {
      GameLobby lobby = GameLobbyServant.getInstance();
      FutureGame player1Future = lobby.createGame("Pedersen", 0);
      String uniqueObjectId1 = player1Future.getId();
      System.out.println("A: "+ uniqueObjectId1);
    }

and TDD the getId() into place, using the java UUID
generator. Working.

Extend the test case and remove the visual output.

    @Test
    public void shouldCreateUniqueObjectIdForFutureGame() {
      GameLobby lobby = GameLobbyServant.getInstance();
      FutureGame player1Future = lobby.createGame("Pedersen", 0);
      String uniqueObjectId1 = player1Future.getId();
      FutureGame player2Future = lobby.createGame("Hansen", 0);
      String uniqueObjectId2 = player2Future.getId();
      assertThat(uniqueObjectId1, is(not(uniqueObjectId2)));
    }

Ok, Child test passes, so I return to the Requestor issue. Now the
reply object correctly contains the unique ID:

    --> RequestObject{operationName='gamelobby_create_game_method', payload='["Pedersen",0]', objectId='none', versionIdentity=1}
    --< ReplyObject [payload={"joinToken":"42","firstPlayer":"Pedersen","id":"d87b05d0-fa2c-428a-a99d-2e559b42b369"}, errorDescription=null, responseCode=201]

Commit: 6bf4e1e.

Next step contains an important insight: The **only** information
relevant for the client proxy is the **objectID** of the server
object! Why? Because all the Requester needs to do is to create a
ClientProxy for the FutureGame which knows the ID of the servant
object: all methods will just send messages to this object and get the
replies. 

Thus - we need not return a FutureGame as payload in the reply object,
only a unique string which is the ID.

First step is 'visual': In GameLobbyProxy I code


    @Override
    public FutureGame createGame(String playerName, int playerLevel) {
      String id =
        requestor.sendRequestAndAwaitReply("none", "gamelobby_create_game_method",
                String.class, playerName, playerLevel);

      System.out.println("---> got the servant object ID: "+ id);
      return null;
    }

which of course fails, as the Invoker has to be recoded to return
strings. So I code:

    @Override
    public ReplyObject handleRequest(String objectId, String operationName, String payload) {
      ReplyObject reply = null;

      FutureGame game = lobby.createGame("Pedersen", 0);
      String id = game.getId();

      reply = new ReplyObject(HttpServletResponse.SC_CREATED,
              gson.toJson(id));

      return reply;
    }

which makes the test case pass (no error from GSon demarshalling!) and
prints the returned string; but of course fails due to the 'return
null;' in the createGame method.


    --> RequestObject{operationName='gamelobby_create_game_method', payload='["Pedersen",0]', objectId='none', versionIdentity=1}
    --< ReplyObject [payload="2a10a3b7-f593-4b72-a7da-cfa05c64b5d5", errorDescription=null, responseCode=201]
    ---> got the servant object ID: 2a10a3b7-f593-4b72-a7da-cfa05c64b5d5


Now we create the ClientProxy for the FutureGame, and TDD it into
place:

    @Override
    public FutureGame createGame(String playerName, int playerLevel) {
      String id =
        requestor.sendRequestAndAwaitReply("none", "gamelobby_create_game_method",
                String.class, playerName, playerLevel);

      System.out.println("---> got the servant object ID: "+ id);
      FutureGame proxy = new FutureGameProxy(id);
      return proxy;
    }

and the proxy is declared as

    public class FutureGameProxy implements FutureGame, ClientProxy {
      public FutureGameProxy(String objectId) {

      }
      // temporary stub methods for the other FutureGame methods
    }

Now the test case actually succeeds because of our test is just
presence of an object.

    FutureGame player1Future = lobbyProxy.createGame("Pedersen", 0);
    assertThat(player1Future, is(not(nullValue())));

Still - the design is in place, notably the central insight:

> ## Transfering Server Created Objects
>
> To transfer a reference to an object created on the server side,
> you must follow this template
>
>  * Make the Servant object generate an unique ID, and provide an
>    accessor method, like `getId()`.
>  * In the Invoker implementation use a String as marshalling format,
>    and just transfer the unique object id.
>  * On the client side, in the ClientProxy, create a ClientProxy
>    object that stores this unique id.
>
> From then on, all ClientProxy method calls just use the stored id as
> the objectId.

Commit: ad66e06.

Step 5: Clean up and refactoring. I remove some stdout output; and I
refactor the method string to be in a MarshallingConstants class.

    @Override
    public FutureGame createGame(String playerName, int playerLevel) {
      String id =
        requestor.sendRequestAndAwaitReply("none",
                MarshallingConstant.GAMELOBBY_CREATE_GAME_METHOD,
                String.class, playerName, playerLevel);
      FutureGame proxy = new FutureGameProxy(id);
      return proxy;
    }

### Iteration 3

Let us add some of the asserts from the servant test case to our
client test case.

    FutureGame player1Future = lobbyProxy.createGame("Pedersen", 0);
    assertThat(player1Future, is(not(nullValue())));

    String joinToken = player1Future.getJoinToken();
    assertThat(joinToken, is(not(nullValue())));

which fails on the last assert; the getJoinToken is not implemented.

TDD of the FutureGameProxy's getJoinToken follows the standard Broker
implementation schema: call the requestor in the proxy:

    @Override
    public String getJoinToken() {
      String token = requestor.sendRequestAndAwaitReply(getId(),
              MarshallingConstant.FUTUREGAME_GET_JOIN_TOKEN_METHOD, 
              String.class);
      return token;
    }

which gives the 'visual' test output

--> RequestObject{operationName='gamelobby_create_game_method', payload='["Pedersen",0]', objectId='none', versionIdentity=1}
--< ReplyObject [payload="8f2ebb1d-8921-486e-b029-188169913a93", errorDescription=null, responseCode=201]
--> RequestObject{operationName='futuregame_get_join_token_method', payload='[]', objectId='8f2ebb1d-8921-486e-b029-188169913a93', versionIdentity=1}
--< ReplyObject [payload="464266c3-0b04-4ebb-999d-803a2c678af7", errorDescription=null, responseCode=201]

and actually the test case pass but for the wrong reason; the assert
is just that the joinToken is not null, but the returned join token is
not from the FutureGameServant, but because our invoker code is still
incomplete and happen to return a string! We need to extend the
invoker code and begin to switch on the `methodName` attribute.

Commit : 0925e82

I begin the proper Invoker structure:

    @Override
    public ReplyObject handleRequest(String objectId, String operationName, String payload) {
      ReplyObject reply = null;

      if (operationName.equals(MarshallingConstant.GAMELOBBY_CREATE_GAME_METHOD)) {

        FutureGame game = lobby.createGame("Pedersen", 0);
        String id = game.getId();

        reply = new ReplyObject(HttpServletResponse.SC_CREATED,
                gson.toJson(id));
      } else if (operationName.equals(MarshallingConstant.FUTUREGAME_GET_JOIN_TOKEN_METHOD)) {

      }
      return reply;
    }

which highlight a central issue: The server side have to store all
server side objects and be able to look them up based upon their
objectId. In web architectures it is often called session id and there
are several methods to keep this kind of server state. I will return
to this issue later; but for now we just keep an internal hashmap that
acts as an internal Name Service. This will suffice for our learning
purposes but does not work in real production servers.

So - two things - make a HashMap, and implement the servant upcall
code:

    @Override
    public ReplyObject handleRequest(String objectId, String operationName, String payload) {
      ReplyObject reply = null;

      if (operationName.equals(MarshallingConstant.GAMELOBBY_CREATE_GAME_METHOD)) {
        FutureGame game = lobby.createGame("Pedersen", 0);
        String id = game.getId();
        futureGameMap.put(id,game);

        reply = new ReplyObject(HttpServletResponse.SC_CREATED,
                gson.toJson(id));

      } else if (operationName.equals(MarshallingConstant.FUTUREGAME_GET_JOIN_TOKEN_METHOD)) {
        FutureGame game = futureGameMap.get(objectId);
        String token = game.getJoinToken();
        reply = new ReplyObject(HttpServletResponse.SC_OK, gson.toJson(token));

      }
      return reply;
    }

Test pass! But we must update our schema somewhat

> ## Transfering Server Created Objects
>
> To transfer a reference to an object created on the server side,
> you must follow this template
>
>  * Make the Servant object generate an unique ID, and provide an
>    accessor method, like `getId()`.
>  * Once a servant object is created, it must be stored in some
>    server accessible storage under the unique id as key.
>  * In the Invoker implementation use a String as marshalling format,
>    and just transfer the unique object id back to the client.
>  * On the client side, in the ClientProxy, create a ClientProxy
>    object that stores this unique id.
>  * When the server invoker gets a method call on some created
>    object, it must use the provided `objectId` to fetch the servant
>    object from the storage, and call the methods on it.

Commit: d635b17

### Iteration 4

Now the schema should be clear

  * Introduce one client method at a time in a test case
  * Implement the ClientProxy method code, defining a new marshalling
    method name. Object creation methods is
    handled by the schema mentioned above.
  * Add another path to the Invoker switch, dealing with the introduce
    marshalling method name. Look up the servant object using the
    provided objectId, and make the upcall on it, and create a proper
    reply object.
    

isAvailable: commit 8e86b70.

### Iteration 5

joinGame. Ok, this is the fun part because this is requesting the
server for the SAME object id as an earlier person created, so the
structure becomes more or less the same on the proxy side:

    @Override
    public FutureGame joinGame(String playerName, String joinToken) {
      String id =
              requestor.sendRequestAndAwaitReply("none",
                      MarshallingConstant.GAMELOBBY_JOIN_GAME_METHOD,
                      String.class, playerName, joinToken);
      FutureGame proxy = new FutureGameProxy(id, requestor);
      return proxy;
    }

Ups, as I begin implementing the invoker code I notice some fake it
code hanging around. I did not do this using pair programming :(. I
fix some hard coded values in the invoker and use the demarshalled
values instead.

I make test cases pass. Resulting invoker code 

    } else if (operationName.equals(MarshallingConstant.GAMELOBBY_JOIN_GAME_METHOD)) {
      String playerName = gson.fromJson(array.get(0), String.class);
      String joinToken = gson.fromJson(array.get(1), String.class);

      FutureGame game = lobby.joinGame(playerName,joinToken);

      // TODO: Handle non existing game
      String id = game.getId();

      reply = new ReplyObject(HttpServletResponse.SC_OK,
              gson.toJson(id));

Commit: 69ad7cc.

### Iteration 6

Continue to add tests to the test case

    // Now, as it is a two player game, both players see
    // that the game has become available.
    assertThat(player1Future.isAvailable(), is(true));
    assertThat(player2Future.isAvailable(), is(true));

which succeeds, as no new code required.

Adding the last test cases

    Game gameForPlayer1 = player1Future.getGame();
    assertThat(gameForPlayer1.getPlayerName(0), is("Pedersen"));
    assertThat(gameForPlayer1.getPlayerName(1), is("Findus"));

This is again a case of 'transfer object created by server', here the
Game instance, so we need to apply the template.

  * FutureGameProxy's getGame() method must call requestor and expect
    an String ID back. Create a GameProxy from it.
  * Code the new branch in the invoker switch. Here we run into the
    issue that Game has no 'getId()' method, so we break off into a
    child test to get that in place.
    
As the last time, we add TDD code to the SERVER side test cases.

Before: commit: 9fead3f

After: commit: 8df508e

The last piece of the puzzle is the Game.getPlayerName
method. Business as usual: Make the proxy code; make the invoker
code - or *not so fast*. We need to store the game under the game id
of course, so a bit of tweaking in the joinGame invoker code; 

Stopping now in 'red bar'; 'break' pattern.

Commit 8df508e. Getting the invoker code fully in place pending.

Time spent 4 hours.

Day 3
-----

### Iteration 6 continued

Ok, fixing the join invoker code:

    } else if (operationName.equals(MarshallingConstant.GAMELOBBY_JOIN_GAME_METHOD)) {
      String playerName = gson.fromJson(array.get(0), String.class);
      String joinToken = gson.fromJson(array.get(1), String.class);

      FutureGame futureGame = lobby.joinGame(playerName,joinToken);

      // TODO: Handle non existing game

      // Return the id of the future game joined so client has reference to it
      String futureGameId = futureGame.getId();

      // Joining a game also creates it so there is another server side
      // created game that will be referenced by future client calls,
      // thus this object must be stored server side under its id.
      String gameId = futureGame.getGame().getId();
      System.out.println("--> storing game id" + gameId);
      gameMap.put(gameId, futureGame.getGame());

      reply = new ReplyObject(HttpServletResponse.SC_OK,
              gson.toJson(futureGameId));

Time spent: less than half hour.

Now all happy path code is working.

Pending tasks

  * Remove fake code for join token generation.
  * TDD of the unhappy path (non existing games etc.)
  * Refactor the invoker code which is blobbing at the moment.
  * Make manual demo programs (real server, real clients)
  
Commit: d5f0a5d

### Iteration 7

Goal: Remove fake code join token generation.

Process: Make a test case in which 4 players make two games. Only make
the test in the server test package as it is solely a domain issue.

The test is easy - create two games and compare join tokens

    @Test
    public void shouldMakeUniqueJoinTokens() {
       GameLobby lobby = GameLobbyServant.getInstance();
       FutureGame player1Future = lobby.createGame("Pedersen", 1);
       String joinTokenGame1 = player1Future.getJoinToken();
       FutureGame player2Future = lobby.createGame("Hardy", 17);
       String joinTokenGame2 = player2Future.getJoinToken();
       assertThat(joinTokenGame1, is(not(joinTokenGame2)));
     }

Fails. Fixing is also easy, I use an class variable of type
AtomicInteger to increment a counter for every created game.

Removing debug info from the LocalMethodCallClientRequestHandler and
the happy path code is done.

Commit ea71879.

### Iteration 8

Goal: Handle error cases.

As the server side code has no error handling, I of course start
there. I adopt to throw exceptions as this will illustrate exception
handling propagation across network boundaries. 

I introduce an UnknownServantException using the test case

    @Test
    public void shouldFailIfNonExistingObjects() {
      FutureGame player2Future;
      // Try to join unknown game
      try {
        player2Future = lobby.joinGame("Findus", "unknown-token");
        fail("Lobby should throw an UnknownServantException due to the unknown join token.");
      } catch (UnknownServantException exc) {
        // Correct response
        assertThat(exc.getMessage(), containsString("unknown-token"));
        assertThat(exc.getMessage(), containsString("Findus"));
      }
    }
    
Working. Commit 7bf351b.

Turning to client side where there are more error issues.

So I copy the test code into the client test case context.

Actually, the test case on client side passes but for the wrong
reason - it is because we use the LocalMethodCallClientRequestHandler
which means the client and server objects are in the same java context
which is not similar to a real distributed setting. We need to encode
a catch in the invoker as it is its role to handle server side exceptions.

So I enter a try catch in the invoker's handleRequest method


    } catch (UnknownServantException e) {
      reply =
              new ReplyObject(
                      HttpServletResponse.SC_NOT_FOUND,
                      e.getMessage());
    }

using the HTTP Status code for 'resource not found' as indication to
the client that something was requested but not found.

Now the test case fails, because the StandardJSONRequster in the
FRDS.Broker library code tests the status code and throws an
IPCException for all status codes in the above 200 range.

We have several options for where to catch this exception, but
I find doing so in the proxy makes sense: it is the joinGame method
that will fail if a wrong join token is provided.

    @Override
    public FutureGame joinGame(String playerName, String joinToken) {
      FutureGame proxy = null;
      try {
        String id =
                requestor.sendRequestAndAwaitReply("none",
                        MarshallingConstant.GAMELOBBY_JOIN_GAME_METHOD,
                        String.class, playerName, joinToken);
        proxy = new FutureGameProxy(id, requestor);
      } catch (IPCException exc) {
        if (exc.getStatusCode() != HttpServletResponse.SC_NOT_FOUND) {
          throw exc;
        }
        throw new UnknownServantException(exc.getMessage());
      }
      return proxy;
    }

Done, and tests pass. Commit 4c92398.

### Iteration 8.b

More exceptional cases - only one left - retrieving a game that does
not exist, in 'getPlayerName'. This is actually difficult to test - as
the protocol does not allow getting a game proxy to call on; but
we can induce the error by creating the GameProxy object directly.

    // Test for retrieval on non-existing game on server side
    Game proxy = new GameProxy("unknown-id", requestor);
    String firstPlayerName = proxy.getPlayerName(0);

This trigger TDD of the relevant invoker code:

        Game game = gameMap.get(objectId);
        if (game == null) {
          throw new UnknownServantException(
                  "Game with object id: " + objectId + " does not exist.");
        }

        int index = gson.fromJson(array.get(0), Integer.class);
        String name = game.getPlayerName(index);
        reply = new ReplyObject(HttpServletResponse.SC_OK, name);

I do not catch the IPC exception in the GameProxy; it is a breach of
the protocol, and thus the client should never be in this
situation. Still we have made the server more robust by handling the
error over there. Updating the test case. Done

Commit e6141a7.


### Iteration 9

Goal: Make Demo programs.

Made gradle tasks and the main programs. The case makes for rather
stupid clients so actually only a single scenario is possible.

  * gradle :demo2:lobbyServer

And then

    csdev@m31:~/proj/broker$ gradle :demo2:lobbyClient -q -Pplayer=Henrik -Pop=create
    LobbyClient: Asked to do operation create for player Henrik
     Future created, the join token is game-3
    csdev@m31:~/proj/broker$ gradle :demo2:lobbyClient -q -Pname=Baerbak -Ptoken=game-3 -Pop=join
    LobbyClient: Asked to do operation join for player Pedersen
     Future joined, available is true
     The Game id is ab200d3d-cf51-473e-82a8-cfc35238d81c
     The Game's 1st player is Henrik
     The Game's 2nd player is Pedersen

Done. Commit 380131f

Note that a lot of details are still pending. You may join the same
game several times which overwrites the second player. Logging is
missing. But, has little relevance for the Broker of multi-object
learning objective.

### Iteration 10

We should anti-blob the Invoker. At the moment all methods are handled
by one large switch in the invoker, and the caching of servant objects
is also one big ball of mud.

One solution to it, mentioned as demultiplexing or dispatchin in
Posa-4, is to associate specific 'upcall responsibles' for each type
on the server side: thereby have type specific invokers. We have three
types: GameLobby, FutureGame, and Game, so that would lead to three
sub-invokers.

I have actually prepared somewhat for this. The naming of the method

    public class MarshallingConstant {

      // Method ids for marshalling
      public static final String GAMELOBBY_CREATE_GAME_METHOD = "gamelobby_create_game_method";
      public static final String GAMELOBBY_JOIN_GAME_METHOD = "gamelobby_join_game_method";;

      public static final String FUTUREGAME_GET_JOIN_TOKEN_METHOD = "futuregame_get_join_token_method";
      public static final String FUTUREGAME_IS_AVAILABLE_METHOD = "futuregame_is_available_method";
      public static final String FUTUREGAME_GET_GAME_METHOD = "futuregame_get_game_method";

      public static final String GAME_GET_PLAYER_NAME = "game_get_player_name_method";
    }

follows a pattern which prefix the method name with the type:
"gamelobby_", "futuregame_", etc.

So the idea is to find the first substring in the operationName given
to the handleRequest method; and then lookup an appropriate
subinvoker and delegate to it.

I can use the existing client test cases for this refactoring.

First shot

    Invoker gameLobbyInvoker = new GameLobbyInvoker(lobby);
    invokerMap.put("gamelobby", gameLobbyInvoker);

And I made some way on this path - to find that it is Do Over! The
problem is the HashMaps of created objects; they are used across
invokers! That is the lobby invoker must access the FutureGame map; as
must the FutureGame invoker. 

Thus it is better to retract to an earlier commit and then FIRST
introduce an abstraction that encapsulate the maps, make that work,
and THEN introduce invokers. I name this abstraction 'NameService' as
that is what it is: a name service that can stores and supports lookup
of servant objects based upon object id. It is great abstraction
because it also loosen the binding between the invokers and the actual
object persistence implementation - I would use a real cache service
like MemCached instead, or a SQL database, or ...

About 3 hours

Day 4
-----

The Do Over is made by

  * git checkout <last-good-state>
  * git branch -b development-do-over
  
Now introduce the Storage in this branch, I

  * remove the 'futureGameMap', introduce interface NameService, and
    change all refs to futureGameMap to the equivalent
    
        nameService = new InMemoryNameService();

        nameService.put(id, futureGame);

    etc.
   * Easy to make to work.
   
   * Do same procedure with the gameMap.
   
The resulting NameService becomes

    public interface NameService {
      void putFutureGame(String objectId, FutureGame futureGame);
      FutureGame getFutureGame(String objectId);

      void putGame(String objectId, Game game);
      Game getGame(String objectId);
    }

All tests pass.

Commit the stuff: 6af87ec

Next, I merge the real development branch into this branch

    git merge development
    
A bit of conflicts to be handled. Done.

Commit 86c391c introduces the invoker for the lobby.

Commit 4781332 introduces the future game invoker.

Commit c6d48f5 introduces the game invoker.

However, quite some duplicated code around. So a bit of clean up to
get clean code that works; but first I force this do over branch back
into development.


    git push origin development-do-over:development --force
    git checkout development
    git pull
    

Well, the only duplicated code is the demarshaling of payload. Is it
worth a separate class or an inheritance hierarchy? I decide that it
is not.

Done.

The final invoker

    @Override
    public ReplyObject handleRequest(String objectId, String operationName, String payload) {
      ReplyObject reply = null;

      // Identify the Invoker to use
      String type = operationName.substring(0, operationName.indexOf('_'));
      Invoker subInvoker = invokerMap.get(type);

      // And do the upcall
      try {
        reply = subInvoker.handleRequest(objectId, operationName, payload);

      } catch (UnknownServantException e) {
        reply =
                new ReplyObject(
                        HttpServletResponse.SC_NOT_FOUND,
                        e.getMessage());
      }

      return reply;
    }

Ahh - some magic constants floating around, better clean them up.

    public class MarshallingConstant {

      // Type prefixes
      public static final String GAME_LOBBY_PREFIX = "gamelobby";
      private static final String FUTUREGAME_PREFIX = "futuregame";
      private static final String GAME_PREFIX = "game";

      // Method ids for marshalling
      public static final String GAMELOBBY_CREATE_GAME_METHOD = GAME_LOBBY_PREFIX + "_create_game_method";
      public static final String GAMELOBBY_JOIN_GAME_METHOD = GAME_LOBBY_PREFIX + "_join_game_method";;

      public static final String FUTUREGAME_GET_JOIN_TOKEN_METHOD = FUTUREGAME_PREFIX + "_get_join_token_method";
      public static final String FUTUREGAME_IS_AVAILABLE_METHOD = FUTUREGAME_PREFIX + "_is_available_method";
      public static final String FUTUREGAME_GET_GAME_METHOD = FUTUREGAME_PREFIX + "_get_game_method";

      public static final String GAME_GET_PLAYER_NAME = GAME_PREFIX + "_get_player_name_method";
    }

And the invoker map population code becomes:

    Invoker gameLobbyInvoker = new GameLobbyInvoker(lobby, nameService, gson);
    invokerMap.put(MarshallingConstant.GAME_LOBBY_PREFIX, gameLobbyInvoker);
    Invoker futureGameInvoker = new FutureGameInvoker(nameService, gson);
    invokerMap.put(MarshallingConstant.FUTUREGAME_PREFIX, futureGameInvoker);
    Invoker gameInvoker = new GameInvoker(nameService, gson);
    invokerMap.put(MarshallingConstant.GAME_PREFIX, gameInvoker);


Pending? More JavaDoc.

Total time: one hour.

Day 5:
------

Just cleaning up code: moving classes into suitable packages; adding
javadoc; fixing a few wrong 'extends Servant' here and there.

Total: one hour.


Later iteration:
----------------

The code base has been refactored to use the NameService interface
name, originally it was named ObjectStorage in the code base and in
this diary. Thus the diary here is not completely true to the
development process, but I considered it better to adjust the diary to
the current state of the code.

Summer 2020 update:
----
During summer 2020 I worked on edition 2 of the FRDS book which 
included improving the *separation of concerns* betwen the
marshalling and IPC layer of the code base. Thus the
invoker code has been updated to reflect that *all*
marshalling is now made in the **Invoker**, which was
not the case in FRDS.Broker Library prior to release 2.0.
