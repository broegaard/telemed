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
import static org.junit.Assert.*;
import org.junit.*;

import frds.broker.*;
import frds.broker.marshall.json.StandardJSONRequestor;

import telemed.server.*;
import telemed.client.*;
import telemed.domain.*;
import telemed.doubles.*;
import telemed.helper.HelperMethods;
import telemed.marshall.json.TeleMedJSONInvoker;

/** Test failure situations in the IPC layer.
 * Here we use a Saboteur to introduce simulated
 * network errors.
 */
public class TestIPCFailureMode {


  private TeleObservation teleObs1;
  private FakeObjectXDSDatabase xds;
  
  private TeleMed telemed;

  @Before 
  public void setup() {
    // Create server side implementations
    xds = new FakeObjectXDSDatabase();
    TeleMed tsServant = new TeleMedServant(xds, false);

    // Server side broker implementations
    Invoker invoker = new TeleMedJSONInvoker(tsServant);
    
    // Create client side broker implementations
    ClientRequestHandler clientRequestHandler = new LocalMethodCallClientRequestHandler(invoker);
    // Decorate it with a saboteur of the connection
    clientRequestHandler = new SaboteurRequestHandler(clientRequestHandler);
    Requestor requestor = new StandardJSONRequestor(clientRequestHandler);
    
    // Finally, create the client proxy for the TeleMed
    telemed = new TeleMedProxy(requestor);
  }

  @Test
  public void shouldCatchFailedStoreCalls() {
    teleObs1 = HelperMethods.createObservation120over70forNancy();
    try {
      telemed.processAndStore(teleObs1);
      fail("Should throw TeleMedException");
    } catch (IPCException e) {
      assertThat(e.getMessage(), containsString("nasty communication error"));
    }
  }

  public class SaboteurRequestHandler implements ClientRequestHandler {

    public SaboteurRequestHandler(ClientRequestHandler clientRequestHandler) {
      // Not really using the decoratee for anything
    }

    @Override
    public ReplyObject sendToServer(RequestObject requestObject) {
      throw new IPCException("Send failed due to nasty communication error");
    }

    @Override
    public void setServer(String hostname, int port) {
      // not used
    }

    @Override
    public void close() {

    }

  }

}
