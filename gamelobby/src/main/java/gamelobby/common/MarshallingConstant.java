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

package gamelobby.common;

/** Versioning used by the marshalling layer in the
 * broker pattern to identify object types and method
 * names.
 *
 * NOTE: method names / operations names MUST be formatted
 * with an underscore between the type prefix and the
 * method name as this convention is used by the
 * invoker.
 *
 * @author Henrik Baerbak Christensen, CS @ AU
 */
public class MarshallingConstant {

  // Type prefixes
  public static final String GAME_LOBBY_PREFIX = "gamelobby";
  public static final String FUTUREGAME_PREFIX = "futuregame";
  public static final String GAME_PREFIX = "game";

  // Method ids for marshalling
  public static final String GAMELOBBY_CREATE_GAME_METHOD = GAME_LOBBY_PREFIX + "_create_game_method";
  public static final String GAMELOBBY_JOIN_GAME_METHOD = GAME_LOBBY_PREFIX + "_join_game_method";

  public static final String FUTUREGAME_GET_JOIN_TOKEN_METHOD = FUTUREGAME_PREFIX + "_get_join_token_method";
  public static final String FUTUREGAME_IS_AVAILABLE_METHOD = FUTUREGAME_PREFIX + "_is_available_method";
  public static final String FUTUREGAME_GET_GAME_METHOD = FUTUREGAME_PREFIX + "_get_game_method";

  public static final String GAME_GET_PLAYER_NAME = GAME_PREFIX + "_get_player_name_method";
  public static final String GAME_GET_PLAYER_IN_TURN = GAME_PREFIX + "_get_player_in_turn_method";
  public static final String GAME_MOVE = GAME_PREFIX + "_move_method";
}
