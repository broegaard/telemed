package gamelobby.client;


import gamelobby.server.GameLobbyRestServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import javax.servlet.http.HttpServletResponse;

import com.mashape.unirest.http.*;
import com.mashape.unirest.http.exceptions.UnirestException;

/** Test the GameLobby system using a REST based architectural pattern.
 * The test cases expressed reflect those of Section 7.10 in the
 * Flexible, Reliable, Distributed Software book.
 *
 * Note: This is the TDD process of a DEMONSTRATION ONLY game lobby
 * REST server, and a lot of assumptions are made on the internal
 * working of this server - one notably aspect is that the
 * returned ID of the futuregame resource is hardcoded to 42,
 * and the game id to 77 in the server.
 *
 * Also there is ABSOLUTELY no error checking code in the server
 * which of course is not realistic, but suffice as the test
 * scenarios exercised match closely those in the FRDS book
 * and the code here should only reflect the actual HATEOAS
 * state machinery of REST systems.
 *
 */
public class TestClientScenario {

  public static int PORTNUMBER = 5567;
  public static final String ROOT_URI = "http://localhost:" + PORTNUMBER;

  public static GameLobbyRestServer server = null;

  @BeforeClass
  public static void setupGameLobbyRestServer() {
    server = new GameLobbyRestServer(PORTNUMBER);
  }

  @AfterClass
  public static void tearDownServer() {
    server.stop();
  }

  @Test
  public void shouldVerifyProtocolOfSection7_10_inFRDS() throws UnirestException {
    executeStory1_CreatingARemoteGame();
    executeStory2_JoiningAnExistingGame();
    executeStory3_PlayingTheGame();
  }

  private void executeStory1_CreatingARemoteGame() throws UnirestException {
    HttpResponse<JsonNode> reply;
    // The 'Create Remote Game' payload to be POSTED by Pedersen...
    String body = "{ playerOne : Pedersen, level : 0 }";
    // the mashape json library will add the missing quotes in the above body...
    JsonNode postLobbyBody = new JsonNode(body);

    // Make the POST
    reply = Unirest.post(ROOT_URI + "/lobby").
                    body(postLobbyBody).
                    asJson();

    // Validate returned resource is the uninitialized future game resource
    // from §7.10 in FRDS.
    assertThat(reply.getHeaders().getFirst("Location"),
            is("localhost:" + PORTNUMBER + "/lobby/42"));
    assertThatReplyIsInitialFutureGameWithStatus(reply, HttpServletResponse.SC_CREATED);

    // Pedersen tests that Findus has not joined yet.
    reply = Unirest.get(ROOT_URI + "/lobby/42").
            asJson();
    assertThatReplyIsInitialFutureGameWithStatus(reply, HttpServletResponse.SC_OK);
  }

  private void executeStory2_JoiningAnExistingGame() throws UnirestException {
    HttpResponse<JsonNode> reply;
    String body = "{ playerTwo : Findus }";
    JsonNode postBody = new JsonNode(body);
    // Make the PUT
    reply = Unirest.post(ROOT_URI + "/lobby/42").
            body(postBody).
            asJson();

    // System.out.println(" -- JOIN GAME - PUT -> " + reply.getBody().getObject().toString());
    assertThatReplyIsInitalizedFutureGameWithStatus(reply, HttpServletResponse.SC_OK);
  }

  private void executeStory3_PlayingTheGame() throws UnirestException {
    HttpResponse<JsonNode> reply;

    // READ the game resource
    reply = Unirest.get(ROOT_URI + "/lobby/game/77").
            asJson();

    // Assert that no moves have been made and that Pedersen is in turn.
    assertThatReplyIsGameResourceWithGivenPlayerInTurn(reply, "Pedersen");
    assertThat(reply.getBody().getObject().getInt("noOfMovesMade"), is(0));
    // And that a move resource has been returned as 'next' link
    String nextLink = reply.getBody().getObject().getString("next");
    assertThat(nextLink, is("/lobby/game/77/move/0"));

    // GET the move resource - which as it does not represent a move in the game - is all null values
    reply = Unirest.get(ROOT_URI + nextLink).asJson();

    assertThat(reply.getStatus(), is(HttpServletResponse.SC_OK));
    assertThat(reply.getBody().getObject().getString("from"), is("null"));

    // === Make a Second Game Move by updating/PUT this resource
    String body = "{ player : Pedersen, from : e2, to : e4 }";
    JsonNode postBody = new JsonNode(body);
    reply = Unirest.put(ROOT_URI + "/lobby/game/77/move/0").
            body(postBody).
            asJson();

    assertThat(reply.getBody().getObject().getString("from"), is("e2"));

    // READ the game resource again and test that Findus is in turn
    reply = Unirest.get(ROOT_URI + "/lobby/game/77").
            asJson();

    assertThatReplyIsGameResourceWithGivenPlayerInTurn(reply, "Findus");
    assertThat(reply.getBody().getObject().getInt("noOfMovesMade"), is(1));

    // And that the next pointer is now pointing to the next move resource
    assertThat(reply.getBody().getObject().getString("next"), is("/lobby/game/77/move/1"));


    /*
    String body = "{ player : Pedersen, from : e2, to : e4 }";
    JsonNode postBody = new JsonNode(body);
    reply = Unirest.put(ROOT_URI + "/lobby/game/77/move/0").
            body(postBody).
            asJson();

    assertThat(reply.getBody().getObject().getString("from"), is("e2"));

    // READ the game resource again and test that Findus is in turn
    reply = Unirest.get(ROOT_URI + "/lobby/game/77").
            asJson();

    assertThatReplyIsGameResourceWithGivenPlayerInTurn(reply, "Findus");
    assertThat(reply.getBody().getObject().getInt("noOfMovesMade"), is(1));

    // And that the next pointer is now pointing to the next move resource
    assertThat(reply.getBody().getObject().getString("next"), is("/lobby/game/77/move/1"));*/
  }

  private void assertThatReplyIsGameResourceWithGivenPlayerInTurn(HttpResponse<JsonNode> reply, String playerInTurn) {
    assertThat(reply.getStatus(), is(HttpServletResponse.SC_OK));
    JsonNode json = reply.getBody();

    assertThat(json.getObject().getString("playerOne"), is("Pedersen"));
    assertThat(json.getObject().getString("playerTwo"), is("Findus"));
    assertThat(reply.getBody().getObject().getInt("level"), is(0));
    assertThat(reply.getBody().getObject().getString("playerInTurn"), is(playerInTurn));
    assertThat(reply.getBody().getObject().getString("next"), containsString("/lobby/game/77/move/"));
  }

  private void assertThatReplyIsInitalizedFutureGameWithStatus(HttpResponse<JsonNode> reply, int statusCode) {
    assertThat(reply.getStatus(), is(statusCode));
    JsonNode json = reply.getBody();

    assertThat(json.getObject().getString("playerOne"), is("Pedersen"));
    assertThat(json.getObject().getString("playerTwo"), is("Findus"));
    assertThat(reply.getBody().getObject().getInt("level"), is(0));
    assertThat(reply.getBody().getObject().getBoolean("available"), is(true));
    assertThat(reply.getBody().getObject().getString("next"), is("/lobby/game/77"));
  }

  private void assertThatReplyIsInitialFutureGameWithStatus(HttpResponse<JsonNode> reply, int statusCode) {
    assertThat(reply.getStatus(), is(statusCode));
    JsonNode json = reply.getBody();

    assertThat(json.getObject().getString("playerOne"), is("Pedersen"));
    assertThat(json.getObject().getString("playerTwo"), is("null"));
    assertThat(reply.getBody().getObject().getInt("level"), is(0));
    assertThat(reply.getBody().getObject().getBoolean("available"), is(false));
    assertThat(reply.getBody().getObject().getString("next"), is("null"));
  }
}
