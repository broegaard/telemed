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
    String body = "{ player : Pedersen, level : 0 }";
    // the mashape json library will add the missing quotes in the above body...
    JsonNode postLobbyBody = new JsonNode(body);

    // Make the POST
    reply = Unirest.post(ROOT_URI + "/lobby").
                    body(postLobbyBody).
                    asJson();

    // Validate returned resource is the uninitialized future game resource
    // from §7.10 in FRDS.
    assertThat(reply.getHeaders().getFirst("Location"), is("localhost:" + PORTNUMBER + "/lobby/42"));
    assertThatReplyIsInitialFutureGameWithStatus(reply, HttpServletResponse.SC_CREATED);

    // Pedersen tests that Findus has not joined yet.
    reply = Unirest.get(ROOT_URI + "/lobby/42").
            asJson();
    assertThatReplyIsInitialFutureGameWithStatus(reply, HttpServletResponse.SC_OK);
  }

  private void executeStory2_JoiningAnExistingGame() throws UnirestException {
    HttpResponse<JsonNode> reply;
    String body = "{ playerTwo : Findus }";
    // Make the PUT
    reply = Unirest.put(ROOT_URI + "/lobby/42").
            body(body).
            asJson();

    System.out.println(" --PUT -> " + reply.getBody().getObject().toString());
    assertThatReplyIsInitalizedFutureGameWithStatus(reply, HttpServletResponse.SC_OK);
  }

  private void executeStory3_PlayingTheGame() throws UnirestException {
    HttpResponse<JsonNode> reply;
    reply = Unirest.get(ROOT_URI + "/lobby/game/77").
            asJson();

    assertThatReplyIsGameResourceWithGivenPlayerInTurn(reply, "Pedersen");

  }

  private void assertThatReplyIsGameResourceWithGivenPlayerInTurn(HttpResponse<JsonNode> reply, String playerInTurn) {
    assertThat(reply.getStatus(), is(HttpServletResponse.SC_OK));
    JsonNode json = reply.getBody();

    assertThat(json.getObject().getString("playerOne"), is("Pedersen"));
    assertThat(json.getObject().getString("playerTwo"), is("Findus"));
    assertThat(reply.getBody().getObject().getInt("level"), is(0));
    assertThat(reply.getBody().getObject().getString("playerInTurn"), is(playerInTurn));
    assertThat(reply.getBody().getObject().getString("next"), is("/lobby/game/move/77"));

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
