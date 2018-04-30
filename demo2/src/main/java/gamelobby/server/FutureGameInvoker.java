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

import javax.servlet.http.HttpServletResponse;

/**
 * At 30 Apr 2018
 *
 * @author Henrik Baerbak Christensen, CS @ AU
 */
public class FutureGameInvoker implements Invoker {
  private final ObjectStorage objectStorage;
  private final Gson gson;

  public FutureGameInvoker(ObjectStorage objectStorage, Gson gson) {
    this.objectStorage = objectStorage;
    this.gson = gson;
  }

  @Override
  public ReplyObject handleRequest(String objectId, String operationName, String payload) {
    ReplyObject reply = null;

    // Demarshall parameters into a JsonArray
    JsonParser parser = new JsonParser();
    JsonArray array =
            parser.parse(payload).getAsJsonArray();

    if (operationName.equals(MarshallingConstant.FUTUREGAME_GET_JOIN_TOKEN_METHOD)) {
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
    }

    return reply;
  }
}
