/*
 * Copyright (C) 2018 - 2021. Henrik Bærbak Christensen, Aarhus University.
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
import frds.broker.ServerRequestHandler;

import frds.broker.ipc.SSLPropertyConstants;
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
  private boolean useTls;
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
    logger.info("method=setPortAndInvoker, port={}", port);
    this.port = port; this.invoker = invoker;
  }

  /**
   * Construct a full URI Tunnel SRH with given invoker, port, and
   * tunnel route. Optionally allow TLS/HTTPS communication (which
   * requires keystore to be defined.)
   * @param invoker the Broker invoker to forward incoming messages to
   * @param port the port for listening to incoming messages
   * @param useTls if true, switch to HTTPS communication (see additional
   *               README for some info on how this works.)
   * @param tunnelRoute the route/path to listen to
   */
  public UriTunnelServerRequestHandler(Invoker invoker, int port, boolean useTls, String tunnelRoute) {
    this();
    setPortAndInvoker(port, invoker);
    this.tunnelRoute = tunnelRoute;
    this.useTls = useTls;
    logger.info("method=constructur, port={}, uri_tunnel_path={}, tls={}", port, this.tunnelRoute, useTls);
  }

  /**
   * Construct a full URI Tunnel SRH with given invoker, port, and
   * tunnel route. O
   * @param invoker the Broker invoker to forward incoming messages to
   * @param port the port for listening to incoming messages
   * @param tunnelRoute the route/path to listen to
   */
  public UriTunnelServerRequestHandler(Invoker invoker,
                                       int port, String tunnelRoute) {
    this(invoker, port, false, tunnelRoute);
  }

  @Override
  public void start() {
    // Set the port to listen to
    port(port);

    // if required to use TLS then get the system properties and secure the connection
    String keystoreFilename = "undefined";
    if (useTls) {
      // Get the System Properties
      keystoreFilename = System.getProperty(SSLPropertyConstants.JAVAX_NET_SSL_KEYSTORE);
      String keystorePassword = System.getProperty(SSLPropertyConstants.JAVAX_NET_SSL_KEYSTORE_PASSWORD);
      secure(keystoreFilename, keystorePassword, null, null);
    }

    logger.info("method=start, port={}, tls={}, keystore='{}'",
            port, useTls, keystoreFilename);

    // POST is for all incoming requests, and they are plain text
    // format as we cannot know the marshalling format in advance
    post(tunnelRoute, MimeMediaType.TEXT_PLAIN, (req, res) -> {
      long startTime = System.currentTimeMillis();
      String marshalledRequest = req.body();
      
      logger.info("method=POST, context=request, request={}", marshalledRequest);

      // The incoming marshalledRequest is the marshalled request to the invoker
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
