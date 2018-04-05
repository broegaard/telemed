package telemed.main;

import java.io.*;

import frs.broker.*;
import frs.broker.ipc.socket.SocketClientRequestHandler;

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
    return new SocketClientRequestHandler(hostname, port);
  }

  public static void main(String[] args) throws IOException {
    new HomeClientSocket(args, 37321);
  }

}
