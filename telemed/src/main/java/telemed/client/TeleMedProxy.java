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

  /* As there is only ONE telemed servant object, the
  objectId is really not used in the TeleMed case, so
  we just provide a 'dummy' string.
   */
  public static final String TELEMED_OBJECTID = "singleton";

  private final Requestor requestor;

  public TeleMedProxy(Requestor crh) {
    this.requestor = crh;
  }

  @Override
  public String processAndStore(TeleObservation teleObs) {
    String uid = 
      requestor.sendRequestAndAwaitReply(TELEMED_OBJECTID,
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
      returnedList = requestor.sendRequestAndAwaitReply(TELEMED_OBJECTID,
              OperationNames.GET_OBSERVATIONS_FOR_OPERATION,
              collectionType, patientId, interval);
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
    return requestor.sendRequestAndAwaitReply(TELEMED_OBJECTID,
        OperationNames.CORRECT_OPERATION, boolean.class, uniqueId, to);
  }

  @Override
  public TeleObservation getObservation(String uniqueId) {
    TeleObservation to;
    // Handle empty return values (404 error code)
    try {
      to = requestor.sendRequestAndAwaitReply(TELEMED_OBJECTID,
              OperationNames.GET_OBSERVATION_OPERATION, TeleObservation.class, uniqueId);
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
    return requestor.sendRequestAndAwaitReply(TELEMED_OBJECTID,
        OperationNames.DELETE_OPERATION, boolean.class, uniqueId);
  }
}
