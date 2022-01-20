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

package telemed.helper;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.w3c.dom.Document;

import telemed.server.XMLUtility;
import telemed.domain.*;

/**
 * Container of methods that are used in multiple test cases to establish and
 * validate observations with known properties.
<#if type == "code">

<#include "/data/author.txt">
</#if>
 */

public class HelperMethods {

  public static final String NANCY_ID = "251248-1234";
  
  /** Create an tele observation of 120 over 70 for Nancy on
   * a specific time: 7.30 June 1st 2012 UTC.
   */
  public static TeleObservation createObservation120over70forNancy() {
    TeleObservation to = new TeleObservation(HelperMethods.NANCY_ID, 120.0, 70.0);
    to.setTime(OffsetDateTime.of(2012, 6, 1, 7, 30, 12, 0, ZoneOffset.UTC));
    return to;
  }

  /** Validate that an HL7 document contains the tags containing information
   * for Nancy's blood pressure of 120 over 70.
   * @param doc Document to validate
   */
  public static void assertThatDocumentRepresentsObservation120over70forNancy(Document doc) {
    // assert root element correct
    assertEquals("ClinicalDocument", doc.getDocumentElement().getNodeName());
    
    // assert timestamp stored correctly
    assertEquals("2012-06-01T07:30:12Z",
        XMLUtility.getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc("value", 0, 
            "effectiveTime", "ClinicalDocument", doc));
    
    // assert proper patient id stored
    assertEquals(NANCY_ID,
        XMLUtility.getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc("extension", 0, 
            "id", "patient", doc));
    
    // assert systolic data
    assertEquals( "MSC88019", 
        XMLUtility.getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc("code", 0, 
            "code", "observation", doc));
    assertEquals( "Systolic BP", 
        XMLUtility.getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc("displayName", 0, 
            "code", "observation", doc));
    assertEquals( "mm(Hg)", 
        XMLUtility.getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc("unit", 0, 
            "value", "observation", doc));
    assertEquals( "120.0", 
        XMLUtility.getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc("value", 0, 
            "value", "observation", doc));

    // assert diastolic data
    assertEquals( "MSC88020", 
        XMLUtility.getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc("code", 1, 
            "code", "observation", doc));
    assertEquals( "Diastolic BP", 
        XMLUtility.getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc("displayName", 1, 
            "code", "observation", doc));
    assertEquals( "mm(Hg)", 
        XMLUtility.getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc("unit", 1, 
            "value", "observation", doc));
    assertEquals( "70.0", 
        XMLUtility.getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc("value", 1, 
            "value", "observation", doc));
  }
}
