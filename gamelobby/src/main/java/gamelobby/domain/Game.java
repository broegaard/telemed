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

/** The actual Game object that represents some
 * game. Here we just have a game with two players,
 * and the only responsibility is to know the names
 * of player 1 and player 2.
 * <p>
 *   Games cannot be created by themselves, you
 *   have to use the GameLobby to create one.
 * </p>
 *
 * @author Henrik Baerbak Christensen, CS @ AU
 */
public interface Game {
  /** Get name of given player.
   * PRECONDITON: only index == 0 and 1 are
   * allowed.
   * @param index the player to get the name of,
   *              either 0 or 1
   * @return name of the player
   */
  String getPlayerName(int index);

  /** Get the unique id of this game object
   *
   * @return object id.
   */
  String getId();

  /** Get the name of the player whose turn
   * it is.
   * @return name of player in turn
   */
  String getPlayerInTurn();

  /** "Make a move" - the only action provided
   * here is that the player in turn changes
   * back and forth between the two players.
   * Note: No validation is made that it is
   * the 'right' player who makes a 'move'!
   */
  void move();
}
