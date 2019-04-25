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

package gamelobby.marshall;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import frds.broker.Invoker;
import frds.broker.ReplyObject;
import gamelobby.common.MarshallingConstant;
import gamelobby.domain.FutureGame;
import gamelobby.domain.GameLobby;
import gamelobby.service.NameService;

import javax.servlet.http.HttpServletResponse;

/** The sub invoker responsible for handling GameLobby
 * method calls.
 *
 * @author Henrik Baerbak Christensen, CS @ AU
 */
public class GameLobbyInvoker implements Invoker {
  private final GameLobby lobby;
  private final Gson gson;
  private final NameService nameService;

  public GameLobbyInvoker(GameLobby lobby, NameService storage, Gson gson) {
    this.lobby = lobby;
    this.nameService = storage;
    this.gson = gson;
  }

  @Override
  public ReplyObject handleRequest(String objectId, String operationName, String payload) {
    ReplyObject reply = null;

    // Demarshall parameters into a JsonArray
    JsonParser parser = new JsonParser();
    JsonArray array =
            parser.parse(payload).getAsJsonArray();

    if (operationName.equals(MarshallingConstant.GAMELOBBY_CREATE_GAME_METHOD)) {
      String playerName = gson.fromJson(array.get(0), String.class);
      int level = gson.fromJson(array.get(1), Integer.class);
      FutureGame futureGame = lobby.createGame(playerName, level);
      String id = futureGame.getId();
      nameService.putFutureGame(id, futureGame);

      reply = new ReplyObject(HttpServletResponse.SC_CREATED,
              gson.toJson(id));

    } else if (operationName.equals(MarshallingConstant.GAMELOBBY_JOIN_GAME_METHOD)) {
      String playerName = gson.fromJson(array.get(0), String.class);
      String joinToken = gson.fromJson(array.get(1), String.class);

      FutureGame futureGame = lobby.joinGame(playerName, joinToken);
      // Note: if the joinToken is unknown, lobby will throw exception
      // which is caught below and handled.

      // Return the id of the future game joined so client has reference to it
      String futureGameId = futureGame.getId();

      // Joining a game also creates it so there is another server side
      // created game that will be referenced by future client calls,
      // thus this object must be stored server side under its id.
      String gameId = futureGame.getGame().getId();
      nameService.putGame(gameId, futureGame.getGame());

      reply = new ReplyObject(HttpServletResponse.SC_OK,
              gson.toJson(futureGameId));
    }
    return reply;
  }
}
