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

import com.google.gson.Gson;
import frds.broker.Invoker;
import frds.broker.ReplyObject;
import gamelobby.domain.FutureGame;
import gamelobby.domain.GameLobby;

import javax.servlet.http.HttpServletResponse;

/**
 * At 25 Apr 2018
 *
 * @author Henrik Baerbak Christensen, CS @ AU
 */
public class GameLobbyJSONInvoker implements Invoker {
  private final GameLobby lobby;
  private Gson gson;

  public GameLobbyJSONInvoker(GameLobby lobby) {
    this.lobby = lobby;
    gson = new Gson();
  }

  @Override
  public ReplyObject handleRequest(String objectId, String operationName, String payload) {
    ReplyObject reply = null;

    FutureGame game = lobby.createGame("Pedersen", 0);
    // Have to cast to real type to convince GSon on marshalling it
    FutureGameServant castGame = (FutureGameServant) game;

    reply = new ReplyObject(HttpServletResponse.SC_CREATED,
            gson.toJson(castGame));

    return reply;
  }
}
