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

package frds.broker.ipc.socket;

import java.io.*;
import java.net.*;

import com.google.gson.Gson;

import frds.broker.ClientRequestHandler;
import frds.broker.IPCException;
import frds.broker.ReplyObject;
import frds.broker.RequestObject;
import frds.broker.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Implementation of the Client Request Handler using simple sockets.
 * As in HTTP protocol 1.0 days, the socket is opened and closed
 * on every call.
 */
public class SocketClientRequestHandler
        implements ClientRequestHandler {

  private String hostname;
  private int port;

  /** Create the CRH. Remember to use
   * 'setServer' before any sendToServer
   * calls.
   */

  public SocketClientRequestHandler() {
    Gson gson = new Gson();
  }

  @Override
  public void setServer(String hostname, int port, boolean useTLS) {
    if (useTLS) {
      throw new RuntimeException("TLS is not implemented for the SocketClientRequestHandler."
              + "If you need secure communication, use the URITunnel variant instead.");
    }
    this.hostname = hostname;
    this.port = port;
  }

  public SocketClientRequestHandler(String hostname, int port) {
    this();
    setServer(hostname, port);
  }

  @Override
  public String sendToServerAndAwaitReply(String request) {
    Socket clientSocket = null;

    // Create the socket connection to the host
    PrintWriter out;
    BufferedReader in;
    try {
      clientSocket = new Socket(hostname, port);
      out = new PrintWriter(clientSocket.getOutputStream(), true);
      in = new BufferedReader(new InputStreamReader(
              clientSocket.getInputStream()));
    } catch (IOException e ) {
      throw new IPCException("Socket creation problems", e);
    }

    // Send it to the server (= write it to the socket stream)
    out.println(request);

    // Block until a reply is received
    String reply;
    try {
      reply = in.readLine();

    } catch (IOException e) {
      throw new IPCException("Socket read problems", e);
    } finally {
      try {
        clientSocket.close();
      } catch (IOException e) {
        throw new IPCException("Socket close problems (1)", e);
      }
    }
    // ... and close the connection
    try {
      clientSocket.close();
    } catch (IOException e) {
      throw new IPCException("Socket close problems (2)", e);
    }

    return reply;
  }

  @Override
  public void close() {
    // Not applicable as the connection is created and torn down on
    // every request
  }

  @Override
  public String toString() {
    return getClass().getCanonicalName() +
        ", " + hostname + ':' + port;
  }
}
