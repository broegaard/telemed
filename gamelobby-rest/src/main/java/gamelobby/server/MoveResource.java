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

package gamelobby.server;

import java.util.StringJoiner;

/** Resource to represent a move:
 * Player p is moving from f to t.
 */
public class MoveResource {
  private final String player;
  private final String from;
  private final String to;

  @Override
  public String toString() {
    return new StringJoiner(", ", MoveResource.class.getSimpleName() + "[", "]")
            .add("player='" + player + "'")
            .add("from='" + from + "'")
            .add("to='" + to + "'")
            .toString();
  }

  public MoveResource(String player, String from, String to) {
    this.player = player;
    this.from = from;
    this.to = to;
  }

  public String getPlayer() {
    return player;
  }

  public String getFrom() {
    return from;
  }

  public String getTo() {
    return to;
  }
}
