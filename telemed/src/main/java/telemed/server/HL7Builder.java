/*
 * Copyright (C) 2018-2021. Henrik Bærbak Christensen, Aarhus University.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package telemed.server;

import java.time.OffsetDateTime;
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

    // The HL7 time format is
    // DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    // but I use ISO8601 as it is easy to read, and support time zones.
    OffsetDateTime observationTime = to.getTime();
    String timeInHL7Format = DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(observationTime);
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
