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
import gamelobby.client.GameProxy;
import gamelobby.domain.FutureGame;
import gamelobby.domain.Game;
import gamelobby.domain.GameLobby;

import java.io.IOException;

/** A crude manual test case to be run from the command line,
 * allows a client to create and join games.
 *
 * @author Henrik Baerbak Christensen, CS @ AU
 */
public class LobbyClient {
  private String operation;
  private String name;
  private String hostname;
  private String objectId;

  public static void main(String[] args) throws IOException {
    new LobbyClient(args);
  }

  public LobbyClient(String[] args) {
    parseCommandlineParameters(args);

    System.out.println("LobbyClient: Asked to do operation "+operation+" for player "+name);
    ClientRequestHandler clientRequestHandler
            = new SocketClientRequestHandler(hostname, Constant.SERVER_PORT);
    Requestor requestor = new StandardJSONRequestor(clientRequestHandler);

    GameLobby lobby = new GameLobbyProxy(requestor);

    if (operation.equals("create")) {
      FutureGame f = lobby.createGame(name, 8);
      String token = f.getJoinToken();
      System.out.println(" Future created, the join objectId is " + token);
    } else if (operation.equals("join")) {
      FutureGame f = lobby.joinGame(name, objectId);
      System.out.println(" Future joined, available is "
              + f.isAvailable());
      Game game = f.getGame();
      System.out.println(" The Game id is " + game.getId());
      System.out.println(" The Game's 1st player is " + game.getPlayerName(0));
      System.out.println(" The Game's 2nd player is " + game.getPlayerName(1));
      System.out.println(" The Game's PLAYER IN TURN is " + game.getPlayerInTurn());
    } else if (operation.equals("move")) {
      Game game = new GameProxy(objectId, requestor);
      System.out.println(" The Game id is " + game.getId());
      System.out.println(" The Game's PLAYER IN TURN is " + game.getPlayerInTurn());
      game.move();
      System.out.println(" A move was made, and now PLAYER IN TURN is " + game.getPlayerInTurn());
    }
  }

  private void parseCommandlineParameters(String[] args) {
    if (args.length < 4) {
      explainAndFail();
    }
    operation = args[0];
    name = args[1];
    objectId = args[2];
    hostname = args[3];
  }

  private static void explainAndFail() {
    System.out.println("Usage: LobbyClient <operation> <name> <objectId> <host>");
    System.out.println("  operation is either 'create' or 'join' or 'move'");
    System.out.println("  objectId is only used in join or move");
    System.out.println("    for join, it is the joinToken");
    System.out.println("    for move, it is the game's objectId");

  }
}