/*
 * Copyright (C) 2018-2021. Henrik Bærbak Christensen, Aarhus University.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package frds.broker.ipc.http;

import com.google.gson.Gson;
import com.mashape.unirest.http.*;
import com.mashape.unirest.http.exceptions.UnirestException;

import frds.broker.ClientRequestHandler;
import frds.broker.IPCException;
import frds.broker.ReplyObject;
import frds.broker.RequestObject;

/**
 * ClientRequestHandler implementation using HTTP as pure IPC
 * also known as URI Tunneling. Based upon the Unirest framework.
 */
public class UriTunnelClientRequestHandler
        implements ClientRequestHandler {

  public static final String PROTOCOL = "http";

  private final Gson gson;
  protected String baseURL;
  protected final String path;

  /** Construct a URI Tunnel based CRH. Will communicate
   * using POST messages over http://(hostname):(port)/(pathForPost)
   *
   * @param hostname name of the machine that hosts the HTTP server
   * @param port port number of the HTTP server
   * @param pathForPost the path for the POST messages
   */
  public UriTunnelClientRequestHandler(String hostname, int port, String pathForPost) {
    baseURL = PROTOCOL + "://" + hostname + ":" + port + "/";
    path = pathForPost;
    gson = new Gson();
  }

  /**
   * Construct a URI Tunnel based CRH. Will communicate
   * using POST messages over http://localhost:4567/tunnel.
   * Remember to call setServer before the first invocation
   * to rewire to another server.
   */

  public UriTunnelClientRequestHandler() {
    baseURL = PROTOCOL + "://localhost:4567/";
    path = "tunnel";
    gson = new Gson();
  }

  @Override
  public void setServer(String hostname, int port) {
    baseURL = PROTOCOL + "://" + hostname + ":" + port + "/";
}

  @Override
  public String sendToServerAndAwaitReply(String request) {
    HttpResponse<String> reply;

    // All calls are URI tunneled through a POST message
    try {
      reply = Unirest.post(baseURL + path)
              .header("Accept", MimeMediaType.TEXT_PLAIN)
              .header("Content-Type", MimeMediaType.TEXT_PLAIN)
              .body(request).asString();
    } catch (UnirestException e) {
      throw new IPCException("UniRest POST request failed on request="
              + request, e);
    }
    return reply.getBody();
  }

  @Override
  public void close() {
    // Not applicable for a HTTP connection.
  }

  @Override
  public String toString() {
    return getClass().getCanonicalName() +
        ", " + baseURL + ", root path: '" + path + "'";
  }

}
