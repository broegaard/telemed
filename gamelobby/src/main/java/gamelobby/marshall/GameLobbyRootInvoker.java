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
import frds.broker.Invoker;
import frds.broker.ReplyObject;
import frds.broker.RequestObject;
import gamelobby.common.MarshallingConstant;
import gamelobby.domain.GameLobby;
import gamelobby.domain.UnknownServantException;
import gamelobby.service.InMemoryNameService;
import gamelobby.service.NameService;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/** The main/root invoker role for the game lobby system.
 * <p>
 *   This implementation uses sub-invokers, one for
 *   each type/class of servant role.
 * </p>
 * <p>
 *   The marshalling format is JSON and GSON is
 *   used as implementation library.
 * </p>
 *
 * @author Henrik Baerbak Christensen, CS @ AU
 */
public class GameLobbyRootInvoker implements Invoker {
  private final GameLobby lobby;
  private final NameService nameService;
  private final Map<String, Invoker> invokerMap;
  private Gson gson;

  public GameLobbyRootInvoker(GameLobby lobby) {
    this.lobby = lobby;
    gson = new Gson();

    nameService = new InMemoryNameService();
    invokerMap = new HashMap<>();

    // Create an invoker for each handled type/class
    // and put them in a map, binding them to the
    // operationName prefixes
    Invoker gameLobbyInvoker = new GameLobbyInvoker(lobby, nameService, gson);
    invokerMap.put(MarshallingConstant.GAME_LOBBY_PREFIX, gameLobbyInvoker);
    Invoker futureGameInvoker = new FutureGameInvoker(nameService, gson);
    invokerMap.put(MarshallingConstant.FUTUREGAME_PREFIX, futureGameInvoker);
    Invoker gameInvoker = new GameInvoker(nameService, gson);
    invokerMap.put(MarshallingConstant.GAME_PREFIX, gameInvoker);
  }
  @Override
  public String handleRequest(String request) {
    // TODO: MAR
    RequestObject requestObject = gson.fromJson(request, RequestObject.class);
    String operationName = requestObject.getOperationName();

    String reply = null;

    // Identify the invoker to use
    String type = operationName.substring(0, operationName.indexOf(MarshallingConstant.SEPARATOR));
    Invoker subInvoker = invokerMap.get(type);

    // And do the upcall on the subInvoker
    try {
      reply = subInvoker.handleRequest(request);

    } catch (UnknownServantException e) {
      reply = gson.toJson(
              new ReplyObject(
                      HttpServletResponse.SC_NOT_FOUND,
                      e.getMessage()));
    }

    return reply;
  }
}
