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

import frds.broker.ClientProxy;
import frds.broker.Requestor;
import gamelobby.domain.FutureGame;
import gamelobby.domain.GameLobby;

/**
 * At 25 Apr 2018
 *
 * @author Henrik Baerbak Christensen, CS @ AU
 */
public class GameLobbyProxy implements GameLobby, ClientProxy {
  public GameLobbyProxy(Requestor requestor) {
  }

  @Override
  public FutureGame createGame(String playerName, int playerLevel) {
    return null;
  }

  @Override
  public FutureGame joinGame(String playerName, String joinToken) {
    return null;
  }
}
