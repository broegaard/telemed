package telemed.server;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.junit.*;
import org.w3c.dom.Document;

import telemed.domain.*;
import telemed.doubles.FakeObjectXDSDatabase;
import telemed.helper.HelperMethods;
import telemed.storage.*;

/**
 * Testing the server side implementation of TeleStore (servant role).
 * Part of the code is from the Net4Care project. www.net4care.dk
 * 
 * @author Henrik Baerbak Christensen, Aarhus University
 *
 */
public class TestTeleMedServant {

  private TeleMed telemed;
  private FakeObjectXDSDatabase xds;

  @Before
  public void setUp() {
    xds = new FakeObjectXDSDatabase();
    telemed = new TeleMedServant(xds);
  }
  
  /**
   * Validate that a storing operation on the telestore
   * results in a HL7 document being stored in XDS.
   * 
   * @throws IOException
   */
  @Test 
  public void shouldStoreInXDS() {
    // Create an observation
    TeleObservation teleObs1 = HelperMethods.createObservation120over70forNancy();
    // Store it
    telemed.processAndStore(teleObs1);
    
    // Validate that the server has received the proper data and
    // that the proper XML document is stored.
    Document stored = xds.getLastStoredObservation();
    assertThat(stored, is(notNullValue()));
    
    HelperMethods.assertThatDocumentRepresentsObservation120over70forNancy(stored);
    // System.out.println("stored = "+Convert.convertXMLDocumentToString(stored));
    
    // Validate the metadata associated with the observation
    MetaData meta = xds.getLastMetaData();
    assertThat(meta, is(notNullValue()));
    assertThat(meta.getPersonID(), is(HelperMethods.NANCY_ID));
  }

  /**
   * Validate the 'store and fetch' operation of
   * the TeleStore
   */
  @Test
  public void shouldHandleBasicTeleStoreAPI() {
    TeleObservation to1, to2, to3;
    to1 = new TeleObservation("pid001", 123, 78); 
    to2 = new TeleObservation("pid001", 125, 75);
    to3 = new TeleObservation("pid017", 180, 110);

    // Only a single to stored
    telemed.processAndStore(to1);
    
    List<TeleObservation> lastWeekList = telemed.getObservationsFor("pid001", TimeInterval.LAST_DAY);
    assertNotNull(lastWeekList);
    
    assertThat(lastWeekList.size(), is(1));
    TeleObservation calculated1 = lastWeekList.get(0);
    assertThat(calculated1.getPatientId(), is("pid001"));
    assertThat(calculated1.getSystolic().toString(), is("Systolic BP:123.0 mm(Hg)"));
    assertThat(calculated1.getDiastolic().toString(), is("Diastolic BP:78.0 mm(Hg)"));
    
    // Store more
    telemed.processAndStore(to2);
    telemed.processAndStore(to3);

    // Two observations for pid 001
    lastWeekList = telemed.getObservationsFor("pid001", TimeInterval.LAST_DAY);
    assertThat(lastWeekList.size(), is(2));
    calculated1 = lastWeekList.get(1);
    assertThat(to2.getTime(), is(calculated1.getTime()));
    assertThat(calculated1.getSystolic().toString(), is("Systolic BP:125.0 mm(Hg)"));
    
    // Only single for pid 017
    lastWeekList = telemed.getObservationsFor("pid017", TimeInterval.LAST_DAY);
    assertThat(lastWeekList.size(), is(1));
    calculated1 = lastWeekList.get(0);
    assertThat(calculated1.getPatientId(), is("pid017"));
    assertThat(to3.getTime(), is(calculated1.getTime()));
  }
  
  /**
   * Validate the search in different time intervals
   * 
   */
  @Test
  public void shouldHandleTimedQueries() {
    // To allow reuse, the validation has been extracted
    // into a public method
    validateTimedQueryBehaviour(telemed);
  }

  /**
   * Validate that queries spanning different time intervals produce the
   * expected results.
   * 
   * @param telestore
   *          the tele med instance to query
   * 
   */
  public static void validateTimedQueryBehaviour(TeleMed telestore) {
    TeleObservation to1, to2, to3, to4;
    to1 = new TeleObservation("pid001", 123, 78); 
    to2 = new TeleObservation("pid001", 125, 75);
    to3 = new TeleObservation("pid001", 180, 110);
    to4 = new TeleObservation("pid001", 193, 130);

    // Change the time for to2, to3, and to4
    
    // 'Rewind' to2, by 23 hours
    LocalDateTime ldt = to1.getTime();
    ldt = ldt.minusHours(23);
    to2.setTime(ldt);
    
    // 'Rewind' to3, by 6 days 23 hours
    ldt = to1.getTime();
    ldt = ldt.minusDays(6).minusHours(23);
    to3.setTime(ldt);

    // 'Rewind' to3, by 7 days 53 minutes
    ldt = to1.getTime();
    ldt = ldt.minusDays(7).minusMinutes(53);
    to4.setTime(ldt);
    
    // Store them
    telestore.processAndStore(to1);
    telestore.processAndStore(to2);
    telestore.processAndStore(to3);
    telestore.processAndStore(to4);

    // Assert that we get the proper set back
    List<TeleObservation> intervalTOList = 
        telestore.getObservationsFor("pid001", TimeInterval.LAST_DAY);
    assertThat(intervalTOList.size(), is(2));
    assertThat(intervalTOList.get(0).getSystolic().getValue(), is(123.0));
    assertThat(intervalTOList.get(1).getSystolic().getValue(), is(125.0));
    
    intervalTOList = 
        telestore.getObservationsFor("pid001", TimeInterval.LAST_WEEK);
    assertThat(intervalTOList.size(), is(3));
    assertThat(intervalTOList.get(2).getSystolic().getValue(), is(180.0));

    intervalTOList = 
        telestore.getObservationsFor("pid001", TimeInterval.LAST_MONTH);
    assertThat(intervalTOList.size(), is(4));
    assertThat(intervalTOList.get(3).getSystolic().getValue(), is(193.0));
  }
  /**
   * Given that a set of tele observations have been stored using home clients,
   * the server should be able to query the XDS for a set of documents
   * fulfilling a given search criteria.
   * 
   * @throws IOException
   */
  @Test public void shouldSupportQueryOnXDS() {
    
    XDSBackend thexds = xds;
    
    verifyQueries(telemed, thexds);
  }

  public static void verifyQueries(TeleMed telemed, XDSBackend thexds) {
    // First - create and upload three observations,
    // two for person 1, and one for person 2
    TeleObservation to1, to2, to3;
    to1 = new TeleObservation("pid001", 130, 80);
    to2 = new TeleObservation("pid001", 125, 85);
    to3 = new TeleObservation("pid002", 180, 90);
    
    // Reset the timestamps of the observations to
    // something known
    
    to1.setTime( LocalDateTime.of(2012, 6, 1, 7, 30));
    to2.setTime( LocalDateTime.of(2012, 6, 1, 10, 30));
    to3.setTime( LocalDateTime.of(2012, 6, 1, 8, 30));
    
    // Upload all three of them

    telemed.processAndStore(to1);
    telemed.processAndStore(to2);
    telemed.processAndStore(to3);

    // ================================================================
    // Now the server has translated them to HL7
    // documents and stored them in the XDS,
    // let us try to retrieve them again.
    
    Collection<Document> list; Document[] array; Document doc;

    LocalDateTime at0730ldt = LocalDateTime.of(2012, 6, 1, 7, 27);
    LocalDateTime at1030ldt = LocalDateTime.of(2012, 6, 1, 10, 35);
    LocalDateTime at0830ldt = LocalDateTime.of(2012, 6, 1, 8, 25);

    // ----------------------------------------------------------------
    // First, search for person 1 between 0730 and 1030
    list = thexds.retriveDocumentSet( "pid001", at0730ldt, at1030ldt);
    assertEquals( 2, list.size() );
    array = (Document[])list.toArray(new Document[list.size()]);  
    
    // Validate entry 1 
    doc = array[0];
    assertEquals("pid001",
            XMLUtility.getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc("extension",
                    0, "id", "patient", doc));
    // The HL7 format is YYYYMMDDHHMMSS for time stamps
    assertEquals("20120601073000",
            XMLUtility.getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc("value",
                    0, "effectiveTime", "ClinicalDocument", doc));

    // Validate entry 2 
    doc = array[1];
    assertEquals("pid001",
            XMLUtility.getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc("extension",
                    0, "id", "patient", doc));
    assertEquals("20120601103000",
            XMLUtility.getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc("value",
                    0, "effectiveTime", "ClinicalDocument", doc));

    // ----------------------------------------------------------------
    // Second, search for person 1 between 0730 and 0830
    list = thexds.retriveDocumentSet( "pid001", at0730ldt, at0830ldt);
    assertEquals( 1, list.size() );
    array = (Document[])list.toArray(new Document[list.size()]);  
    doc = array[0];
    assertEquals("20120601073000",
            XMLUtility.getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc("value",
                    0, "effectiveTime", "ClinicalDocument", doc));
   
    // ----------------------------------------------------------------
    // Third, search for person 2 between 0730 and 1030
    list = thexds.retriveDocumentSet( "pid002", at0730ldt, at1030ldt);
    assertEquals( 1, list.size() );
    array = (Document[])list.toArray(new Document[list.size()]);  
      
    // Validate entry
    doc = array[0];
    assertEquals("pid002",
            XMLUtility.getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc("extension",
                    0, "id", "patient", doc));
    // The HL7 format is YYYYMMDDHHMMSS for time stamps
    assertEquals("20120601083000",
            XMLUtility.getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc("value",
                    0, "effectiveTime", "ClinicalDocument", doc));
  }
  
  @Test
  public void shouldSupportModificationMethods() {
    validateModificationMethods(telemed);
  }

  public static void validateModificationMethods(TeleMed telemed) {
    TeleObservation to1, to2, to3;
    to1 = new TeleObservation("pid001", 123, 78); 
    to2 = new TeleObservation("pid001", 125, 75);
    to3 = new TeleObservation("pid017", 180, 110);

    // Store them in order
    String id = telemed.processAndStore(to1);
    assertThat(id, is(notNullValue()));

    String id2 = telemed.processAndStore(to2);
    assertThat(id2, is(notNullValue()));

    id = telemed.processAndStore(to3);
    assertThat(id, is(notNullValue()));
    
    TeleObservation to;
    
    // Revise the t02 observation
    to = new TeleObservation("pid001", 227.0, 91.0);
    to.setTime(LocalDateTime.of(2011, 3, 1, 7, 30));
    boolean isValid = telemed.correct(id2, to);
    assertThat(isValid, is(true));
    
    TeleObservation stored = telemed.getObservation(id2);
    assertThat(stored, is(notNullValue()));
    assertThat(stored.getSystolic().getValue(), is(227.0));
    // ensure that timestamps were NOT changed
    assertThat(to2.getTime(), is(stored.getTime()));
    
    // And remove it
    isValid = telemed.delete(id2);
    assertThat(isValid, is(true));
    stored = telemed.getObservation(id2);
    assertThat(stored, is(nullValue()));
    
    // verify pid001's measurements
    List<TeleObservation> intervalTOList = 
        telemed.getObservationsFor("pid001", TimeInterval.LAST_DAY);
    assertThat(intervalTOList.size(), is(1));
  }
  
}
