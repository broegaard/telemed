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

import frds.broker.Servant;
import gamelobby.domain.FutureGame;
import gamelobby.domain.Game;
import gamelobby.domain.GameLobby;

import java.util.HashMap;
import java.util.Map;

/** Servant implementation of the lobby.
 *
 * @author Henrik Baerbak Christensen, CS @ AU
 */
public class GameLobbyServant implements Servant, GameLobby {

  private static GameLobby singleton;

  public static GameLobby getInstance() {
    if (singleton == null)
      singleton = new GameLobbyServant();
    return singleton;
  }

  /** The collection of games waiting in the lobby to
   * be joined by players.
   */
  private Map<String, FutureGameServant> gamesInLobby;

  public GameLobbyServant() {
    gamesInLobby = new HashMap<>();
  }

  @Override
  public FutureGame createGame(String playerName, int playerLevel) {
    FutureGameServant future = new FutureGameServant(playerName, playerLevel);
    gamesInLobby.put(future.getJoinToken(), future);
    return future;
  }

  @Override
  public FutureGame joinGame(String playerName, String joinToken) {
    FutureGameServant future = gamesInLobby.get(joinToken);
    // TODO: Handle lookup of non-existing game

    // Transform the future so it represents a valid
    // game
    Game theActualGame = new GameServant(future.getFirstPlayerName(), playerName);
    future.setGame(theActualGame);

    return future;
  }
}
