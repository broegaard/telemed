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

package gamelobby.main;

import frds.broker.Invoker;
import frds.broker.ipc.socket.SocketServerRequestHandler;

import gamelobby.domain.GameLobby;
import gamelobby.marshall.GameLobbyRootInvoker;
import gamelobby.server.GameLobbyServant;

/** Socket based GameLobby server.
 *
 * @author Henrik Baerbak Christensen, CS @ AU
 */
public class LobbyServer {

  public static void main(String[] args) throws Exception {
    new LobbyServer();
  }

  public LobbyServer() {
    int port = Constant.SERVER_PORT;
    // Define the server side delegates

    GameLobby lobby = GameLobbyServant.getInstance();
    Invoker invoker = new GameLobbyRootInvoker(lobby);

    // Configure a socket based server request handler
    SocketServerRequestHandler ssrh =
            new SocketServerRequestHandler(port, invoker);

    // Welcome
    System.out.println("=== GameLobby Socket based Server Request Handler (port:"
            + port + ") ===");
    System.out.println(" Use ctrl-c to terminate!");
    ssrh.start();
  }
}

