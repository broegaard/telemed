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

package gamelobby.service;

import gamelobby.domain.FutureGame;
import gamelobby.domain.Game;

/** Storage of servant objects created by the server.
 * Basically encapsulates Map's that allows 'put' and 'get'
 * operations on the server side servant objects.
 *
 * @author Henrik Baerbak Christensen, CS @ AU
 */
public interface ObjectStorage {
  /** Put a future game into the map under given id
   *
   * @param objectId ID of the object
   * @param futureGame the servant object
   */
  void putFutureGame(String objectId, FutureGame futureGame);

  /** Get a future game.
   *
   * @param objectId the id of the servant object to get.
   * @return the future game with this id.
   */
  FutureGame getFutureGame(String objectId);

  /** Put a game into the map under given id
   *
   * @param objectId ID of the object
   * @param game the servant object
   */
  void putGame(String objectId, Game game);

  /** Get a game.
   *
   * @param objectId the id of the servant object to get
   * @return the servant object
   */
  Game getGame(String objectId);
}
