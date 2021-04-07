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

import static spark.Spark.*;

import com.google.gson.Gson;

import frds.broker.Invoker;
import frds.broker.ReplyObject;
import frds.broker.RequestObject;
import frds.broker.ServerRequestHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;

/** ServerRequestHandler implementation using HTTP and URI Tunneling.
 * <p>
 * Implementation based on the Spark-Java framework.
 *
 */

public class UriTunnelServerRequestHandler
        implements ServerRequestHandler {

  public static final String DEFAULT_URI_TUNNEL_PATH = "tunnel";

  protected final Gson gson;
  protected Invoker invoker;
  protected int port;
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
      long startTime = System.currentTimeMillis();
      String marshalledRequest = req.body();
      
      // The incoming marshalledRequest is the marshalled request to the invoker
      // Log the request, using a key-value format
      logger.info("method=POST, context=request, request={}", marshalledRequest);

      String reply = invoker.handleRequest(marshalledRequest);

      // Store the last verb and status code to allow spying during test
      lastVerb = req.requestMethod();

      // The reply is opaque - so we have no real chance of setting a proper
      // status code.
      res.status(HttpServletResponse.SC_OK);
      // Hmm, we also do not know the actual marshalling format but
      // just know it is textual
      res.type(MimeMediaType.TEXT_PLAIN);

      // response time in milliseconds for invoker upload is calculated
      long responseTime = System.currentTimeMillis() - startTime;
      logger.info("method=handleRequest, context=reply, reply={}, responseTime_ms={}",
              reply, responseTime);

      return reply;
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

  public String lastHTTPVerb() {
    return lastVerb;
  }

}
