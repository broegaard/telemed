package gamelobby.server;

import java.util.StringJoiner;

public class GameResource {
  private final String playerOne;
  private final String playerTwo;
  private final int level;
  private final int id;
  private String playerInTurn;
  private int noOfMovesMade;
  private String next;

  // Not used in our demonstation code, represents
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
    return "/lobby/game/" + getId() + "/move/" + getNoOfMovesMade();
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
