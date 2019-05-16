package gamelobby.server;

import com.google.gson.Gson;
import com.mashape.unirest.http.*;

import javax.servlet.http.HttpServletResponse;

import java.awt.*;
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

  private void configureRoutes() {

    // Create Remote Game
    post("/lobby", (request, response) ->
    {
      debugOutput("-> POST /lobby: " + request.body().toString());

      // Demarshall body
      String payload = request.body();
      JsonNode asNode = new JsonNode(payload);
      String playerName = asNode.getObject().getString("playerOne");
      Integer level = asNode.getObject().getInt("level");

      // Call 'domain' code to create the future game
      int futureGameId = createFutureGameAndInsertIntoDatabase(playerName, level);
      FutureGameResource fgame = database.get(futureGameId);

      // And construct the response of the POST
      response.status(HttpServletResponse.SC_CREATED);
      String location = request.host() + "/lobby/" + futureGameId;
      response.header("Location", location );

      debugOutput("-< Location: " + location);
      debugOutput("-< Reply: " + gson.toJson(fgame));

      return gson.toJson(fgame);
    });

    // Get FutureGame
    get( "/lobby/:futureGameId", (request, response) -> {
      debugOutput("-> GET /lobby/{future-game-id}: " + request.body().toString());
      String idAsString = request.params(":futureGameId");
      // TODO: Handle non-integer provided as path
      Integer id = Integer.parseInt(idAsString);

      FutureGameResource fgame = getFutureGameFromDatabase(id);

      response.status(HttpServletResponse.SC_OK);

      debugOutput("-< Reply: " + gson.toJson(fgame));

      return gson.toJson(fgame);
    });

    // Update the FutureGame => make a state transition
    post( "/lobby/:futureGameId", (request, response) -> {
      debugOutput("-> POST /lobby/{future-game-id}: " + request.body().toString());

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
      updateFutureGameInDatabase(id, fgame);

      debugOutput("-< Reply: " + gson.toJson(fgame));

      return gson.toJson(fgame);
    });

    // Get FutureGame
    get( "/lobby/game/:gameId", (request, response) -> {
      debugOutput("-> GET /lobby/game/{game-id}: " + request.body().toString());
      String idAsString = request.params(":gameId");
      // TODO: Handle non-integer provided as path
      Integer id = Integer.parseInt(idAsString);

      response.status(HttpServletResponse.SC_OK);

      GameResource game = getGameFromDatabase(id);

      debugOutput("-< Reply: " + gson.toJson(game));

      return gson.toJson(game);
    });

    // PUT on move resource
    put( "/lobby/game/move/:gameId", (request, response) -> {
      debugOutput("-> PUT /lobby/game/move/{game-id}: " + request.body().toString());
      String idAsString = request.params(":gameId");
      // TODO: Handle non-integer provided as path
      Integer id = Integer.parseInt(idAsString);

      // Demarshall body
      String payload = request.body();
      JsonNode asNode = new JsonNode(payload);
      asNode.getObject().put("isValid",  true);

      // Update game resource
      GameResource game = getGameFromDatabase(id);
      game.makeAMove();

      debugOutput("-< Reply: " + asNode.getObject());
      return asNode.getObject();
    });

  }


  // === Domain handling of FutureGame resources
  private int futureGameId = 42;
  private Map<Integer, FutureGameResource> database
          = new HashMap<>();

  private int createFutureGameAndInsertIntoDatabase(String playerName, Integer level) {
    FutureGameResource f = new FutureGameResource(playerName, level);
    database.put(futureGameId, f);
    futureGameId++;

    return futureGameId - 1;
  }
  private FutureGameResource getFutureGameFromDatabase(int id) {
    return database.get(id);
  }
  private void updateFutureGameInDatabase(int id, FutureGameResource fgame) {
    database.put(id, fgame);
  }

  // === Domain handling of Game resources
  private GameResource theOneGameOurServerHandles;
  private int createGameResourceAndInsertIntoDatabase(FutureGameResource fgame) {
    // Fake it code - we only handle a single game instance with id = 77;
    int theId = 77;
    theOneGameOurServerHandles =
            new GameResource( fgame.getPlayerOne(), fgame.getPlayerTwo(),
                    fgame.getLevel(), theId);
    return theId;
  }
  private GameResource getGameFromDatabase(int id) {
    return theOneGameOurServerHandles;
  }


  public void stop() {
    spark.Spark.stop();
  }

  private void debugOutput(String s) {
    System.out.println(s);
  }


}
