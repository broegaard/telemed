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

    // Create Remote Game
    post("/lobby", (req, response) ->
    {
      // Demarshall body
      String payload = req.body();
      JsonNode asNode = new JsonNode(payload);
      String playerName = asNode.getObject().getString("player");
      Integer level = asNode.getObject().getInt("level");

      // Call 'domain' code to create the future game
      int futureGameId = createFutureGameAndInsertIntoDatabase(playerName, level);
      FutureGameResource fgame = database.get(futureGameId);

      // And construct the response of the POST
      response.status(HttpServletResponse.SC_CREATED);
      response.header("Location",
              req.host() + "/lobby/" + futureGameId);
      return gson.toJson(fgame);
    });

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
