package gamelobby.client;

import com.mashape.unirest.http.exceptions.UnirestException;
import gamelobby.server.GameLobbyRestServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import javax.servlet.http.HttpServletResponse;

import com.mashape.unirest.http.*;

/** Test the GameLobby system using a REST based architectural pattern.
 * The test cases expressed reflect those of Section 7.10 in the
 * Flexible, Reliable, Distributed Software book.
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
    // The 'Create Remote Game' payload to be POSTED by Pedersen...
    String body = "{ player : Pedersen, level : 0 }";
    JsonNode postLobbyBody = new JsonNode(body);

    // System.out.println("Posting on /lobby the payload: " + postLobbyBody.toString());

    // Make the POST
    HttpResponse<JsonNode> reply =
            Unirest.post(ROOT_URI + "/lobby").
                    body(postLobbyBody).
                    asJson();

    // Validate returned resource
    assertThat(reply.getStatus(), is(HttpServletResponse.SC_CREATED));
    assertThat(reply.getHeaders().getFirst("Location"), is("localhost:" + PORTNUMBER + "/lobby/42"));
    JsonNode json = reply.getBody();
    assertThat(json.getObject().getString("playerOne"), is("Pedersen"));
    System.out.println("---> " + json.toString());
    assertThat(json.getObject().getString("playerTwo"), is("null"));
    assertThat(reply.getBody().getObject().getInt("level"), is(0));
    assertThat(reply.getBody().getObject().getBoolean("available"), is(false));
    assertThat(reply.getBody().getObject().getString("next"), is("null"));


    // Pedersen tests that Findus has not joined yet.

  }
}
