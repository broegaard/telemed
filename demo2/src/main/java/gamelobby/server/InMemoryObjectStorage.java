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

import gamelobby.domain.FutureGame;
import gamelobby.domain.Game;

import java.util.HashMap;
import java.util.Map;

/** An ObjectStorage that keeps all servant objects in memory.
 * At 30 Apr 2018
 *
 * @author Henrik Baerbak Christensen, CS @ AU
 */
public class InMemoryObjectStorage implements ObjectStorage {
  private Map<String, FutureGame> futureGameMap;
  private Map<String, Game> gameMap;

  public InMemoryObjectStorage() {
    this.futureGameMap = new HashMap<>();
    this.gameMap = new HashMap<>();
  }

  @Override
  public void putFutureGame(String objectId, FutureGame futureGame) {
    futureGameMap.put(objectId, futureGame);
  }

  @Override
  public FutureGame getFutureGame(String objectId) {
    return futureGameMap.get(objectId);
  }

  @Override
  public void putGame(String objectId, Game game) {
    gameMap.put(objectId, game);
  }

  @Override
  public Game getGame(String objectId) {
    return gameMap.get(objectId);
  }
}
