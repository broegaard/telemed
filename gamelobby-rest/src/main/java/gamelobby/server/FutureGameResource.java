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

/** The Record/PODO structure that represents
 * a future game resource. Note that we do not
 * reuse the FutureGame interface from the
 * gamelobby module, as it does not fit
 * the RESTish way well.
 */

public class FutureGameResource {
  private final String playerOne;
  private String playerTwo;
  private final int level;
  private boolean available;
  private String next;

  public FutureGameResource(String playerName, int level) {
    playerOne = playerName;
    playerTwo = "null";
    this.level = level;
    available = false;
    next = "null";
  }

  public boolean isAvailable() {
    return available;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", FutureGameResource.class.getSimpleName() + "[", "]")
            .add("playerOne='" + playerOne + "'")
            .add("playerTwo='" + playerTwo + "'")
            .add("level=" + level)
            .add("available=" + available)
            .add("next='" + next + "'")
            .toString();
  }

  public String getPlayerOne() {
    return playerOne;
  }

  public String getPlayerTwo() {
    return playerTwo;
  }

  public int getLevel() {
    return level;
  }

  public String getNext() {
    return next;
  }

  public void setPlayerTwo(String playerTwo) {
    this.playerTwo = playerTwo;
  }

  public void setAvailable(boolean isAvailable) {
    available = isAvailable;
  }

  public void setNext(String next) {
    this.next = next;
  }
}
