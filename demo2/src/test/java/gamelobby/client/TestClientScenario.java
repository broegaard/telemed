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

package gamelobby.client;

import gamelobby.domain.FutureGame;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.nullValue;

import frds.broker.ClientRequestHandler;
import frds.broker.Invoker;
import frds.broker.Requestor;
import frds.broker.marshall.json.StandardJSONRequestor;

import gamelobby.domain.GameLobby;
import gamelobby.doubles.LocalMethodCallClientRequestHandler;
import gamelobby.server.GameLobbyJSONInvoker;
import gamelobby.server.GameLobbyServant;


/**
 * At 25 Apr 2018
 *
 * @author Henrik Baerbak Christensen, CS @ AU
 */
public class TestClientScenario {

  @Test
  public void shouldHandleStory1OnClient() {
    // Create the server side facade to the lobby
    GameLobby lobby = GameLobbyServant.getInstance();

    // Server side broker implementation of the Invoker
    Invoker invoker = new GameLobbyJSONInvoker(lobby);

    // Create client side broker implementations, using the local
    // method client request handler to avoid any real IPC layer.
    ClientRequestHandler clientRequestHandler =
            new LocalMethodCallClientRequestHandler(invoker);
    Requestor requestor =
            new StandardJSONRequestor(clientRequestHandler);

    // Finally, create the client proxy for the lobby
    GameLobby lobbyProxy = new GameLobbyProxy(requestor);

    FutureGame player1Future = lobbyProxy.createGame("Pedersen", 0);
    assertThat(player1Future, is(not(nullValue())));
  }
}