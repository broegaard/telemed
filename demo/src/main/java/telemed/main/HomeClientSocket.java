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

import java.io.*;

import frds.broker.ClientRequestHandler;
import frds.broker.ipc.socket.SocketClientRequestHandler;

/**
 * A shell based home client prototype. Configured for the
 * Socket based client request handler. Hardwired to
 * a server running at port 37321.
 * 
 * @author Henrik Baerbak Christensen, Aarhus University
 *
 */
public class HomeClientSocket extends HomeClientTemplate {
  public HomeClientSocket(String[] args, int port) throws IOException {
    super(args, port);
  }

  @Override
  public ClientRequestHandler createClientRequestHandler(String hostname, int port) {
    ClientRequestHandler crh = new SocketClientRequestHandler();
    crh.setServer(hostname, port);
    return new SocketClientRequestHandler(hostname, port);
  }

  public static void main(String[] args) throws IOException {
    new HomeClientSocket(args, 37321);
  }

}
