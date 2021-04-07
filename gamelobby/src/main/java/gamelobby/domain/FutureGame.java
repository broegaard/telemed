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

package gamelobby.domain;

/** A 'Future' that represents a Game instance
 * that will eventually be created or has been
 * created.
 *
 * <p>
 *   Never create FutureGame instances, you have
 *   to use the GameLobby to create it.
 * </p>
 *
 * @author Henrik Baerbak Christensen, CS @ AU
 */
public interface FutureGame {
  /** Return the 'join token' which is a string
   * that the second player uses, to join a given
   * game.
   * @return the join token for this future game.
   */
  String getJoinToken();

  /** Returns if a game is available, that is,
   * two players are connected to play a game.
   *
   * @return false in case only a single player
   * has joined/created the game; true once
   * two players have joined.
   */
  boolean isAvailable();

  /** Get the real game instance.
   * PRECONDITION: You can only get a
   * game instance if the method 'isAvailable()'
   * returns true.
   *
   * @return the game instance that this
   * future game represents.
   */
  Game getGame();

  /** Get the unique id of this game.
   *
   * @return id of this game instance.
   */
  String getId();
}
