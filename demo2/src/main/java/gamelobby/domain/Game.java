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

import frds.broker.Servant;

/** The actual Game object that represents some
 * game. Here we just have a game with two players,
 * and the only responsibility is to know the names
 * of player 0 and player 1.
 *
 * @author Henrik Baerbak Christensen, CS @ AU
 */
public interface Game extends Servant {
  String getPlayerName(int index);
}
