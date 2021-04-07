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

package telemed.main;

import telemed.domain.TeleMed;
import telemed.doubles.FakeObjectXDSDatabase;
import telemed.rest.RESTServerRequestHandlerInvoker;
import telemed.server.TeleMedServant;
import telemed.storage.XDSBackend;

/** Jetty/Spark-java based server responding to REST calls.
 * 
 * @author Henrik Baerbak Christensen, Aarhus University
 *
 */
public class ServerMainREST {
  
  public static void main(String[] args) throws Exception {
    if (args.length < 1) {
      explainAndDie();
    }
    new ServerMainREST(args[0]); // No error handling!
  }
  
  private static void explainAndDie() {
    System.out.println("Usage: ServerMainREST {port}");
    System.out.println("       port = port number for server to listen to");
    System.exit(-1);
  }

  public ServerMainREST(String portNo) throws Exception {
    // Define the server side delegates
    XDSBackend xds = null;
    xds = new FakeObjectXDSDatabase();
    TeleMed tsServant = new TeleMedServant(xds);

    // Configure the Spark-java servlet
    int port = Integer.parseInt(portNo);
    RESTServerRequestHandlerInvoker srh =
        new RESTServerRequestHandlerInvoker(port, tsServant, xds);
    srh.start();
    
    // Welcome 
    System.out.println("=== TeleMed Spark based REST Server Request Handler (port:"+port+") ===");
    System.out.println(" Use ctrl-c to terminate!"); 
  }
}
