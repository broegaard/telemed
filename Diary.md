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

Iteration 1: So, the first step entails creating GameLobbyJSONInvoker
and GameLobbyProxy as just temporary test stubs. commit: e62519c.

Iteration 2: 

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
return record types, and make the requestor convert it!

