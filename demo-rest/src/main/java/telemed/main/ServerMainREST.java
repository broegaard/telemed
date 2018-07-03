package telemed.main;

import telemed.domain.TeleMed;
import telemed.doubles.FakeObjectXDSDatabase;
import telemed.rest.RESTServerRequestHandlerInvoker;
import telemed.server.TeleMedServant;
import telemed.storage.XDSBackend;

/** Jetty/Spark-java based server responding to REST calls.
 * 
 * @author Henrik Baerbak Christensen, Aarhus University
 *
 */
public class ServerMainREST {
  
  public static void main(String[] args) throws Exception {
    if (args.length < 1) {
      explainAndDie();
    }
    new ServerMainREST(args[0]); // No error handling!
  }
  
  private static void explainAndDie() {
    System.out.println("Usage: ServerMainREST {port}");
    System.out.println("       port = port number for server to listen to");
    System.exit(-1);
  }

  public ServerMainREST(String portNo) throws Exception {
    // Define the server side delegates
    XDSBackend xds = null;
    xds = new FakeObjectXDSDatabase();
    TeleMed tsServant = new TeleMedServant(xds);

    // Configure the Spark-java servlet
    int port = Integer.parseInt(portNo);
    RESTServerRequestHandlerInvoker srh =
        new RESTServerRequestHandlerInvoker(port, tsServant, xds);
    srh.start();
    
    // Welcome 
    System.out.println("=== TeleMed Spark based REST Server Request Handler (port:"+port+") ===");
    System.out.println(" Use ctrl-c to terminate!"); 
  }
}
