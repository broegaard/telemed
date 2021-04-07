/*
 * Copyright (C) 2018-2021. Henrik Bærbak Christensen, Aarhus University.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package gamelobby.client;

import frds.broker.ClientProxy;
import frds.broker.IPCException;
import frds.broker.Requestor;
import gamelobby.common.MarshallingConstant;
import gamelobby.domain.FutureGame;
import gamelobby.domain.GameLobby;
import gamelobby.domain.UnknownServantException;

import javax.servlet.http.HttpServletResponse;

/**
 * The client proxy for the game lobby.
 *
 * @author Henrik Baerbak Christensen, CS @ AU
 */
public class GameLobbyProxy implements GameLobby, ClientProxy {
  public static final String GAMELOBBY_OBJECTID = "gamelobby-singleton";
  private final Requestor requestor;

  public GameLobbyProxy(Requestor requestor) {
    this.requestor = requestor;
  }

  @Override
  public FutureGame createGame(String playerName, int playerLevel) {
    String id =
      requestor.sendRequestAndAwaitReply(GAMELOBBY_OBJECTID,
              MarshallingConstant.GAMELOBBY_CREATE_GAME_METHOD,
              String.class, playerName, playerLevel);
    FutureGame proxy = new FutureGameProxy(id, requestor);
    return proxy;
  }

  @Override
  public FutureGame joinGame(String playerName, String joinToken) {
    FutureGame proxy = null;
    try {
      String id =
              requestor.sendRequestAndAwaitReply(GAMELOBBY_OBJECTID,
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
}
