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

package frds.broker.ipc.socket;

import java.io.*;
import java.net.*;

import com.google.gson.Gson;

import frds.broker.Invoker;
import frds.broker.ReplyObject;
import frds.broker.RequestObject;
import frds.broker.ServerRequestHandler;

/**
 * Implementation of the Server Request Handler using
 * simple sockets for IPC. It is a Runnable and the main
 * method of the server must create a thread that has
 * an instance to support receiving network requests.
 * <p>
 * The current implementation is pretty verbose
 * for learning purposes.
 */
public class SocketServerRequestHandler
        implements Runnable, ServerRequestHandler {
  private int port;
  private ServerSocket serverSocket = null;
  private Invoker invoker = null;
  private final Gson gson;

  /** Construct a socket based server request handler.
   * Remember to set the invoker delegate and port before
   * starting the process.
   */
  public SocketServerRequestHandler() {
    gson = new Gson();
  }

  @Override
  public void setPortAndInvoker(int port, Invoker invoker) {
    this.port = port;
    this.invoker = invoker;
  }

  public SocketServerRequestHandler(int port, Invoker invoker) {
    this();
    setPortAndInvoker(port, invoker);
  }

  private boolean isStopped;

  @Override
  public void run() {
    openServerSocket();

    System.out.println("*** Server socket established ***");
    
    isStopped = false;
    while (!isStopped) {

      System.out.println("--> Accepting...");
      Socket clientSocket = null;
      try {
        clientSocket = serverSocket.accept();
      } catch(IOException e) {
        if(isStopped) {
          System.out.println("Server Stopped.") ;
          return;
        }
        throw new RuntimeException(
            "Error accepting client connection", e);
      }

      try {
        readMessageAndDispatch(clientSocket);
      } catch (IOException e) {
        System.out.println("ERROR: IOException encountered: "
                + e.getMessage());
      }
    }
    System.out.println("Server Stopped.");
  }

  private void readMessageAndDispatch(Socket clientSocket)
          throws IOException {
    PrintWriter out =
            new PrintWriter(clientSocket.getOutputStream(), true);
    BufferedReader in = new BufferedReader(new InputStreamReader(
        clientSocket.getInputStream()));

    String inputLine;
    ReplyObject reply = null;

    inputLine = in.readLine();
    System.out.println("--> Received " + inputLine);
    if (inputLine == null) {
      System.err.println(
              "Server read a null string from the socket???");
    } else {
      RequestObject p =
              gson.fromJson(inputLine, RequestObject.class);
      reply = invoker.handleRequest(p.getObjectId(),
              p.getOperationName(), p.getPayload());

      System.out.println("--< replied: " + reply);
    }
    String replyAsString = gson.toJson(reply);
    out.println(replyAsString);

    System.out.println("Closing socket...");
    in.close();
    out.close();
  }
  
  private void openServerSocket() {
    try {
      this.serverSocket = new ServerSocket(this.port);
      System.out.println("Socket accepting on port: "
              + port);
    } catch (IOException e) {
      System.out.println("Failed to open server socket at port "
              + port);
      System.exit(-1);
    } 
  }

  private Thread daemon;
  @Override
  public void start() {
    daemon = new Thread(this);
    daemon.start();
  }

  @Override
  public void stop() {
    isStopped = true; // Will only stop after next message...
  }

  @Override
  public String toString() {
    return getClass().getCanonicalName() + ", port " + port;
  }
}
