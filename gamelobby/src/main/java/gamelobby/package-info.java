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

/**
 * Game Lobby project - a simplified system for pairing a number of
 * players waiting to join distributed games; the main focus is
 * the learning goal of showing how objects can be created on
 * a server in a client-server architecture, and made available for
 * remote communication in clients, using the Broker pattern.
 *
 * <p>
 *   The current architecture is suitable but the implemented
 *   behavior have a number of limitations, aimed at reducing
 *   code complexity. Specifically, A) only two player games are
 *   supported: the first player creates the game, and the second
 *   player joins it; B) the resulting 'game' has no behavior at
 *   all, except allowing players to view the names of the two players;
 *   C) error handling is over-simplified, for instance there is
 *   no check that a third player joins a game.
 * </p>
 * <p>
 *   The interaction is described in 'Flexible, Reliable, Distributed
 *   Software', published on leanpub.com.
 * </p>
 *
 * @author Henrik Baerbak Christensen, CS @ AU, 2018
 */
package gamelobby;