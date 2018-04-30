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

/**
 * The Game Lobby system is a simple system for players to enter
 * a lobby which allows them to create a new game or join an existing.
 * As it is just a demonstration of how to implement multi-object
 * dispatch on the server side, the Game roles does not contain
 * any real game responsibilities.
 * <p>
 *   The general scenarios is to getFutureGame a GameLobby instance and
 *   ask it to create a game. This returns a FutureGame instance
 *   that represents the game that will eventually be joined by
 *   all players (here only two player games are supported).
 *   Another player will then ask the lobby to join the created
 *   game. Once both players are active (one creator, one joiner)
 *   the FutureGame can be asked to return the actual Game instance
 *   for actual remote play of the game.
 * </p>
 * <p>
 *   See the TestScenario test case for the actual code to
 *   create distributed play.
 * </p>
 *
 * @author Henrik Baerbak Christensen, CS @ AU
 */
package gamelobby.domain;