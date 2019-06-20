/*
 * Copyright (C) 2018 Henrik Bærbak Christensen, baerbak.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package frds.broker.ipc.http;

import static spark.Spark.*;

import com.google.gson.Gson;

import frds.broker.Invoker;
import frds.broker.ReplyObject;
import frds.broker.RequestObject;
import frds.broker.ServerRequestHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** ServerRequestHandler implementation using HTTP and URI Tunneling.
 * <p>
 * Implementation based on the Spark-Java framework.
 *
 */

// Note - the logging level for incoming/outgoing messages is set to INFO
// which is too talkative; but set so for teaching purposes
public class UriTunnelServerRequestHandler
        implements ServerRequestHandler {

  public static final String DEFAULT_URI_TUNNEL_PATH = "tunnel";

  protected final Gson gson;
  protected Invoker invoker;
  protected int port;
  protected int lastStatusCode;
  protected String lastVerb;
  protected String tunnelRoute;
  protected final Logger logger;

  /** Create a URI Tunnel based server request handler,
   * defaulting to path '/tunnel'. Remember to set port
   * and Invoker before starting the server process.
   */
  public UriTunnelServerRequestHandler() {
    gson = new Gson();
    logger = LoggerFactory.getLogger(UriTunnelServerRequestHandler.class);
    tunnelRoute = DEFAULT_URI_TUNNEL_PATH;
  }
  @Override
  public void setPortAndInvoker(int port, Invoker invoker) {
    this.port = port; this.invoker = invoker;
  }

  public UriTunnelServerRequestHandler(Invoker invoker,
                                       int port, String tunnelRoute) {
    this();
    setPortAndInvoker(port, invoker);
    this.tunnelRoute = tunnelRoute;
  }

  @Override
  public void start() {
    // Set the port to listen to
    port(port);

    // POST is for all incoming requests
    post(tunnelRoute, (req,res) -> {
      String body = req.body();
      
      // The incoming body is a full request
      // object to be demarshalled
      RequestObject requestObject = gson.fromJson(body, RequestObject.class);
      //logger.info("--> Received request: " + requestObject);
      logger.info("action=request, requestObject=\"{}\"", requestObject);

      ReplyObject reply = invoker.handleRequest(requestObject.getObjectId(),
              requestObject.getOperationName(), requestObject.getPayload());

      // Store the last verb and status code to allow spying during test
      lastVerb = req.requestMethod();
      lastStatusCode = reply.getStatusCode();
      
      res.status(reply.getStatusCode());
      res.type(MimeMediaType.APPLICATION_JSON);

      logger.info("action=reply, replyObject=\"{}\"", reply);

      return gson.toJson(reply);
    });
  }

  @Override
  public void stop() {
    spark.Spark.stop();
  }

  @Override
  public String toString() {
    return getClass().getCanonicalName() + ", port " + port +
        ", root path: '" + tunnelRoute + "'";
  }

  /**
   * Return status code of last operation. A test retrieval interface.
   * 
   * @return last status code
   */
  public int lastStatusCode() {
    return lastStatusCode;
  }

  public String lastHTTPVerb() {
    return lastVerb;
  }

}
