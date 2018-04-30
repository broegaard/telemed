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
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import frds.broker.Invoker;
import frds.broker.ReplyObject;
import gamelobby.common.MarshallingConstant;
import gamelobby.domain.FutureGame;
import gamelobby.domain.Game;
import gamelobby.domain.GameLobby;
import gamelobby.domain.UnknownServantException;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/** The invoker role for the game lobby system.
 *
 * @author Henrik Baerbak Christensen, CS @ AU
 */
public class GameLobbyJSONInvoker implements Invoker {
  private final GameLobby lobby;
  private final ObjectStorage objectStorage;
  private final Map<String, Invoker> invokerMap;
  private Gson gson;

  public GameLobbyJSONInvoker(GameLobby lobby) {
    this.lobby = lobby;
    gson = new Gson();

    objectStorage = new InMemoryObjectStorage();
    invokerMap = new HashMap<>();

    // Create an invoker for each handled type
    // and put them in a map, binding them to the
    // operationName prefixes
    Invoker gameLobbyInvoker = new GameLobbyInvoker(lobby, objectStorage, gson);
    invokerMap.put("gamelobby", gameLobbyInvoker);
  }

  @Override
  public ReplyObject handleRequest(String objectId, String operationName, String payload) {
    ReplyObject reply = null;

    // Demarshall parameters into a JsonArray
    JsonParser parser = new JsonParser();
    JsonArray array =
            parser.parse(payload).getAsJsonArray();

    // Identify the Dispatcher to use
    String type = operationName.substring(0, operationName.indexOf('_'));
    System.out.println(" ---> " + type);
    Invoker subInvoker = invokerMap.get(type);

    try {

      if (type.equals("gamelobby")) {
        reply = subInvoker.handleRequest(objectId,operationName,payload);

      } else if (operationName.equals(MarshallingConstant.FUTUREGAME_GET_JOIN_TOKEN_METHOD)) {
        FutureGame futureGame = objectStorage.getFutureGame(objectId);
        String token = futureGame.getJoinToken();
        reply = new ReplyObject(HttpServletResponse.SC_OK, gson.toJson(token));

      } else if (operationName.equals(MarshallingConstant.FUTUREGAME_IS_AVAILABLE_METHOD)) {
        FutureGame futureGame = objectStorage.getFutureGame(objectId);
        boolean isAvailable = futureGame.isAvailable();
        reply = new ReplyObject(HttpServletResponse.SC_OK, gson.toJson(isAvailable));

      } else if (operationName.equals(MarshallingConstant.FUTUREGAME_GET_GAME_METHOD)) {
        FutureGame futureGame = objectStorage.getFutureGame(objectId);
        Game game = futureGame.getGame();
        String id = game.getId();
        reply = new ReplyObject(HttpServletResponse.SC_OK, gson.toJson(id));

      } else if (operationName.equals(MarshallingConstant.GAME_GET_PLAYER_NAME)) {
        Game game = objectStorage.getGame(objectId);
        if (game == null) {
          throw new UnknownServantException(
                  "Game with object id: " + objectId + " does not exist.");
        }

        int index = gson.fromJson(array.get(0), Integer.class);
        String name = game.getPlayerName(index);
        reply = new ReplyObject(HttpServletResponse.SC_OK, name);

      }

    } catch (UnknownServantException e) {
      reply =
              new ReplyObject(
                      HttpServletResponse.SC_NOT_FOUND,
                      e.getMessage());
    }

    return reply;
  }
}
