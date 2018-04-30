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
import gamelobby.domain.Game;
import gamelobby.domain.UnknownServantException;

import javax.servlet.http.HttpServletResponse;

/**
 * At 30 Apr 2018
 *
 * @author Henrik Baerbak Christensen, CS @ AU
 */
public class GameInvoker implements Invoker {
  private final ObjectStorage objectStorage;
  private final Gson gson;

  public GameInvoker(ObjectStorage objectStorage, Gson gson) {
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
    if (operationName.equals(MarshallingConstant.GAME_GET_PLAYER_NAME)) {
      Game game = objectStorage.getGame(objectId);
      if (game == null) {
        throw new UnknownServantException(
                "Game with object id: " + objectId + " does not exist.");
      }

      int index = gson.fromJson(array.get(0), Integer.class);
      String name = game.getPlayerName(index);
      reply = new ReplyObject(HttpServletResponse.SC_OK, name);

    }
    return reply;
  }
}
