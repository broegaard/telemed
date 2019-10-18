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

package telemed.ipc;

import frds.broker.ClientRequestHandler;
import frds.broker.Requestor;
import org.junit.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import javax.servlet.http.HttpServletResponse;

import frds.broker.marshall.json.StandardJSONRequestor;
import frds.broker.ipc.http.UriTunnelClientRequestHandler;
import frds.broker.ipc.http.UriTunnelServerRequestHandler;

import telemed.server.*;
import telemed.client.*;
import telemed.domain.*;
import telemed.doubles.FakeObjectXDSDatabase;
import telemed.helper.HelperMethods;
import telemed.ipc.http.*;
import telemed.marshall.json.TeleMedJSONInvoker;

/** Test that a simple upload and fetch scenario is working
 * using the HTTP URI Tunneling variants of the Client- and
 * ServerRequestHandlers.
 *
 *
 * @author Henrik Baerbak Christensen, Aarhus University.
 *
 */
public class TestUriTunnel {

  TeleMedProxy teleMed;
  UriTunnelServerRequestHandler serverRequestHandler;

  @Before
  public void setup() throws InterruptedException {
    // The 'trick' to get random port number each time the
    // test case is run, to avoid OS slow connection release trouble.
    final int PORT_NUMBER = ThreadLocalRandom.current().nextInt(10000, 30000);

    // Given the Server side roles on a UriTunnel IPC
    FakeObjectXDSDatabase xds = new FakeObjectXDSDatabase();
    TeleMed tsServant = new TeleMedServant(xds);
    TeleMedJSONInvoker invoker = new TeleMedJSONInvoker(tsServant);
    serverRequestHandler = new TeleMedUriTunnelServerRequestHandler(invoker, PORT_NUMBER, xds);
    serverRequestHandler.start();

    // Given the Client side roles
    ClientRequestHandler restCRH =
            new UriTunnelClientRequestHandler("localhost",
                    PORT_NUMBER, Constants.BLOODPRESSURE_PATH);

    Requestor requestor = new StandardJSONRequestor(restCRH);
    teleMed = new TeleMedProxy(requestor);
  }
  
  @After
  public void teardown() {
    serverRequestHandler.stop();
  }
  
  @Test
  public void shouldHandleScenario() {
    // Given a tele observation
    TeleObservation teleObs1 = new TeleObservation(HelperMethods.NANCY_ID, 127.3, 93);
    
    // When the observation is uploaded
    String id2 = teleMed.processAndStore(teleObs1);

    // Then verify that CREATED code was returned
    assertThat(serverRequestHandler.lastStatusCode(), is(HttpServletResponse.SC_CREATED));
    assertThat(serverRequestHandler.lastHTTPVerb(), is("POST"));
    
    assertThat(id2, is(notNullValue()));
    
    // When we get all observations
    List<TeleObservation> l = teleMed.getObservationsFor(HelperMethods.NANCY_ID, TimeInterval.LAST_DAY);

    // Then verify it is correctly fetched
    assertThat(l, is(notNullValue()));
    assertThat(l.size(), is(1));
    assertThat(l.get(0).getSystolic().getValue(), is(127.3));
  }
  
}
