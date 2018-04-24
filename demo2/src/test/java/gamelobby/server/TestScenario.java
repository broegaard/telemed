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
import gamelobby.domain.GameLobby;

import org.junit.Test;

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

  @Test
  public void shouldHandleStory1() {
    // Get the game lobby singleton
    GameLobby lobby = GameLobbyServant.getInstance();
    assertThat(lobby, is(not(nullValue())));

    // Ask lobby to create a game for a beginner (playerLevel = 0).
    // A Future is returned, that is a placeholder for a future
    // game that will eventually be able to return a live game.

    FutureGame player1Future = lobby.createGame(0);

    // Get the token for my fellow players to enter when wanting
    // to join my game
    String joinToken = player1Future.getJoinToken();
    assertThat(joinToken, is(not(nullValue())));

    // As a second player has not yet joined, the game
    // is not yet created
    assertThat(player1Future.isAvailable(), is(false));

    // Second player - wants to join the game using the token
    FutureGame player2Future = lobby.joinGame(joinToken);

    // Now, as it is a two player game, both players see
    // that the game has become available.

    assertThat(player1Future.isAvailable(), is(true));
    assertThat(player2Future.isAvailable(), is(true));
  }
}
