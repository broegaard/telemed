package telemed.main;

import frs.broker.*;
import telemed.server.*;
import telemed.domain.TeleMed;
import telemed.doubles.*;
import frs.broker.ipc.socket.SocketServerRequestHandler;
import telemed.marshall.json.StandardJSONInvoker;
import telemed.storage.XDSBackend;

/** App server, using socket based implementations of broker roles.
 * The server is hardwired to port 37321.
 *
 * @author Henrik Baerbak Christensen, Aarhus University
 *
 */
public class ServerMainSocket {
  
  private static Thread daemon; 
  
  public static void main(String[] args) throws Exception {
    new ServerMainSocket(args[0]); // No error handling!
  }
  
  public ServerMainSocket(String type) throws Exception {
    int port = 37321;
    // Define the server side delegates
    XDSBackend xds = null;
    if (type.equals("memory")) {
      xds = new FakeObjectXDSDatabase();
    } else {
      // Open for other implementations, connecting to real
      // databases. Contact Henrik Baerbak for such.
      System.out.println("Sorry - only memory based DB supported.");
      System.exit(0);
    }

    TeleMed tsServant = new TeleMedServant(xds);
    Invoker invoker = new StandardJSONInvoker(tsServant);

    // Configure a socket based server request handler
    SocketServerRequestHandler ssrh =
            new SocketServerRequestHandler(port, invoker);
    
    // Welcome
    // Welcome
    System.out.println("=== TeleMed Socket based Server Request Handler (port:"
            + port + ") ===");
    System.out.println(" Use ctrl-c to terminate!");
    
    // and start the daemon...
    daemon = new Thread(ssrh); 
    daemon.start(); 
    
    // Ensure that its lifetime follows that of the main process
    daemon.join(); 
  }
}
