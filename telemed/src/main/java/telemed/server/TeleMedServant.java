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
import java.util.*;

import org.w3c.dom.Document;

import frds.broker.Servant;
import telemed.domain.*;
import telemed.storage.*;

/**
 * The implementation of the TeleMed interface which provides the business
 * behaviour required. As it resides on the server side, it is the 'servant'
 * object.
 */
public class TeleMedServant implements TeleMed, Servant {

  private final XDSBackend xds;

  public TeleMedServant(XDSBackend xds) {
    this.xds = xds;
  }

  @Override
  public String processAndStore(TeleObservation teleObs) {
    // Generate the XML document representing the
    // observation in HL7 (HealthLevel7) format.
    HL7Builder builder = new HL7Builder();   
    Director.construct(teleObs, builder);
    Document hl7Document = builder.getResult();
    
    // Generate the metadata for the observation
    MetadataBuilder metaDataBuilder = new MetadataBuilder();
    Director.construct(teleObs, metaDataBuilder);
    MetaData metadata = metaDataBuilder.getResult();
    
    // Finally store the document in the XDS storage system
    String uniqueId = null;
    uniqueId = xds.provideAndRegisterDocument(metadata, hl7Document);
    
    return uniqueId;
  }

  @Override
  public List<TeleObservation> getObservationsFor(String patientId, TimeInterval interval) {
    List<TeleObservation> teleObsList = new ArrayList<>();
    // Calculate the time interval to search within
    OffsetDateTime now = OffsetDateTime.now();
    OffsetDateTime someTimeAgo = null;
    if (interval == TimeInterval.LAST_DAY) {
      someTimeAgo = now.minusDays(1);
    } else if (interval == TimeInterval.LAST_WEEK) {
      someTimeAgo = now.minusDays(7);
    } else {
      someTimeAgo = now.minusMonths(1);
    }

    // Query the database for those HL7 documents that match query
    List<Document> docList = xds.retriveDocumentSet(patientId, someTimeAgo, now);
    
    // Sigh - have to convert back from XML to a TeleObservation
    docList.stream().forEach( (d) -> { 
      TeleObservation to = createTeleObsFromHL7Document(d);
      teleObsList.add(to);
    } );
    return teleObsList;
  }

  @Override
  public TeleObservation getObservation(String uniqueId) {
    Document doc = xds.retriveDocument(uniqueId);
    if (doc == null) { return null; }
    
    return createTeleObsFromHL7Document(doc);
  }

  @Override
  public boolean correct(String uniqueId, TeleObservation to) {
    // Find the document if any
    Document doc = xds.retriveDocument(uniqueId);
    if (doc == null) { return false; }
    
    // Maintain the time stamp, cannot be corrected
    OffsetDateTime originalTime = getTimeFromHL7Document(doc);
    to.setTime(originalTime);
    
    // Create a new document from the given tele obs
    HL7Builder builder = new HL7Builder();   
    Director.construct(to, builder);
    Document hl7Document = builder.getResult();

    // and correct it in the XDS backtier
    return xds.correctDocument(uniqueId, XDSBackend.Operation.UPDATE, hl7Document);
  }

  @Override
  public boolean delete(String uniqueId) {
    return xds.correctDocument(uniqueId, XDSBackend.Operation.DELETE, null);
  }

  private TeleObservation createTeleObsFromHL7Document(Document d) {
    TeleObservation to;
    // A bit fragile but we rely on the sequence numbers to retrieve systolic and diastolic measurements
    String sysAsString = XMLUtility.
            getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc("value",
                    0, "value", "observation", d);
    String diaAsString = XMLUtility.
            getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc("value",
                    1, "value", "observation", d);
    String localpatientId = XMLUtility.
            getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc("extension",
                    0, "id", "patient", d);
    
    // Create the tele observation
    to = new TeleObservation(localpatientId, Double.parseDouble(sysAsString),
            Double.parseDouble(diaAsString));

    // Retrieve the time stamp and convert it into java equivalent
    OffsetDateTime ldt;
    ldt = getTimeFromHL7Document(d);
    to.setTime(ldt);
    return to;
  }

  private OffsetDateTime getTimeFromHL7Document(Document d) {
    OffsetDateTime ldt;
    String timestamp = XMLUtility.
            getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc("value",
                    0, "effectiveTime", "ClinicalDocument", d);
    ldt = OffsetDateTime.parse(timestamp);
    return ldt;
  }


}
