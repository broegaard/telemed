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
import gamelobby.common.MarshallingConstant;
import gamelobby.domain.FutureGame;
import gamelobby.domain.GameLobby;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * At 25 Apr 2018
 *
 * @author Henrik Baerbak Christensen, CS @ AU
 */
public class GameLobbyJSONInvoker implements Invoker {
  private final GameLobby lobby;
  private Gson gson;
  private Map<String, FutureGame> futureGameMap;

  public GameLobbyJSONInvoker(GameLobby lobby) {
    this.lobby = lobby;
    gson = new Gson();

    futureGameMap = new HashMap<>();
  }

  @Override
  public ReplyObject handleRequest(String objectId, String operationName, String payload) {
    ReplyObject reply = null;

    if (operationName.equals(MarshallingConstant.GAMELOBBY_CREATE_GAME_METHOD)) {
      FutureGame game = lobby.createGame("Pedersen", 0);
      String id = game.getId();
      futureGameMap.put(id,game);

      reply = new ReplyObject(HttpServletResponse.SC_CREATED,
              gson.toJson(id));

    } else if (operationName.equals(MarshallingConstant.FUTUREGAME_GET_JOIN_TOKEN_METHOD)) {
      FutureGame game = futureGameMap.get(objectId);
      String token = game.getJoinToken();
      reply = new ReplyObject(HttpServletResponse.SC_OK, gson.toJson(token));

    } else if (operationName.equals(MarshallingConstant.FUTUREGAME_IS_AVAILABLE_METHOD)) {
      FutureGame game = futureGameMap.get(objectId);
      boolean isAvailable = game.isAvailable();
      reply = new ReplyObject(HttpServletResponse.SC_OK, gson.toJson(isAvailable));

    }
    return reply;
  }
}
