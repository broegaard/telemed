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

import telemed.common.OperationNames;
import telemed.domain.TeleMed;
import telemed.domain.TeleObservation;
import telemed.domain.TimeInterval;
import telemed.helper.HelperMethods;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.junit.Before;
import org.junit.Test;

/**
 * TDD of the TeleMed proxy, using a Spy Requestor to
 * verify proper behavior.
 *
 * @author Henrik Baerbak Christensen, Aarhus University
 *
 */
public class TestTeleMedProxy {

  private SpyRequestor requestor;
  private TeleMed telemed;

  @Before
  public void setup() {
    requestor = new SpyRequestor();
    telemed = new TeleMedProxy(requestor);
  }

  @Test
  public void shouldValidateRequestObjectCreated() {
    // Create an observation
    TeleObservation teleObs1 = 
        HelperMethods.createObservation120over70forNancy();
    // and store it through the client proxy
    telemed.processAndStore(teleObs1);

    // Validate the requestor's state is correctly set by the proxy
    assertThat(requestor.lastOperationName, 
        is(OperationNames.PROCESS_AND_STORE_OPERATION));
    assertThat(requestor.lastObjectId, is(teleObs1.getPatientId()));
    // Testing the arguments and the type is tricky, but they will be
    // covered intensively by other tests later

    telemed.getObservationsFor("pid01", TimeInterval.LAST_DAY);

    // Validate
    assertThat(requestor.lastOperationName, 
        is(OperationNames.GET_OBSERVATIONS_FOR_OPERATION));
    assertThat(requestor.lastObjectId, is("pid01"));
    assertThat(requestor.lastArgument[0], is(TimeInterval.LAST_DAY));

    // 'correct' and 'delete' are left as an exercise :-)
  }
}
