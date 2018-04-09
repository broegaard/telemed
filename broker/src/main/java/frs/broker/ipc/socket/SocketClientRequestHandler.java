package frs.broker.ipc.socket;

import java.io.*;
import java.net.*;

import com.google.gson.Gson;

import frs.broker.*;

/**
 * Implementation of the Client Request Handler using simple sockets.
 * As in HTTP protocol 1.0 days, the socket is opened and closed
 * on every call.
 */
public class SocketClientRequestHandler
        implements ClientRequestHandler {

  private final String hostname;
  private final int port;
  private PrintWriter out;
  private BufferedReader in;
  private final Gson gson;

  public SocketClientRequestHandler(String hostname, int port) {
    this.hostname = hostname;
    this.port = port;
    gson = new Gson();
  }

  @Override
  public ReplyObject sendToServer(RequestObject requestObject) {
    Socket clientSocket = null;
    ReplyObject replyObj = null;
    
    // Create the socket connection to the host
    try {
      clientSocket = new Socket(hostname, port);
      out = new PrintWriter(clientSocket.getOutputStream(), true);
      in = new BufferedReader(new InputStreamReader(
          clientSocket.getInputStream()));
    } catch (IOException e ) {
      throw new IPCException("Socket creation problems", e);
    }

    // Create and marshal the request object 
    String onthewireRequestObject = gson.toJson(requestObject);
    
    // Send it to the server (= write it to the socket stream)
    out.println(onthewireRequestObject);

    // Block until a reply is received
    String reply;
    try {
      reply = in.readLine();
      
      // and demarshall it into a reply object
      replyObj = gson.fromJson(reply, ReplyObject.class);
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
    return replyObj;
  }

  @Override
  public void close() {
    // Not applicable as the connection is created and torn down on
    // every request
  }

}
