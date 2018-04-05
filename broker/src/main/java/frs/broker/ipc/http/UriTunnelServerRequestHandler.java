package frs.broker.ipc.http;

import static spark.Spark.*;

import com.google.gson.Gson;

import frs.broker.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** ServerRequestHandler implementation using HTTP and URI Tunneling.
 * NOTE! Spark-Java likes static methods too much which are
 * contradictionary to flexible delegation based designs as
 * their bindings are made too early. You therefore have to
 * A) Instantiate the SRH and next B) invoke the srh.registerRoutes()
 * method.
 * <p>
 * Implementation based on the Spark-Java framework.

   This source code is from the book 
     "Flexible, Reliable Software:
       Using Patterns and Agile Development"
     published by CRC Press.
   Author: 
     Henrik B Christensen 
     Department of Computer Science
     Aarhus University
   
   Please visit http://www.baerbak.com/ for further information.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
 
       http://www.apache.org/licenses/LICENSE-2.0
 
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
public class UriTunnelServerRequestHandler
        implements ServerRequestHandler {

  protected final Gson gson;
  protected final Invoker invoker;
  protected final int port;
  protected int lastStatusCode;
  protected String lastVerb;
  private final String tunnelRoute;
  private final Logger logger;


  public UriTunnelServerRequestHandler(Invoker invoker,
                                       int port, String tunnelRoute) {
    this.invoker = invoker;
    this.port = port;
    this.tunnelRoute = tunnelRoute;
    gson = new Gson();

    logger = LoggerFactory.getLogger(UriTunnelServerRequestHandler.class);
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
      RequestObject p = gson.fromJson(body, RequestObject.class);
      logger.trace("--> Received request: " + p);

      ReplyObject reply = invoker.handleRequest(p.getObjectId(),
              p.getOperationName(), p.getPayload());

      // Store the last verb and status code to allow spying during test
      lastVerb = req.requestMethod();
      lastStatusCode = reply.getStatusCode();
      
      res.status(reply.getStatusCode());
      res.type(MimeMediaType.APPLICATION_JSON);

      logger.trace("--< Reply: " + reply);

      return gson.toJson(reply);
    });
  }

  @Override
  public void stop() {
    
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
