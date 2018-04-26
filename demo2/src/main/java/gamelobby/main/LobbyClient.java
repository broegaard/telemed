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

import frds.broker.ClientRequestHandler;
import frds.broker.Requestor;
import frds.broker.ipc.socket.SocketClientRequestHandler;
import frds.broker.marshall.json.StandardJSONRequestor;
import gamelobby.client.GameLobbyProxy;
import gamelobby.domain.FutureGame;
import gamelobby.domain.GameLobby;

import java.io.IOException;

/**
 * At 26 Apr 2018
 *
 * @author Henrik Baerbak Christensen, CS @ AU
 */
public class LobbyClient {
  private String operation;
  private String name;
  private String hostname;
  private String token;

  public static void main(String[] args) throws IOException {
    new LobbyClient(args);
  }

  public LobbyClient(String[] args) {
    parseCommandlineParameters(args);

    System.out.println("LobbyClient: Asked to do operation "+operation+" for player "+name);
    ClientRequestHandler clientRequestHandler
            = new SocketClientRequestHandler(hostname, 37321);
    Requestor requestor = new StandardJSONRequestor(clientRequestHandler);

    GameLobby lobby = new GameLobbyProxy(requestor);

    if (operation.equals("create")) {
      FutureGame f = lobby.createGame(name, 8);
      String token = f.getJoinToken();
      System.out.println(" Future created, the join token is " + token);
    } else if (operation.equals("join")) {
      FutureGame f = lobby.joinGame(name, token);
      System.out.println(" Future joined, available is "
              + f.isAvailable());
      System.out.println(" The Game id is " + f.getGame().getId());
      System.out.println(" The Game's 1st player is " + f.getGame().getPlayerName(0));
      System.out.println(" The Game's 2nd player is " + f.getGame().getPlayerName(1));
    }

  }


  private void parseCommandlineParameters(String[] args) {
    if (args.length < 4) {
      explainAndFail();
    }
    operation = args[0];
    name = args[1];
    token = args[2];
    hostname = args[3];
  }

  private static void explainAndFail() {
    System.out.println("Usage: LobbyClient <operation> <name> <token> <host>");
    System.out.println("  operation is either 'create' or 'join' or 'state'");
    System.out.println("  token is ignored for all but the join operation");

  }
}