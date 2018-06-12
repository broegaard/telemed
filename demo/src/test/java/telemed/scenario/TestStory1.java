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

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import java.util.*;

import frds.broker.ClientRequestHandler;
import frds.broker.Invoker;
import frds.broker.Requestor;
import frds.broker.ServerRequestHandler;
import frds.broker.ipc.http.UriTunnelClientRequestHandler;
import frds.broker.ipc.http.UriTunnelServerRequestHandler;
import frds.broker.ipc.socket.SocketClientRequestHandler;
import frds.broker.ipc.socket.SocketServerRequestHandler;
import frds.broker.marshall.json.StandardJSONRequestor;

import org.junit.*;
import org.w3c.dom.Document;

import telemed.server.*;
import telemed.client.*;
import telemed.domain.*;
import telemed.doubles.*;
import telemed.helper.HelperMethods;
import telemed.marshall.json.TeleMedJSONInvoker;

/**
 * The central TeleMed story 1: Nancy uploads a blood pressure measurement to the
 * server side; and we validate that a proper HL7 document is stored in the
 * national XDS database.
 * 
 * @author Henrik Baerbak Christensen, Aarhus University
 *
 */
public class TestStory1 {

  private TeleObservation teleObs1;
  private FakeObjectXDSDatabase xds;
  
  private TeleMed teleMed;

  @Before 
  public void setup() {
    teleObs1 = HelperMethods.createObservation120over70forNancy();
    // Create server side implementations
    xds = new FakeObjectXDSDatabase();
    TeleMed teleMedServant = new TeleMedServant(xds);

    // Server side broker implementations
    Invoker invoker = new TeleMedJSONInvoker(teleMedServant);
    
    // Create client side broker implementations, using the local 
    // method client request handler to avoid any real IPC layer.
    ClientRequestHandler clientRequestHandler =
        new LocalMethodCallClientRequestHandler(invoker);
    Requestor requestor =
        new StandardJSONRequestor(clientRequestHandler);
    
    // Finally, create the client proxy for the TeleMed
    teleMed = new TeleMedProxy(requestor);
  }
  
  @Test
  public void shouldStoreFromClient() {
    // Nancy uploads a single observation 
    teleMed.processAndStore(teleObs1);
    
    // And the proper HL7 document is stored in the backend XDS
    Document stored = xds.getLastStoredObservation();
    HelperMethods.assertThatDocumentRepresentsObservation120over70forNancy(stored);
  }
  
  @Test
  public void shouldFetchFromClient() {
    TeleObservation to1, to2;
    to1 = new TeleObservation("pid001", 123, 78); 
    to2 = new TeleObservation("pid001", 125, 75);
  
    // Store two observations
    teleMed.processAndStore(to1);
    teleMed.processAndStore(to2);
    
    List<TeleObservation> lastWeekList = teleMed.getObservationsFor("pid001", TimeInterval.LAST_DAY);
    assertThat(lastWeekList, is(notNullValue()));

    assertThat(lastWeekList.size(), is(2));
    TeleObservation obs;
    obs = lastWeekList.get(0);
    assertThat(obs.getPatientId(), is("pid001"));
    assertThat(obs.getSystolic().toString(), is("Systolic BP:123.0 mm(Hg)"));
    
    obs = lastWeekList.get(0);
    assertThat(obs.getPatientId(), is("pid001"));
    assertThat(obs.getDiastolic().toString(), is("Diastolic BP:78.0 mm(Hg)"));
  }
    
  @Test
  public void shouldHandleTimedQueries() {
    // Reuse test case from the server test code, note that
    // these tests operate on the full client->server broker
    // implementation.
    TestTeleMedServant.validateTimedQueryBehaviour(teleMed);
  }

  @Test
  public void shouldSupportModificationMethods() {
    TestTeleMedServant.validateModificationMethods(teleMed);
  }

}
