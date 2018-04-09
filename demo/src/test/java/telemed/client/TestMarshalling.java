package telemed.client;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import frs.broker.Invoker;
import frs.broker.ReplyObject;
import frs.broker.RequestObject;
import frs.broker.Requestor;
import frs.broker.marshall.json.StandardJSONRequestor;
import org.junit.Before;
import org.junit.Test;
import frs.broker.Constants;
import telemed.domain.TeleMed;
import telemed.domain.TeleObservation;
import telemed.doubles.FakeObjectXDSDatabase;
import telemed.doubles.LocalMethodCallClientRequestHandler;
import telemed.helper.HelperMethods;
import telemed.marshall.json.TeleMedJSONInvoker;
import telemed.server.TeleMedServant;

import javax.servlet.http.HttpServletResponse;


/**
 * At 24 Oct 2017
 *
 * @author Henrik Baerbak Christensen, CS @ AU
 */
public class TestMarshalling {
  private TeleObservation teleObs1;
  private TeleMed teleMed;
  private LocalMethodCallClientRequestHandler clientRequestHandler;

  @Before
  public void setup() {
    teleObs1 = HelperMethods.createObservation120over70forNancy();
    // Create server side implementations
    FakeObjectXDSDatabase xds = new FakeObjectXDSDatabase();
    TeleMed teleMedServant = new TeleMedServant(xds);

    // Server side broker implementations
    Invoker invoker = new TeleMedJSONInvoker(teleMedServant);

    // Create client side broker implementations, using the local
    // method client request handler to avoid any real IPC layer.
    clientRequestHandler =
            new LocalMethodCallClientRequestHandler(invoker);
    Requestor requestor =
            new StandardJSONRequestor(clientRequestHandler);

    // Finally, create the client proxy for the TeleMed
    teleMed = new TeleMedProxy(requestor);
  }

  @Test
  public void shouldVerifyMarshallingFormat() {
    // Nancy uploads a single observation
    teleMed.processAndStore(teleObs1);

    // 'smoke testing' the request and reply
    RequestObject req = clientRequestHandler.getLastRequest();
    assertThat(req.getObjectId(), is(HelperMethods.NANCY_ID));
    assertThat(req.getVersionIdentity(), is(Constants.MARSHALLING_VERSION));
    // some 'smoke testing' of the payload
    assertThat(req.getPayload(), containsString("\"systolic\":{\"value\":120.0,"));

    ReplyObject rep = clientRequestHandler.getLastReply();
    assertThat(rep.getStatusCode(), is(HttpServletResponse.SC_CREATED));
    assertThat(rep.getVersionIdentity(), is(Constants.MARSHALLING_VERSION));
    assertThat(rep.getPayload(), is("\"uid-1\""));
  }

}
