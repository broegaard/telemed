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

package gamelobby.domain;

/**
 * The GameLobby is responsible for allowing players to
 * create and join games. One player must create a game,
 * while another may join it.
 * <p>
 *   The lobby returns FutureGame instances that provides
 *   the actual Game instance once both players are active.
 * </p>
 */
public interface GameLobby {
  /** Create a new 'future' game to be played.
   *
   * @param playerName Name of the player
   * @param playerLevel Level of play, to represent
   *                    novice, casual, expert, etc.
   *                    (not used in this demo)
   * @return a FutureGame that can be used for the
   * second player to join the game.
   */
  FutureGame createGame(String playerName, int playerLevel);

  /** Join an existing game.
   *
   * @param playerName Name of the player
   * @param joinToken A unique string that identifies
   *                  the FutureGame that this player
   *                  wants to join. Must be provided by
   *                  the creating player.
   * @return a FutureGame that can be used to
   * get the actual game.
   * @throws UnknownServantException in case the
   * join token does not represent any existing game.
   */
  FutureGame joinGame(String playerName, String joinToken);
}
