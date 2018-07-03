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

package saip.main;

import frds.broker.Invoker;
import frds.broker.ipc.http.UriTunnelServerRequestHandler;

import telemed.server.*;
import telemed.domain.TeleMed;
import telemed.doubles.*;
import telemed.ipc.http.TeleMedUriTunnelServerRequestHandler;
import telemed.marshall.json.TeleMedJSONInvoker;
import telemed.storage.XDSBackend;

import saip.storage.mongo.MongoXDSAdapter;

/** Jetty/Spark-java based server responding to URI Tunneled POST
 * uploads and GET requests. The server is hardwired to port 4567.
 *
 * This variant is made for SAiP courses.
 *
 * @author Henrik Baerbak Christensen, Aarhus University
 *
 */
public class ServerMainHTTP {
  
  public static void main(String[] args) throws Exception {
    // Command line argument parsing and validation
    if (args.length < 2) {
      explainAndDie();
    }
    new ServerMainHTTP(args[0], args[1]); // No error handling!
  }
  
  private static void explainAndDie() {
    System.out.println("Usage: ServerMainHTTP {db}");
    System.out.println("       db = 'memory' is the in-memory db");
    System.out.println("       db = {host} is MongoDB on 'host:27017'");
    System.out.println("       pehack = 'true'/'false'; if 'true' then client timestamp is overwritten");
    System.exit(-1);
  }

  public ServerMainHTTP(String type, String PEHackEnabled) {
    int port = 4567;
    // Define the server side delegates
    XDSBackend xds = null;
    if (type.equals("memory")) {
      xds = new FakeObjectXDSDatabase();
    } else {
      xds = new MongoXDSAdapter(type, 27017);
    }
    // Create the TeleMed servant
    TeleMed tsServant = new TeleMedServant(xds);
    if (PEHackEnabled.equals("true")) {
      // To avoid changing the general TeleMed
      // implementation, we use a Decorator
      // pattern to change the behavior of
      // timestamping a bit to introduce the
      // Performance Enginering hack
      tsServant = new PEHackDecorator(tsServant);
    }
    // Create server side implementation of Broker roles
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
