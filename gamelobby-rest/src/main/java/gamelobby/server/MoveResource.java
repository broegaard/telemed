package gamelobby.server;

import java.util.StringJoiner;

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
