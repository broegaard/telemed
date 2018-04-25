Development Diary
=================

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
        Game gameForPlayer2= player1Future.getGame();
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


