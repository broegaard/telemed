package frs.broker.ipc.http;

import com.google.gson.Gson;
import com.mashape.unirest.http.*;
import com.mashape.unirest.http.exceptions.UnirestException;

import frs.broker.*;

/**
 * ClientRequestHandler implementation using HTTP as pure IPC
 * also known as URI Tunneling. Based upon the Unirest framework.
 */
public class UriTunnelClientRequestHandler
        implements ClientRequestHandler {

  private final Gson gson;
  private final String baseURL;
  private final String path;

  /** Construct a URI Tunnel based CRH. Will communicate
   * using POST messages over http://(hostname):(port)/(pathForPost)
   *
   * @param hostname name of the machine that hosts the HTTP server
   * @param port port number of the HTTP server
   * @param pathForPost the path for the POST messages
   */
  public UriTunnelClientRequestHandler(String hostname, int port, String pathForPost) {
    baseURL = "http://" + hostname + ":" + port + "/";
    path = pathForPost;
    gson = new Gson();
  }

  @Override
  public ReplyObject sendToServer(RequestObject requestObject) {
    HttpResponse<JsonNode> jsonResponse = null;
    ReplyObject reply = null;

    // The request object's payload does NOT include operationName
    // and as we use HTTP as a pure transport protocol, we need
    // to create a more complete request object which includes
    // the full set of data required
    String requestAsJson = gson.toJson(requestObject);
    
    // All calls are URI tunneled through a POST message
    try {
      jsonResponse = Unirest.post(baseURL + path)
          .header("Accept", MimeMediaType.APPLICATION_JSON)
          .header("Content-Type", MimeMediaType.APPLICATION_JSON)
          .body(requestAsJson).asJson();
    } catch (UnirestException e) {
      throw new IPCException("UniRest POST request failed on objId="
          + requestObject.getObjectId() + ", operationName="
              + requestObject.getOperationName(), e);
    }
    
    String body = jsonResponse.getBody().toString();
    reply = gson.fromJson(body, ReplyObject.class);
    return reply;
  }

  @Override
  public void close() {
    // Not applicable for a HTTP connection.
  }

}
