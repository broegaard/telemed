package gamelobby.server;

import java.util.StringJoiner;

public class GameResource {
  private final String playerOne;
  private final String playerTwo;
  private final int level;
  private final int id;
  private String playerInTurn;
  private int moveCount;

  public String getNext() {
    return next;
  }

  private final String next;

  public GameResource(String playerOne, String playerTwo, int level, int theId) {
    this.playerOne = playerOne;
    this.playerTwo = playerTwo;
    this.level = level;
    this.id = theId;
    this.playerInTurn = playerOne;
    this.next = "/lobby/game/move/" + theId;
    this.moveCount = 0;
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

  public int getId() {
    return id;
  }

  public String getPlayerInTurn() {
    return playerInTurn;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", GameResource.class.getSimpleName() + "[", "]")
            .add("playerOne='" + playerOne + "'")
            .add("playerTwo='" + playerTwo + "'")
            .add("level=" + level)
            .add("id=" + id)
            .add("playerInTurn='" + playerInTurn + "'")
            .toString();
  }

  public void makeAMove() {
    if (playerInTurn.equals(playerOne)) {
      playerInTurn = playerTwo;
    } else {
      playerInTurn = playerOne;
    }
    moveCount++;
  }

  public int getMoveCount() {
    return moveCount;
  }
}
