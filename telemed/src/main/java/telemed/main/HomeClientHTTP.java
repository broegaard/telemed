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

import java.io.*;

import frds.broker.ClientRequestHandler;
import frds.broker.ipc.http.UriTunnelClientRequestHandler;
import telemed.ipc.http.Constants;

/**
 * A shell based home client prototype. Just sends a single tele observation
 * to the server side for processing and storing. Uses URI Tunneled HTTP
 * for upload. Server hardwired to port 4567.
 * 
 * @author Henrik Baerbak Christensen, Aarhus University
 *
 */
public class HomeClientHTTP extends HomeClientTemplate {

  public HomeClientHTTP(String[] args, int port) throws IOException {
    super(args, port);
  }

  @Override
  public ClientRequestHandler createClientRequestHandler(String hostname, int port) {
    return new UriTunnelClientRequestHandler(hostname, port, Constants.BLOODPRESSURE_PATH);
  }

  public static void main(String[] args) throws IOException {
    new HomeClientHTTP(args, 4567);
  }
}
