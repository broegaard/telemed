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

package telemed.marshall.json;

import java.util.*;

import com.google.gson.*;
import javax.servlet.http.HttpServletResponse;

import frds.broker.Invoker;
import frds.broker.ReplyObject;

import frds.broker.RequestObject;
import telemed.common.OperationNames;
import telemed.domain.*;
import telemed.storage.XDSException;

/**
 * Implementation of the Invoker role that uses JSON (and the Gson
 * library) for marshalling and demarshalling and is bound to a
 * single instance of a TeleMed servant.
 */
public class TeleMedJSONInvoker implements Invoker {

  private final TeleMed teleMed;
  private final Gson gson;

  public TeleMedJSONInvoker(TeleMed teleMedServant) {
    teleMed = teleMedServant;
    gson = new Gson();
  }

  @Override
  public String handleRequest(String request) {
    // Do the demarshalling
    RequestObject requestObject =
            gson.fromJson(request, RequestObject.class);
    JsonArray array =
            JsonParser.parseString(requestObject.getPayload()).getAsJsonArray();

    ReplyObject reply;

    /* As there is only one TeleMed instance (a singleton)
       the objectId is not used for anything in our case.
     */
    try {
      // Dispatching on all known operations
      // Each dispatch follows the same algorithm
      // a) retrieve parameters from json array (if any)
      // b) invoke servant method
      // c) populate a reply object with return values

      if (requestObject.getOperationName().equals(OperationNames.
              PROCESS_AND_STORE_OPERATION)) {
        // Parameter convention: [0] = TeleObservation
        TeleObservation ts = gson.fromJson(array.get(0),
                TeleObservation.class);

        String uid = teleMed.processAndStore(ts);
        reply = new ReplyObject(HttpServletResponse.SC_CREATED,
                gson.toJson(uid));

      } else if (requestObject.getOperationName().equals(OperationNames.
              GET_OBSERVATIONS_FOR_OPERATION)) {
        // Parameter convention: [0] = patientId
        String patientId = gson.fromJson(array.get(0), String.class);
        // Parameter convention: [1] = time interval
        TimeInterval interval = gson.fromJson(array.get(1),
                TimeInterval.class);

        List<TeleObservation> tol =
                teleMed.getObservationsFor(patientId, interval);
        int statusCode =
                (tol == null || tol.size() == 0) ?
                        HttpServletResponse.SC_NOT_FOUND :
                        HttpServletResponse.SC_OK;
        reply = new ReplyObject(statusCode, gson.toJson(tol));

      } else if (requestObject.getOperationName().equals(OperationNames.
              CORRECT_OPERATION)) {
        // Parameter convention: [0] = patientId
        String patientId = gson.fromJson(array.get(0), String.class);
        // Parameter convention: [1] = tele observation
        TeleObservation to = gson.fromJson(array.get(1),
                TeleObservation.class);

        boolean isValid = teleMed.correct(patientId, to);
        reply = new ReplyObject(HttpServletResponse.SC_OK,
                gson.toJson(isValid));

      } else if (requestObject.getOperationName().equals(OperationNames.
              GET_OBSERVATION_OPERATION)) {
        // Parameter convention: [0] = patientId
        String patientId = gson.fromJson(array.get(0), String.class);

        TeleObservation to = teleMed.getObservation(patientId);
        // If there are no teleobservation to get, make
        // the proper error code
        if (to == null) {
          reply = new ReplyObject(HttpServletResponse.SC_NOT_FOUND,
                  "No teleobservation is stored for patient with id: "
                          + patientId);
        } else {
          reply = new ReplyObject(HttpServletResponse.SC_OK, gson.toJson(to));
        }

      } else if (requestObject.getOperationName().equals(OperationNames.
              DELETE_OPERATION)) {
        // Parameter convention: [0] = patientId
        String patientId = gson.fromJson(array.get(0), String.class);

        boolean isValid = teleMed.delete(patientId);
        reply = new ReplyObject(HttpServletResponse.SC_OK,
                gson.toJson(isValid));
        // More correctly, it should be 204: no contents, but most
        // HTTP libraries will then not send any payload, breaking
        // the requestor code...

      } else {
        // Unknown operation
        reply = new ReplyObject(HttpServletResponse.
                SC_NOT_IMPLEMENTED,
                "Server received unknown operation name: '"
                        + requestObject.getOperationName() + "'.");
      }

    } catch( XDSException e ) {
      reply =
              new ReplyObject(
                      HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                      e.getMessage());
    }

    // And marshall the reply
    return gson.toJson(reply);
  }

}
