/*
 * Copyright (C) 2018 Henrik Bærbak Christensen, baerbak.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package gamelobby.client;

import frds.broker.IPCException;
import gamelobby.domain.FutureGame;
import gamelobby.domain.Game;
import gamelobby.domain.UnknownServantException;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.fail;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.nullValue;

import frds.broker.ClientRequestHandler;
import frds.broker.Invoker;
import frds.broker.Requestor;
import frds.broker.marshall.json.StandardJSONRequestor;

import gamelobby.domain.GameLobby;
import gamelobby.doubles.LocalMethodCallClientRequestHandler;
import gamelobby.marshall.GameLobbyRootInvoker;
import gamelobby.server.GameLobbyServant;

import javax.servlet.http.HttpServletResponse;


/** TDD from the client side of the proxies and the invoker.
 * All done using the fake-object CRH and SRH pair.
 *
 * @author Henrik Baerbak Christensen, CS @ AU
 */
public class TestClientScenario {
  private GameLobby lobbyProxy;
  private Requestor requestor;

  @Before
  public void setup() {
    // Create the server side facade to the lobby
    GameLobby lobby = GameLobbyServant.getInstance();

    // Server side broker implementation of the Invoker
    Invoker invoker = new GameLobbyRootInvoker(lobby);

    // Create client side broker implementations, using the local
    // method client request handler to avoid any real IPC layer.
    ClientRequestHandler clientRequestHandler =
            new LocalMethodCallClientRequestHandler(invoker);
    requestor =
            new StandardJSONRequestor(clientRequestHandler);

    // Finally, create the client proxy for the lobby
    lobbyProxy = new GameLobbyProxy(requestor);
  }


  @Test
  public void shouldHandleStory1OnClient() {
    FutureGame player1Future = lobbyProxy.createGame("Pedersen", 0);
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
    FutureGame player2Future = lobbyProxy.joinGame("Findus", joinToken);
    assertThat(player2Future, is(not(nullValue())));

    // Assure both talk to same game
    assertThat(player1Future.getId(), is(player2Future.getId()));

    // Now, as it is a two player game, both players see
    // that the game has become available.
    assertThat(player1Future.isAvailable(), is(true));
    assertThat(player2Future.isAvailable(), is(true));

    // And they can make state changes and read game state to the game
    Game gameForPlayer1 = player1Future.getGame();
    assertThat(gameForPlayer1.getPlayerName(0), is("Pedersen"));
    assertThat(gameForPlayer1.getPlayerName(1), is("Findus"));
    assertThat(gameForPlayer1.getPlayerInTurn(), is("Pedersen"));

    // Our second player sees the same game state
    Game gameForPlayer2 = player2Future.getGame();
    assertThat(gameForPlayer2.getPlayerName(0), is("Pedersen"));
    assertThat(gameForPlayer2.getPlayerName(1), is("Findus"));
    assertThat(gameForPlayer2.getPlayerInTurn(), is("Pedersen"));

    // And the ID's of the games are the same
    assertThat(gameForPlayer1.getId(), is(gameForPlayer2.getId()));

    // Make a state change, player one makes a move
    gameForPlayer1.move();

    // And verify turn is now the opposite player
    assertThat(gameForPlayer1.getPlayerInTurn(), is("Findus"));
    assertThat(gameForPlayer2.getPlayerInTurn(), is("Findus"));

    // Finally, we may need to talk with our game instance without
    // having to get the instance from the future game; like
    // Findus accidentially closes the program and then wants
    // to rejoin after restarting.
    String idOfGameGivenToMeByMyOpponent = gameForPlayer1.getId();
    Game theSameGame = new GameProxy(idOfGameGivenToMeByMyOpponent,
        requestor);
    assertThat(theSameGame.getPlayerInTurn(), is("Findus"));
  }

  @Test
  public void shouldFailIfNonExistingObjects() {
     FutureGame player2Future;
    // Try to join unknown game
    try {
      player2Future = lobbyProxy.joinGame("Findus", "unknown-token");
      fail("Lobby should throw an UnknownServantException due to the unknown join token.");
    } catch (UnknownServantException exc) {
      // Correct response
      assertThat(exc.getMessage(), containsString("unknown-token"));
      assertThat(exc.getMessage(), containsString("Findus"));
    }

    // Test for retrieval on non-existing game on server side
    try {
      Game proxy = new GameProxy("unknown-id", requestor);
      String firstPlayerName = proxy.getPlayerName(0);
      fail("Server should reply with an IPCexception");
    } catch (IPCException e) {
      assertThat(e.getStatusCode(), is(HttpServletResponse.SC_NOT_FOUND));
    }
  }
}