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

import frds.broker.Servant;
import gamelobby.domain.FutureGame;
import gamelobby.domain.Game;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/** Servant implementation of FutureGame role.
 *
 * @author Henrik Baerbak Christensen, CS @ AU
 */
public class FutureGameServant implements FutureGame, Servant {
  private final String joinToken;

  private Game theGame;
  private String firstPlayer;
  private String id;

  // Make a global thread safe counter for the join token
  // Note: This will ONLY work in a single-server solution;
  // you cannot load-balance multiple servers using this technique
  // as server-1 may create the same 'unique' token
  // as server-2
  private static AtomicInteger gameCounter = new AtomicInteger();

  public FutureGameServant(String playerName, int playerLevel) {
    // ignore the player level for now

    // Add the player's name
    firstPlayer = playerName;

    // Create the object ID to bind server and client side
    // Servant-ClientProxy objects together
    id = UUID.randomUUID().toString();

    // Create a unique game join token.
    joinToken = "game-"+gameCounter.incrementAndGet();

    // No actual game has been created yet.
    theGame = null;
  }


  @Override
  public String getJoinToken() {
    return joinToken;
  }

  @Override
  public boolean isAvailable() {
    return theGame != null;
  }

  @Override
  public Game getGame() {
    return theGame;
  }

  @Override
  public String getId() {
    return id;
  }

  public void setGame(Game theActualGame) {
    theGame = theActualGame;
  }

  public String getFirstPlayerName() {
    return firstPlayer;
  }

  @Override
  public String toString() {
    return "FutureGameServant{" +
            "joinToken='" + joinToken + '\'' +
            ", theGame=" + theGame +
            ", firstPlayer='" + firstPlayer + '\'' +
            ", id='" + id + '\'' +
            '}';
  }
}
