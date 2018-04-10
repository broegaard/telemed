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

package telemed.main;

import frds.broker.Invoker;
import frds.broker.ipc.http.UriTunnelServerRequestHandler;

import telemed.server.*;
import telemed.domain.TeleMed;
import telemed.doubles.*;
import telemed.ipc.http.TeleMedUriTunnelServerRequestHandler;
import telemed.marshall.json.TeleMedJSONInvoker;
import telemed.storage.XDSBackend;

/** Jetty/Spark-java based server responding to URI Tunneled POST
 * uploads and GET requests. The server is hardwired to port 4567.
 *
 * @author Henrik Baerbak Christensen, Aarhus University
 *
 */
public class ServerMainHTTP {
  
  public static void main(String[] args) throws Exception {
    // Command line argument parsing and validation
    if (args.length < 1) {
      explainAndDie();
    }
    new ServerMainHTTP(args[0]); // No error handling!
  }
  
  private static void explainAndDie() {
    System.out.println("Usage: ServerMainHTTP {db}");
    System.out.println("       db = 'memory' is the only type DB allowed");
    System.exit(-1);
  }

  public ServerMainHTTP(String type) {
    int port = 4567;
    // Define the server side delegates
    XDSBackend xds = null;
    if (type.equals("memory")) {
      xds = new FakeObjectXDSDatabase();
    } else {
      // Open for other implementations, connecting to real
      // databases. Contact Henrik Baerbak if you want to
      // try other DBs.
      System.out.println("Sorry - only memory based DB supported.");
      System.exit(0);
    }
    // Create server side implementation of Broker roles
    TeleMed tsServant = new TeleMedServant(xds);
    Invoker invoker = new TeleMedJSONInvoker(tsServant);

    UriTunnelServerRequestHandler srh =
        new TeleMedUriTunnelServerRequestHandler(invoker, port, xds);
    srh.start();

    // Welcome
    System.out.println("=== TeleMed Spark based Server Request Handler (port:"
            + port + ") ===");
    System.out.println(" Use ctrl-c to terminate!"); 
  }
}
