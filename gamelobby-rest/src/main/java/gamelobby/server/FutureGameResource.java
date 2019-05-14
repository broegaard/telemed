package gamelobby.server;

import java.util.StringJoiner;

public class FutureGameResource {
  private final String playerOne;
  private final String playerTwo;
  private final int level;
  private final boolean available;
  private final String next;

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
}
