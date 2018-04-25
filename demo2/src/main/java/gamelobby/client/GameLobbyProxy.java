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
import gamelobby.common.MarshallingConstant;
import gamelobby.domain.FutureGame;
import gamelobby.domain.GameLobby;
import gamelobby.server.FutureGameServant;

/**
 * At 25 Apr 2018
 *
 * @author Henrik Baerbak Christensen, CS @ AU
 */
public class GameLobbyProxy implements GameLobby, ClientProxy {
  private final Requestor requestor;

  public GameLobbyProxy(Requestor requestor) {
    this.requestor = requestor;
  }

  @Override
  public FutureGame createGame(String playerName, int playerLevel) {
    String id =
      requestor.sendRequestAndAwaitReply("none",
              MarshallingConstant.GAMELOBBY_CREATE_GAME_METHOD,
              String.class, playerName, playerLevel);
    FutureGame proxy = new FutureGameProxy(id);
    return proxy;
  }

  @Override
  public FutureGame joinGame(String playerName, String joinToken) {
    return null;
  }
}
