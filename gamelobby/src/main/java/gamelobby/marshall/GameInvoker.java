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
import gamelobby.domain.Game;
import gamelobby.domain.UnknownServantException;
import gamelobby.service.NameService;

import javax.servlet.http.HttpServletResponse;

/**
 * The sub invoker for game instances.
 *
 * @author Henrik Baerbak Christensen, CS @ AU
 */
public class GameInvoker implements Invoker {
  private final NameService nameService;
  private final Gson gson;

  public GameInvoker(NameService nameService, Gson gson) {
    this.nameService = nameService;
    this.gson = gson;
  }

  @Override
  public ReplyObject handleRequestDEATHROW(String objectId, String operationName, String payload) {
    ReplyObject reply = null;
    // Demarshall parameters into a JsonArray
    JsonParser parser = new JsonParser();
    JsonArray array =
            parser.parse(payload).getAsJsonArray();

    Game game = getGameOrThrowUnknownException(objectId);

    if (operationName.equals(MarshallingConstant.GAME_GET_PLAYER_NAME)) {
      int index = gson.fromJson(array.get(0), Integer.class);
      String name = game.getPlayerName(index);
      reply = new ReplyObject(HttpServletResponse.SC_OK, name);

    } else if (operationName.equals(MarshallingConstant.GAME_GET_PLAYER_IN_TURN)) {
      String name = game.getPlayerInTurn();
      reply = new ReplyObject(HttpServletResponse.SC_OK, name);

    } else if (operationName.equals(MarshallingConstant.GAME_MOVE)) {
      game.move();
      reply = new ReplyObject(HttpServletResponse.SC_OK, null);
    }
    return reply;
  }

  @Override
  public String handleRequest(String request) {
    // TODO: MAR
    return null;
  }

  private Game getGameOrThrowUnknownException(String objectId) {
    Game game = nameService.getGame(objectId);
    if (game == null) {
      throw new UnknownServantException(
          "Game with object id: " + objectId + " does not exist.");
    }
    return game;
  }
}
