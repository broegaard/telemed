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

package gamelobby.client;

import frds.broker.ClientProxy;
import frds.broker.IPCException;
import frds.broker.Requestor;
import gamelobby.common.MarshallingConstant;
import gamelobby.domain.Game;

/** Proxy for the game. Never create it your self,
 * getFutureGame it from the FutureGame once the 'isAvailable()'
 * method returns true.
 *
 * @author Henrik Baerbak Christensen, CS @ AU
 */
public class GameProxy implements Game, ClientProxy {
  private final String objectId;
  private final Requestor requestor;

  GameProxy(String objectId, Requestor requestor) {
    this.objectId = objectId;
    this.requestor = requestor;
  }

  @Override
  public String getPlayerName(int index) {
    String name = requestor.sendRequestAndAwaitReply(objectId,
            MarshallingConstant.GAME_GET_PLAYER_NAME, String.class, index);
    return name;
  }

  @Override
  public String getId() {
    return objectId;
  }

  @Override
  public String getPlayerInTurn() {
    String name = requestor.sendRequestAndAwaitReply(objectId,
        MarshallingConstant.GAME_GET_PLAYER_IN_TURN, String.class);
    return name;
  }

  @Override
  public void move() {
    requestor.sendRequestAndAwaitReply(objectId,
        MarshallingConstant.GAME_MOVE, String.class);

  }
}
