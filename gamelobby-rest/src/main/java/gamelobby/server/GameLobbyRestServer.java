package gamelobby.server;

import com.google.gson.Gson;
import com.mashape.unirest.http.*;
import spark.Request;

import javax.servlet.http.HttpServletResponse;

import java.util.*;
import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

/** A DEMONSTRATION ONLY game lobby server using REST
 * to make state changes in a game system using the
 * HATEOAS paradigm.
 *
 * NO ERROR CHECKING at all is implemented. Only
 * HAPPY PATH is supported, that is, no handling of
 * invalid moves, retrieving non-existing resources, etc.
 *
 * ONLY ONE FutureGame resource and ONE Game resource
 * is handled. No real domain code is implemented,
 * all are Fake-It code within the server.
 *
 * If you want to follow the interaction between
 * client and server, enable the println statement
 * in the debugOutput method and run the test case.
 */

public class GameLobbyRestServer {
  private final Gson gson;

  public GameLobbyRestServer(int portNumber) {
    port(portNumber);
    gson = new Gson();
    configureRoutes();
  }

  private void configureRoutes() {

    // Create Remote Game (i.e. create a FutureGame resource)
    post("/lobby", (request, response) ->
    {
      debugOutput(request);

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

    // Get FutureGame resource
    get( "/lobby/:futureGameId", (request, response) -> {
      debugOutput(request);

      String idAsString = request.params(":futureGameId");
      // TODO: Handle non-integer provided as path
      Integer id = Integer.parseInt(idAsString);

      FutureGameResource fgame = getFutureGameFromDatabase(id);

      response.status(HttpServletResponse.SC_OK);

      debugOutput("-< Reply: " + gson.toJson(fgame));

      return gson.toJson(fgame);
    });

    // Partial update the FutureGame resource, thereby transition into a valid game
    post( "/lobby/:futureGameId", (request, response) -> {
      debugOutput(request);

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

    // Get Game resource
    get( "/lobby/game/:gameId", (request, response) -> {
      debugOutput(request);
      String idAsString = request.params(":gameId");
      // TODO: Handle non-integer provided as path
      Integer id = Integer.parseInt(idAsString);

      response.status(HttpServletResponse.SC_OK);

      GameResource game = getGameFromDatabase(id);

      debugOutput("-< Reply: " + gson.toJson(game));

      return gson.toJson(game);
    });

    // GET on a move resource
    get( "/lobby/game/:gameId/move/:moveId", (request, response) -> {
      debugOutput(request);
      String gameIdAsString = request.params(":gameId");
      // TODO: Handle non-integer provided as gameId
      Integer gameId = Integer.parseInt(gameIdAsString);

      String moveIdAsString = request.params(":moveId");
      // TODO: Handle non-integer provided as moveId
      Integer moveId = Integer.parseInt(moveIdAsString);

      // TODO: handle out of bounds and null values
      List<MoveResource> theMoveList = getMoveListForGame(gameId);
      MoveResource move = theMoveList.get(moveId);

      debugOutput("-< Reply: " + gson.toJson(move));

      response.status(HttpServletResponse.SC_OK);
      return gson.toJson(move);
    });

    // Update the move resource, i.e. making a transition in the game's state
    put( "/lobby/game/:gameId/move/:moveId", (request, response) -> {
      debugOutput(request);

      String gameIdAsString = request.params(":gameId");
      // TODO: Handle non-integer provided as path
      Integer gameId = Integer.parseInt(gameIdAsString);

      // Demarshall body
      String payload = request.body();
      MoveResource move = gson.fromJson(payload, MoveResource.class);

      // Update game resource with the new move
      GameResource game = getGameFromDatabase(gameId);
      MoveResource nextMove = makeAMove(game, move);

      // TODO: Implement logic to handle an invalid move
      // Simply return the same object as PUT
      debugOutput("-< Reply: " + gson.toJson(nextMove));
      return gson.toJson(nextMove);
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

  // === Domain handling of Game and Move resources
  private GameResource theOneGameOurServerHandles;
  private List<MoveResource> moveResourceList;
  private int createGameResourceAndInsertIntoDatabase(FutureGameResource fgame) {
    // Fake it code - we only handle a single game instance with id = 77;
    int theId = 77;
    theOneGameOurServerHandles =
            new GameResource( fgame.getPlayerOne(), fgame.getPlayerTwo(),
                    fgame.getLevel(), theId);

    // Create first move resource
    moveResourceList = new ArrayList<>();
    MoveResource move = new MoveResource("null", "null", "null");
    moveResourceList.add(move);
    return theId;
  }

  private GameResource getGameFromDatabase(int id) {
    // Fake-it - only one game instance handled
    return theOneGameOurServerHandles;
  }


  private MoveResource makeAMove(GameResource game, MoveResource move) {
    MoveResource moveResource = game.makeAMove(move);
    return moveResource;
  }

  private List<MoveResource> getMoveListForGame(Integer id) {
    // Fake-it - only one move list implemented
    return moveResourceList;
  }



  public void stop() {
    spark.Spark.stop();
  }

  private void debugOutput(String s) {
    // Enable the printing below to review the request/reply
    // of the GameLobby REST server.

    // System.out.println(s);
  }

  private void debugOutput(Request request) {
    debugOutput("-> " + request.requestMethod() + " " + request.pathInfo()
            + ": " + request.body().toString());
  }

}
