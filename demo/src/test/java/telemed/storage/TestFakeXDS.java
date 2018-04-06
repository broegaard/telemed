package telemed.storage;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;
import org.w3c.dom.Document;

import telemed.server.*;
import telemed.domain.TeleObservation;
import telemed.doubles.FakeObjectXDSDatabase;

/**
 * The Fake object XDS is complex enough to warrant a set of test cases.
 * 
 * @author Henrik Baerbak Christensen, Computer Science, Aarhus University
 *
 */
public class TestFakeXDS {

  private XDSBackend xds;
  private MetaData md;
  private Document hl7;

  @Test
  public void shouldSupportStoreFetchRoundTrip() {
    xds = new FakeObjectXDSDatabase();
    
    // Create MetaData and HL7 for a tele observation
    TeleObservation to = new TeleObservation("bjarne", 128.0,  89.0);
    buildMdAndHl7For(to);
    
    // Store it and validate generated ID
    String uniqueId = xds.provideAndRegisterDocument(md, hl7);
    
    assertThat(uniqueId, is("uid-1"));
    
    // Retrieve based upon ID
    Document stored = xds.retriveDocument(uniqueId);
    assertThat(stored, is(notNullValue()));
    assertThat( XMLUtility.getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc("value",
            0, "value", "observation", stored),
        is("128.0"));
    
    // UPDATING existing teleobs
    to = new TeleObservation("bjarne", 132.0, 91.0);
    buildMdAndHl7For(to);
    
    boolean isValid = xds.correctDocument(uniqueId, XDSBackend.Operation.UPDATE, hl7);
    assertThat(isValid, is(true));
    stored = xds.retriveDocument(uniqueId);
    assertThat(stored, is(notNullValue()));
    assertThat( XMLUtility.getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc("value",
            0, "value", "observation", stored),
        is("132.0"));
    
    // updating non existing doc
    isValid = xds.correctDocument("non-existing-id", XDSBackend.Operation.UPDATE, hl7);
    assertThat(isValid, is(false));
    
    // DELETING existing teleobs
    isValid = xds.correctDocument(uniqueId, XDSBackend.Operation.DELETE, hl7);
    assertThat(isValid, is(true));
    // Second delete is not valid
    isValid = xds.correctDocument(uniqueId, XDSBackend.Operation.DELETE, hl7);
    assertThat(isValid, is(false));
    
    // No contents any more
    stored = xds.retriveDocument(uniqueId);
    assertThat(stored, is(nullValue()));
  }
  
  private void buildMdAndHl7For(TeleObservation to) {
    MetadataBuilder mdBuilder = new MetadataBuilder();
    Director.construct(to, mdBuilder);
    md = mdBuilder.getResult();
    
    HL7Builder hl7Builder = new HL7Builder();
    Director.construct(to, hl7Builder);
    hl7 = hl7Builder.getResult();
  }

}
