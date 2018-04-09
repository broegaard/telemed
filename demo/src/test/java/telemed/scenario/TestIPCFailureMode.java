package telemed.scenario;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import frs.broker.marshall.json.StandardJSONRequestor;
import org.junit.*;

import frs.broker.*;
import telemed.server.*;
import telemed.client.*;
import telemed.domain.*;
import telemed.doubles.*;
import telemed.helper.HelperMethods;
import telemed.marshall.json.TeleMedJSONInvoker;

/** Test failure situations in the IPC layer.
 * Here we use a Saboteur to introduce simulated
 * network errors.
 */
public class TestIPCFailureMode {


  private TeleObservation teleObs1;
  private FakeObjectXDSDatabase xds;
  
  private TeleMed telemed;

  @Before 
  public void setup() {
    // Create server side implementations
    xds = new FakeObjectXDSDatabase();
    TeleMed tsServant = new TeleMedServant(xds);

    // Server side broker implementations
    Invoker invoker = new TeleMedJSONInvoker(tsServant);
    
    // Create client side broker implementations
    ClientRequestHandler clientRequestHandler = new LocalMethodCallClientRequestHandler(invoker);
    // Decorate it with a saboteur of the connection
    clientRequestHandler = new SaboteurRequestHandler(clientRequestHandler);
    Requestor requestor = new StandardJSONRequestor(clientRequestHandler);
    
    // Finally, create the client proxy for the TeleMed
    telemed = new TeleMedProxy(requestor);
  }

  @Test
  public void shouldCatchFailedStoreCalls() {
    teleObs1 = HelperMethods.createObservation120over70forNancy();
    try {
      telemed.processAndStore(teleObs1);
      fail("Should throw TeleMedException");
    } catch (IPCException e) {
      assertThat(e.getMessage(), containsString("nasty communication error"));
    }
  }

  public class SaboteurRequestHandler implements ClientRequestHandler {

    public SaboteurRequestHandler(ClientRequestHandler clientRequestHandler) {
      // Not really using the decoratee for anything
    }

    @Override
    public ReplyObject sendToServer(RequestObject requestObject) {
      throw new IPCException("Send failed due to nasty communication error");
    }

    @Override
    public void close() {

    }

  }

}
