package telemed.server;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.xml.parsers.*;

import org.w3c.dom.*;

import telemed.domain.*;

/**
 * Implementation of the builder interface to build a HL7 version 3 document. In
 * this teaching context it is not valid HL7 but shortened format that vaguely
 * resemble the (much more complex) HL7 format.
 */
public class HL7Builder implements Builder {

  // Formatter for the date-time format required by HL7
  public static final DateTimeFormatter HL7_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
  
  // The document to be built
  private Document document;
  private Element root;
  private Element observationList;

  /** Access the final resulting document
   * once the build process is over.
   * @return the final document. Is undefined
   * if the full build process has not been
   * followed.
   */
  public Document getResult() {
    return document;
  }

  @Override
  public void buildHeader(TeleObservation to) {
    // Create the XML document
    DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
    DocumentBuilder docBuilder = null;
    try {
       docBuilder = dbfac.newDocumentBuilder();
    } catch ( ParserConfigurationException e ) {
      throw new RuntimeException(e);
    }
    document = docBuilder.newDocument();

    // Create the root element
    root = document.createElement("ClinicalDocument");
    
    document.appendChild(root);
    
    // Create the effectiveTime, that is, the time the
    // observation was made.
    Element time = document.createElement("effectiveTime");
    // Date observationTime = new Date( to.getTime() );
    LocalDateTime observationTime = to.getTime();
    String timeInHL7Format = HL7_TIME_FORMAT.format(observationTime);// formatter.format(observationTime);
    time.setAttribute("value", timeInHL7Format);
    
    root.appendChild(time);
  }

  @Override
  public void buildPatientInfo(TeleObservation to) {
    Element patient = document.createElement("patient");
    Element id = document.createElement("id");
    id.setAttribute("extension", to.getPatientId());
    
    patient.appendChild(id);
    root.appendChild(patient);  
  }

  @Override
  public void buildObservationList(TeleObservation to) {
    observationList = document.createElement("component");
    root.appendChild(observationList);
  }

  @Override
  public void appendObservation(ClinicalQuantity quantity) {
    Element observation = document.createElement("observation");
    
    Element code = document.createElement("code");
    code.setAttribute("code", quantity.getCode());
    code.setAttribute("displayName", quantity.getDisplayName());
    observation.appendChild(code);
    
    Element value = document.createElement("value");
    value.setAttribute("unit", quantity.getUnit());
    value.setAttribute("value", ""+quantity.getValue());
    observation.appendChild(value);
    
    observationList.appendChild(observation);
  }
}
