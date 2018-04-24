package gamelobby.domain;

import frds.broker.Servant;

public interface GameLobby extends Servant {
  FutureGame createGame(String playerName, int playerLevel);

  FutureGame joinGame(String playerName, String joinToken);
}
