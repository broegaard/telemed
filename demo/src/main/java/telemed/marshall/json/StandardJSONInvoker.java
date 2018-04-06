package telemed.marshall.json;

import java.util.*;

import com.google.gson.*;
import javax.servlet.http.HttpServletResponse;

import frs.broker.*;
import telemed.common.OperationNames;
import telemed.domain.*;
import telemed.storage.XDSException;

/**
 * Implementation of the Invoker role that uses JSON (and the Gson
 * library) for marshalling and demarshalling and is bound to a
 * single instance of a TeleMed servant.
<#if type == "code">

<#include "/data/author.txt">
</#if>
 */
public class StandardJSONInvoker implements Invoker {

  private final TeleMed teleMed;
  private final Gson gson;

  public StandardJSONInvoker(TeleMed teleMedServant) {
    teleMed = teleMedServant;
    gson = new Gson();
  }

  @Override
  public ReplyObject handleRequest(String objectId,
                                   String operationName,
                                   String payloadJSONArray) {
    ReplyObject reply = null;

    /*
     * To support multiple argument methods the parameters are
     * marshalled into a JSONArray of potentially mixed types.
     * This is a bit complex to demarshall, please review the
     * Gson docs + example (RawCollectionsExample) which is
     * the method used here.
     */

    // Demarshall parameters into a JsonArray
    JsonParser parser = new JsonParser();
    JsonArray array =
            parser.parse(payloadJSONArray).getAsJsonArray();
    
    try {
      // Dispatching on all known operations
      // Each dispatch follows the same algorithm
      // a) retrieve parameters from json array (if any)
      // b) invoke servant method
      // c) populate a reply object with return values

      if (operationName.equals(OperationNames.
              PROCESS_AND_STORE_OPERATION)) {
        // Parameter convention: [0] = TeleObservation
        TeleObservation ts = gson.fromJson(array.get(0),
                TeleObservation.class);

        String uid = teleMed.processAndStore(ts);
        reply = new ReplyObject(HttpServletResponse.SC_CREATED,
                gson.toJson(uid));

      } else if (operationName.equals(OperationNames.
              GET_OBSERVATIONS_FOR_OPERATION)) {
        // Parameter convention: [0] = time interval
        TimeInterval interval = gson.fromJson(array.get(0),
                TimeInterval.class);

        List<TeleObservation> tol =
                teleMed.getObservationsFor(objectId, interval);
        int statusCode =
                (tol == null || tol.size() == 0) ?
                        HttpServletResponse.SC_NOT_FOUND :
                        HttpServletResponse.SC_OK;
        reply = new ReplyObject(statusCode, gson.toJson(tol));

      } else if (operationName.equals(OperationNames.
              CORRECT_OPERATION)) {
        // Parameter convention: [0] = tele observation
        TeleObservation to = gson.fromJson(array.get(0),
                TeleObservation.class);

        boolean isValid = teleMed.correct(objectId, to);
        reply = new ReplyObject(HttpServletResponse.SC_OK,
                gson.toJson(isValid));

      } else if (operationName.equals(OperationNames.
              GET_OBSERVATION_OPERATION)) {
        // Parameter: none

        TeleObservation to = teleMed.getObservation(objectId);
        int statusCode = (to == null) ?
                HttpServletResponse.SC_NOT_FOUND :
                HttpServletResponse.SC_OK;
        reply = new ReplyObject(statusCode, gson.toJson(to));

      } else if (operationName.equals(OperationNames.
              DELETE_OPERATION)) {
        // Parameter: none

        boolean isValid = teleMed.delete(objectId);
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
                        + operationName + "'.");
      }

    } catch( XDSException e ) {
      reply = 
          new ReplyObject(
              HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
              e.getMessage());
    }
    return reply;
  }

}