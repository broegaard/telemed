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

package gamelobby.marshall;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import frds.broker.Invoker;
import frds.broker.ReplyObject;
import frds.broker.RequestObject;
import gamelobby.common.MarshallingConstant;
import gamelobby.domain.FutureGame;
import gamelobby.domain.Game;
import gamelobby.service.NameService;

import javax.servlet.http.HttpServletResponse;

/** The sub invoker for future game instances.
 *
 * @author Henrik Baerbak Christensen, CS @ AU
 */
public class FutureGameInvoker implements Invoker {
  private final NameService nameService;
  private final Gson gson;

  public FutureGameInvoker(NameService nameService, Gson gson) {
    this.nameService = nameService;
    this.gson = gson;
  }

  @Override
  public String handleRequest(String request) {
    // Do demarshalling
    RequestObject requestObject = gson.fromJson(request, RequestObject.class);
    String objectId = requestObject.getObjectId();
    String operationName = requestObject.getOperationName();
    String payload = requestObject.getPayload();
    JsonArray array = JsonParser.parseString(payload).getAsJsonArray();

    ReplyObject reply = null;

    if (operationName.equals(MarshallingConstant.FUTUREGAME_GET_JOIN_TOKEN_METHOD)) {
      FutureGame futureGame = nameService.getFutureGame(objectId);
      String token = futureGame.getJoinToken();
      reply = new ReplyObject(HttpServletResponse.SC_OK, gson.toJson(token));

    } else if (operationName.equals(MarshallingConstant.FUTUREGAME_IS_AVAILABLE_METHOD)) {
      FutureGame futureGame = nameService.getFutureGame(objectId);
      boolean isAvailable = futureGame.isAvailable();
      reply = new ReplyObject(HttpServletResponse.SC_OK, gson.toJson(isAvailable));

    } else if (operationName.equals(MarshallingConstant.FUTUREGAME_GET_GAME_METHOD)) {
      FutureGame futureGame = nameService.getFutureGame(objectId);
      Game game = futureGame.getGame();
      String id = game.getId();
      reply = new ReplyObject(HttpServletResponse.SC_OK, gson.toJson(id));
    }

    return gson.toJson(reply);
  }
}
