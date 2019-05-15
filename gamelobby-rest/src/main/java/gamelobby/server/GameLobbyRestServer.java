package gamelobby.server;

import com.google.gson.Gson;
import com.mashape.unirest.http.*;

import javax.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

/** A DEMONSTRATION ONLY game lobby server using REST
 * to make state changes in a game system.
 *
 * NO ERROR CHECKING at all is implemented.
 *
 * ONLY ONE FutureGame resource and ONE Game resource
 * is handled. No real domain code is implemented,
 * all are Fake-It code within the server.
 */

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

      // Update resource
      fgame.setPlayerTwo(playerTwo);
      fgame.setAvailable(true);

      // Create game instance
      int gameId = createGameResourceAndInsertIntoDatabase(fgame);

      fgame.setNext("/lobby/game/" + gameId);
      database.put(id, fgame);

      return gson.toJson(fgame);
    });

    // Get FutureGame
    get( "/lobby/game/:gameId", (request, response) -> {
      String idAsString = request.params(":gameId");
      // TODO: Handle non-integer provided as path
      Integer id = Integer.parseInt(idAsString);

      response.status(HttpServletResponse.SC_OK);

      return gson.toJson(theOneGameOurServerHandles);
    });

    // PUT on move resource
    put( "/lobby/game/move/:gameId", (request, response) -> {
      String idAsString = request.params(":gameId");
      // TODO: Handle non-integer provided as path
      Integer id = Integer.parseInt(idAsString);

      // Demarshall body
      String payload = request.body();
      JsonNode asNode = new JsonNode(payload);
      asNode.getObject().put("isValid",  true);

      // Update game resource
      theOneGameOurServerHandles.makeAMove();

      return asNode.getObject();
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
