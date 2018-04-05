package telemed.main;

import java.io.*;

import frs.broker.*;
import frs.broker.ipc.http.UriTunnelClientRequestHandler;
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
