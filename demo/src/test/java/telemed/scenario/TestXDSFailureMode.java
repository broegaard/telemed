/*
 * Copyright (C) 2018 Henrik Bærbak Christensen, baerbak.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package telemed.scenario;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import frds.broker.IPCException;
import frds.broker.Invoker;
import frds.broker.ReplyObject;
import frds.broker.Requestor;
import frds.broker.marshall.json.StandardJSONRequestor;

import org.junit.Test;
import org.w3c.dom.Document;

import telemed.server.*;
import telemed.client.*;
import telemed.domain.*;
import telemed.doubles.*;
import telemed.helper.HelperMethods;
import telemed.marshall.json.TeleMedJSONInvoker;
import telemed.storage.*;

/** 
 * Initial tests of failure scenarios for XDS trouble at server side.
 * 
 * @author Henrik Baerbak Christensen, Computer Science, Aarhus University
 *
 */
public class TestXDSFailureMode {

  @Test
  public void shouldValidateXDSFailureIsFlaggedToTheClient() {
    // Create fake XDS
    XDSBackend xdsf = new FakeObjectXDSDatabase();
    // And wrap it in a saboteur (Meszaros)
    SaboteurXDS xds = new SaboteurXDS(xdsf);
    TeleMed tsServant = new TeleMedServant(xds, false);
    Invoker invoker = new TeleMedJSONInvoker(tsServant);
    
    LocalMethodCallClientRequestHandler clientRequestHandler = 
        new LocalMethodCallClientRequestHandler(invoker);
    
    Requestor requestor = new StandardJSONRequestor(clientRequestHandler);
    TeleMedProxy telemed = new TeleMedProxy(requestor);
    
    TeleObservation teleObs1 = HelperMethods.createObservation120over70forNancy();
    // Tell our saboteur to fail on method 'provideAndRegister'
    xds.failOnMethodOfType(1);
    
    try {
      telemed.processAndStore(teleObs1);
      fail("Should throw TeleMedExcpetion");
    } catch (IPCException e) {
      ReplyObject lastReply = clientRequestHandler.getLastReply();
      assertThat(lastReply.isSuccess(), is(false));
      assertThat(lastReply.errorDescription(), containsString("XDS Failure"));
      assertThat(lastReply.getStatusCode(), is(500)); // HTTP 500 internal server error
    }
  }

  public class SaboteurXDS implements XDSBackend {

    private final XDSBackend delegate;
    private int cfg = 0;

    public SaboteurXDS(XDSBackend xds) {
      delegate = xds;
    }

    public void failOnMethodOfType(int i) {
      cfg = i;
    }

    public String provideAndRegisterDocument(MetaData metaData, Document observationAsHL7) {
      if (cfg==1) { 
        throw new XDSException("XDS Failure, when storing for patient id:"+metaData.getPersonID());
      }
      return delegate.provideAndRegisterDocument(metaData, observationAsHL7);
    }

    public List<Document> retriveDocumentSet(String personID, LocalDateTime start, LocalDateTime end) {
      return delegate.retriveDocumentSet(personID, start, end);
    }

    public Document retriveDocument(String uniqueId) {
      return delegate.retriveDocument(uniqueId);
    }

    public boolean correctDocument(String uniqueId, Operation operation, Document doc) {
      return delegate.correctDocument(uniqueId, operation, doc);
    }
  }
}
