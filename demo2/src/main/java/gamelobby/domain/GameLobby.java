package gamelobby.domain;

public interface GameLobby {
  FutureGame createGame(int playerLevel);

  FutureGame joinGame(String joinToken);
}
