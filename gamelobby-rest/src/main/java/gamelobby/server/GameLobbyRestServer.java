package gamelobby.server;

import com.google.gson.Gson;
import com.mashape.unirest.http.*;

import javax.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

public class GameLobbyRestServer {
  private final Gson gson;

  public GameLobbyRestServer(int portNumber) {
    port(portNumber);
    gson = new Gson();
    configureRoutes();
  }

  // Create the 'database' of future game resources
  private int futureGameId = 42;
  private Map<Integer, FutureGameResource> database
          = new HashMap<>();

  private void configureRoutes() {

    // Create FutureGame
    post("/lobby", (request, response) ->
    {
      // Demarshall body
      String payload = request.body();
      JsonNode asNode = new JsonNode(payload);
      String playerName = asNode.getObject().getString("player");
      Integer level = asNode.getObject().getInt("level");

      // Call 'domain' code to create the future game
      int futureGameId = createFutureGameAndInsertIntoDatabase(playerName, level);
      FutureGameResource fgame = database.get(futureGameId);

      // And construct the response of the POST
      response.status(HttpServletResponse.SC_CREATED);
      response.header("Location",
              request.host() + "/lobby/" + futureGameId);
      return gson.toJson(fgame);
    });

    // Get FutureGame
    get( "/lobby/:futureGameId", (request, response) -> {
      String idAsString = request.params(":futureGameId");
      // TODO: Handle non-integer provided as path
      Integer id = Integer.parseInt(idAsString);

      FutureGameResource fgame = database.get(id);

      response.status(HttpServletResponse.SC_OK);

      return gson.toJson(fgame);
    });

    // Update the FutureGame => make a state transition
    put( "/lobby/:futureGameId", (request, response) -> {
      String idAsString = request.params(":futureGameId");
      // TODO: Handle non-integer provided as path
      Integer id = Integer.parseInt(idAsString);

      FutureGameResource fgame = database.get(id);

      // Demarshall body
      String payload = request.body();
      JsonNode asNode = new JsonNode(payload);

      String playerTwo = asNode.getObject().getString("playerTwo");
      System.out.println("#--> " + playerTwo);

      // Create game instance
      int gameId = createGameResourceAndInsertIntoDatabase(fgame);

      // Update resource
      fgame.setPlayerTwo(playerTwo);
      fgame.setAvailable(true);
      fgame.setNext("/lobby/game/" + gameId);

      return gson.toJson(fgame);
    });

  }

  private GameResource theOneGameOurServerHandles;
  private int createGameResourceAndInsertIntoDatabase(FutureGameResource fgame) {
    // Fake it code - we only handle a single game instance with id = 77;
    int theId = 77;
    theOneGameOurServerHandles =
            new GameResource( fgame.getPlayerOne(), fgame.getPlayerTwo(),
                    fgame.getLevel(), theId);
    return theId;
  }

  private int createFutureGameAndInsertIntoDatabase(String playerName, Integer level) {
    FutureGameResource f = new FutureGameResource(playerName, level);
    database.put(futureGameId, f);
    futureGameId++;

    return futureGameId - 1;
  }

  public void stop() {
    spark.Spark.stop();
  }
}
