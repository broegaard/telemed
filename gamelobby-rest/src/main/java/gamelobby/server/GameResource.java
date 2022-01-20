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

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/** Resource to represent a game: names of
 * players, player in turn, no of moves made,
 * and 'next' link to the next move resource to
 * update in order to advance the game's state.
 *
 * Note: Proper modeling of the GameResource is not
 * the issue here, so a lot of hardcoded URI paths
 * and other fake-it code is present.
 */
public class GameResource {
  private final String playerOne;
  private final String playerTwo;
  private final int level;
  private final int id;
  private String playerInTurn;
  private int noOfMovesMade;
  private String next;

  // Not used in our demonstration code, represents
  // a real game's internal board state...
  private final String board;

  public GameResource(String playerOne, String playerTwo,
                      int level, int theId) {
    this.playerOne = playerOne;
    this.playerTwo = playerTwo;
    this.level = level;
    this.id = theId;
    this.playerInTurn = playerOne;
    this.noOfMovesMade = 0;
    this.board = "[...]";

    this.next = computeNextLink();
  }

  private String computeNextLink() {
    return "/lobby/game/" + id + "/move/" + getNoOfMovesMade();
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

  public int getId() {
    return id;
  }

  public String getPlayerInTurn() {
    return playerInTurn;
  }

  // make a move, and return ID of next move resource
  public MoveResource makeAMove(MoveResource move) {
    // TODO: Handle invalid moves by returning a move resource
    // that is nullified or in other ways show an illegal move.

    // Otherwise 'make the move'
    if (playerInTurn.equals(playerOne)) {
      playerInTurn = playerTwo;
    } else {
      playerInTurn = playerOne;
    }

    noOfMovesMade++;
    this.next = computeNextLink();

    return move;
  }

  public int getNoOfMovesMade() {
    return noOfMovesMade;
  }

  public String getBoard() {
    return board;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", GameResource.class.getSimpleName() + "[", "]")
            .add("playerOne='" + playerOne + "'")
            .add("playerTwo='" + playerTwo + "'")
            .add("level=" + level)
            .add("id=" + id)
            .add("playerInTurn='" + playerInTurn + "'")
            .add("noOfMovesMade=" + noOfMovesMade)
            .add("board='" + board + "'")
            .add("next='" + next + "'")
            .toString();
  }

}
