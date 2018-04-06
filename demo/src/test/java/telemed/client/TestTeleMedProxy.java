package telemed.client;

import telemed.common.OperationNames;
import telemed.domain.TeleMed;
import telemed.domain.TeleObservation;
import telemed.domain.TimeInterval;
import telemed.helper.HelperMethods;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.junit.Before;
import org.junit.Test;

/**
 * TDD of the TeleMed proxy, using a Spy Requestor to
 * verify proper behavior.
 *
 * @author Henrik Baerbak Christensen, Aarhus University
 *
 */
public class TestTeleMedProxy {

  private SpyRequestor requestor;
  private TeleMed telemed;

  @Before
  public void setup() {
    requestor = new SpyRequestor();
    telemed = new TeleMedProxy(requestor);
  }

  @Test
  public void shouldValidateRequestObjectCreated() {
    // Create an observation
    TeleObservation teleObs1 = 
        HelperMethods.createObservation120over70forNancy();
    // and store it through the client proxy
    telemed.processAndStore(teleObs1);

    // Validate the requestor's state is correctly set by the proxy
    assertThat(requestor.lastOperationName, 
        is(OperationNames.PROCESS_AND_STORE_OPERATION));
    assertThat(requestor.lastObjectId, is(teleObs1.getPatientId()));
    // Testing the arguments and the type is tricky, but they will be
    // covered intensively by other tests later

    telemed.getObservationsFor("pid01", TimeInterval.LAST_DAY);

    // Validate
    assertThat(requestor.lastOperationName, 
        is(OperationNames.GET_OBSERVATIONS_FOR_OPERATION));
    assertThat(requestor.lastObjectId, is("pid01"));
    assertThat(requestor.lastArgument[0], is(TimeInterval.LAST_DAY));

    // 'correct' and 'delete' are left as an exercise :-)
  }
}
