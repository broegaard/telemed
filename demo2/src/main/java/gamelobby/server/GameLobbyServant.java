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
import gamelobby.domain.GameLobby;

/**
 * At 24 Apr 2018
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

  @Override
  public FutureGame createGame(int playerLevel) {
    return new StandardFutureGame(playerLevel);
  }

  @Override
  public FutureGame joinGame(String joinToken) {
    return null;
  }
}
