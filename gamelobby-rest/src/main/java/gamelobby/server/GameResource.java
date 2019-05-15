package gamelobby.server;

public class GameResource {
  private final String playerOne;
  private final String playerTwo;
  private final int level;
  private final int id;
  private final String playerInTurn;

  public GameResource(String playerOne, String playerTwo, int level, int theId) {
    this.playerOne = playerOne;
    this.playerTwo = playerTwo;
    this.level = level;
    this.id = theId;
    this.playerInTurn = playerOne;
  }
}
