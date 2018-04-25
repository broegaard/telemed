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

import gamelobby.domain.Game;

import java.util.UUID;

/** Servant implementation of Game.
 *
 * At 24 Apr 2018
 *
 * @author Henrik Baerbak Christensen, CS @ AU
 */
public class GameServant implements Game {
  private String[] playerList = new String[2];
  private String objectId;

  public GameServant(String firstPlayerName, String secondPlayerName) {
    playerList[0] = firstPlayerName;
    playerList[1] = secondPlayerName;

    // Assign unique id
    objectId = UUID.randomUUID().toString();
  }

  @Override
  public String getPlayerName(int index) {
    return playerList[index];
  }

  @Override
  public String getId() {
    return objectId;
  }
}
