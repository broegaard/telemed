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

package telemed.client;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import frds.broker.Invoker;
import frds.broker.ReplyObject;
import frds.broker.RequestObject;
import frds.broker.Requestor;
import frds.broker.marshall.json.StandardJSONRequestor;
import org.junit.Before;
import org.junit.Test;
import frds.broker.Versioning;
import telemed.common.OperationNames;
import telemed.domain.TeleMed;
import telemed.domain.TeleObservation;
import telemed.doubles.FakeObjectXDSDatabase;
import telemed.doubles.LocalMethodCallClientRequestHandler;
import telemed.helper.HelperMethods;
import telemed.marshall.json.TeleMedJSONInvoker;
import telemed.server.TeleMedServant;

import javax.servlet.http.HttpServletResponse;


/**
 * At 24 Oct 2017
 *
 * @author Henrik Baerbak Christensen, CS @ AU
 */
public class TestMarshalling {
  private TeleObservation teleObs1;
  private TeleMed teleMed;
  private LocalMethodCallClientRequestHandler clientRequestHandler;

  @Before
  public void setup() {
    teleObs1 = HelperMethods.createObservation120over70forNancy();
    // Create server side implementations
    FakeObjectXDSDatabase xds = new FakeObjectXDSDatabase();
    TeleMed teleMedServant = new TeleMedServant(xds);

    // Server side broker implementations
    Invoker invoker = new TeleMedJSONInvoker(teleMedServant);

    // Create client side broker implementations, using the local
    // method client request handler to avoid any real IPC layer.
    clientRequestHandler =
            new LocalMethodCallClientRequestHandler(invoker);
    Requestor requestor =
            new StandardJSONRequestor(clientRequestHandler);

    // Finally, create the client proxy for the TeleMed
    teleMed = new TeleMedProxy(requestor);
  }

  @Test
  public void shouldVerifyMarshallingFormat() {
    // Change marshalling format to a new version
    Versioning.SetMarshallingFormatVersion(7);

    // Nancy uploads a single observation
    teleMed.processAndStore(teleObs1);

    // 'smoke testing' the request and reply
    String request = clientRequestHandler.getLastRequest();
    assertThat(request, containsString("\"versionIdentity\":" + Versioning.MARSHALLING_VERSION));
    assertThat(request, containsString("\"operationName\":\"" + OperationNames.PROCESS_AND_STORE_OPERATION));

    // some 'smoke testing' of the payload
    assertThat(request, containsString("systolic"));

    String reply = clientRequestHandler.getLastReply();
    assertThat(reply, containsString(":"+HttpServletResponse.SC_CREATED));
    assertThat(reply, containsString("versionIdentity\":"+Versioning.MARSHALLING_VERSION));
    assertThat(reply, containsString("uid-1"));
  }
}
