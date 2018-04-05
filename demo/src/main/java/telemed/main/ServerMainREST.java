package telemed.main;

import telemed.server.*;
import telemed.domain.TeleMed;
import telemed.doubles.*;
import telemed.rest.SparkJavaServerRequestHandlerAndInvoker;
import telemed.storage.XDSBackend;

/** Main program for a
 * Jetty/Spark-java based server responding to REST calls.
 * 
 * @author Henrik Baerbak Christensen, Aarhus University
 *
 */
public class ServerMainREST {
  
  public static void main(String[] args) throws Exception {
    new ServerMainREST(); // No error handling!
  }

  public ServerMainREST() {
    // Define the server side delegates
    XDSBackend xds = null;
    xds = new FakeObjectXDSDatabase();
    TeleMed tsServant = new TeleMedServant(xds);

    // Configure the Spark-java servlet
    int port = 4567;
    SparkJavaServerRequestHandlerAndInvoker srh =
            new SparkJavaServerRequestHandlerAndInvoker(port, tsServant);
    srh.registerRoutes();

    // Welcome
    System.out.println("=== TeleMed REST Server Request Handler (port:"+port+") ===");
    System.out.println(" Use ctrl-c to terminate!"); 
  }
}
