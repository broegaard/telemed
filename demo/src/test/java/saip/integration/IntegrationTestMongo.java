package saip.integration;

import static org.junit.Assert.*;

import org.junit.*;

import static org.hamcrest.CoreMatchers.*;

import org.w3c.dom.Document;

import telemed.server.*;
import telemed.domain.TeleObservation;
import telemed.storage.*;
import saip.storage.mongo.MongoXDSAdapter;

/** Integration tests requiring a running MongoDB
 * on localhost:27017.
 * 
 * REMOVE '@Ignore' annotation to enable this
 * test case on a machine that has the
 * proper environment defined.
 * 
 * @author Henrik Baerbak Christensen, Aarhus University.
 *
 */
@Ignore
public class IntegrationTestMongo {

  private XDSBackend xds;
  private TeleMedServant telemed;
  
  @Before
  public void setup() {
    xds = new MongoXDSAdapter("localhost", 27017);
    MongoXDSAdapter adapter = (MongoXDSAdapter)xds;
    
    // Wipe the DB to ensure reproducible tests
    adapter.dropTheDb("yes-i-am-testing");
    
    telemed = new TeleMedServant(xds);
  }
  
  @Test
  public void shouldDoAllXDSOperations() {
    TeleObservation to = new TeleObservation("pid01", 121, 77);
    
    // Store it
    String uniqueId = telemed.processAndStore(to);
    assertThat(uniqueId, is(notNullValue()));
    
    // Retrieve based upon ID
    Document stored = xds.retriveDocument(uniqueId);
    assertThat(stored, is(notNullValue()));
    assertThat( XMLUtility.getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc("value", 0, "value", "observation", stored), 
        is("121.0"));
  }
  
  @Test
  public void shouldHandleQueries() {
    // To allow reuse, the validation has been extracted
    // into a public method
    TestTeleMedServant.verifyQueries(telemed, xds);
  }

  @Test
  public void shouldSupportModificationMethods() {
    TestTeleMedServant.validateModificationMethods(telemed);
  }


}
