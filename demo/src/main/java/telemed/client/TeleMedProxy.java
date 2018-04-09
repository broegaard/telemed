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

import java.lang.reflect.Type;
import java.util.*;

import com.google.gson.reflect.*;

import frs.broker.*;
import telemed.common.OperationNames;
import telemed.domain.*;

/**
 * The ClientProxy implementation of the TeleMed role. This proxy object
 * resides on the client side and acts as a TeleMed instance, but all method
 * calls are marshaled and sent to the server, and the replies are interpreted
 * before returning to the callers.
 */
public class TeleMedProxy implements TeleMed, ClientProxy {

  private final Requestor requestor;

  public TeleMedProxy(Requestor crh) {
    this.requestor = crh;
  }

  @Override
  public String processAndStore(TeleObservation teleObs) {
    String uid = 
      requestor.sendRequestAndAwaitReply(teleObs.getPatientId(), 
        OperationNames.PROCESS_AND_STORE_OPERATION, 
	    String.class, teleObs);
    return uid; 
  }

  @Override
  public List<TeleObservation> getObservationsFor(String patientId, 
	  TimeInterval interval) {
    Type collectionType = 
      new TypeToken<List<TeleObservation>>(){}.getType();
    return requestor.sendRequestAndAwaitReply(patientId,
        OperationNames.GET_OBSERVATIONS_FOR_OPERATION, 
        collectionType, interval);
  }

  @Override
  public boolean correct(String uniqueId, TeleObservation to) {
    return requestor.sendRequestAndAwaitReply(uniqueId, 
        OperationNames.CORRECT_OPERATION, boolean.class, to);
  }

  @Override
  public TeleObservation getObservation(String uniqueId) {
    TeleObservation to;

    try {
      to = requestor.sendRequestAndAwaitReply(uniqueId,
              OperationNames.GET_OBSERVATION_OPERATION, TeleObservation.class);
    } catch (IPCException e) {
      // TODO: introduce status code in IPCException and use it
      // If e.getStatusCode != SC_NOT_FOUND throw ipc exception again
      to = null;
    }
    return to;
  }

  @Override
  public boolean delete(String uniqueId) {
    return requestor.sendRequestAndAwaitReply(uniqueId, 
        OperationNames.DELETE_OPERATION, boolean.class);
  }
}
