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

import frds.broker.ClientProxy;
import frds.broker.IPCException;
import frds.broker.Requestor;
import telemed.common.OperationNames;
import telemed.domain.*;

import javax.servlet.http.HttpServletResponse;

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

    // Handle empty return values (404 error code)
    List<TeleObservation> returnedList;
    try {
      returnedList = requestor.sendRequestAndAwaitReply(patientId,
              OperationNames.GET_OBSERVATIONS_FOR_OPERATION,
              collectionType, interval);
    } catch(IPCException e) {
      if (e.getStatusCode() != HttpServletResponse.SC_NOT_FOUND) {
        throw e;
      }
      returnedList = new ArrayList<>();
    }

    return returnedList;
  }

  @Override
  public boolean correct(String uniqueId, TeleObservation to) {
    return requestor.sendRequestAndAwaitReply(uniqueId, 
        OperationNames.CORRECT_OPERATION, boolean.class, to);
  }

  @Override
  public TeleObservation getObservation(String uniqueId) {
    TeleObservation to;
    // Handle empty return values (404 error code)
    try {
      to = requestor.sendRequestAndAwaitReply(uniqueId,
              OperationNames.GET_OBSERVATION_OPERATION, TeleObservation.class);
    } catch (IPCException e) {
      if (e.getStatusCode() != HttpServletResponse.SC_NOT_FOUND) {
        throw e;
      }
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
