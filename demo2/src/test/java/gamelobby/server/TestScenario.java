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

package gamelobby.server;

import gamelobby.domain.FutureGame;
import gamelobby.domain.Game;
import gamelobby.domain.GameLobby;

import gamelobby.domain.UnknownServantException;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletResponse;

import static junit.framework.TestCase.fail;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.nullValue;

/**
 * At 24 Apr 2018
 *
 * @author Henrik Baerbak Christensen, CS @ AU
 */
public class TestScenario {

  private GameLobby lobby;

  @Before
  public void setup() {
    lobby = GameLobbyServant.getInstance();
  }

  @Test
  public void shouldHandleStory1() {
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

  @Test
  public void shouldCreateUniqueObjectIdForFutureGame() {
    FutureGame player1Future = lobby.createGame("Pedersen", 0);
    String uniqueObjectId1 = player1Future.getId();
    FutureGame player2Future = lobby.createGame("Hansen", 0);
    String uniqueObjectId2 = player2Future.getId();
    assertThat(uniqueObjectId1, is(not(uniqueObjectId2)));
  }

   @Test
  public void shouldCreateIDForGame() {
    Game game = new GameServant("Hans", "Peter");
    assertThat(game.getId(), is(not(nullValue())));
   }

  @Test
  public void shouldMakeUniqueJoinTokens() {
     FutureGame player1Future = lobby.createGame("Pedersen", 1);
     String joinTokenGame1 = player1Future.getJoinToken();
     FutureGame player2Future = lobby.createGame("Hardy", 17);
     String joinTokenGame2 = player2Future.getJoinToken();
     assertThat(joinTokenGame1, is(not(joinTokenGame2)));
   }

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

    // What happens in case you retrieve a game before it is made? Just a null
    FutureGame player1Future = lobby.createGame("Pedersen", 1);
    Game game = player1Future.getGame();
    assertThat(game, is(nullValue()));

  }
}
