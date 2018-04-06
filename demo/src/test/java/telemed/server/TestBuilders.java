package telemed.server;

import static org.junit.Assert.*;

import org.junit.*;
import org.w3c.dom.*;

import telemed.domain.*;
import telemed.helper.HelperMethods;
import telemed.storage.MetaData;

/**
 * TDD test cases for crafting the builder that builds something that vaguely
 * resemble HL7.
 * 
 * @author Henrik Baerbak Christensen, Aarhus University
 *
 */
public class TestBuilders {
  
  private TeleObservation to; 

  @Before public void setup() {
    to = HelperMethods.createObservation120over70forNancy();
  }
  
  @Test public void shouldBuildValidHL7() {
    // Construct a builder for building HL7 XML documents
    HL7Builder builder = new HL7Builder();
    
    // Ask the director to construct a document using the 
    // HL7 builder
    Director.construct(to, builder);
    
    // Retrive the constructed element from the builder
    Document doc = builder.getResult();
    
    // Hopefully it is not null
    assertNotNull(doc);
    
    // System.out.println( Utility.convertXMLDocumentToString(doc));
    
    // assert that the built document indeed contains the information
    // that it is supposed to contain.
    HelperMethods.assertThatDocumentRepresentsObservation120over70forNancy(doc);
  }
  
  @Test public void shouldBuildValidMetadata() {
    MetadataBuilder builder = new MetadataBuilder();
    Director.construct(to, builder);
    MetaData meta = builder.getResult();
    
    // validate person identity of metadata
    assertEquals( HelperMethods.NANCY_ID, meta.getPersonID() );
    
    // assert time stamp in metadata
    assertEquals( 1338528600000L, meta.getTimestamp() );
  }
  
}
