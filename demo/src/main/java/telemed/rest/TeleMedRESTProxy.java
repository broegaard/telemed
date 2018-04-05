package telemed.rest;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.mashape.unirest.http.*;
import com.mashape.unirest.http.exceptions.UnirestException;

import frs.broker.ClientProxy;
import frs.broker.IPCException;
import frs.broker.ipc.http.MimeMediaType;
import telemed.domain.*;
import telemed.ipc.http.Constants;

/**
 * The ClientProxy, implementation of
 * the TeleMed role on the client side. It also serves the
 * Requestor and ClientRequestHandler roles, because the
 * REST architectural pattern handles both marshalling and
 * IPC responsibilities of the Broker
 * pattern. Note that Requestor and ClientRequestHandler are not
 * in the implements list as the methods they require, are not
 * used in the REST case.
 <#if type == "code">

 <#include "/data/author.txt">
 </#if>
 */
public class TeleMedRESTProxy implements TeleMed, ClientProxy {

  private final String baseURL;
  private final Gson gson;

  public TeleMedRESTProxy(String hostname, int port) {
    baseURL = "http://" + hostname + ":" + port + "/";
    gson = new Gson();
  }

  @Override
  public String processAndStore(TeleObservation teleObs) {
    String payload = gson.toJson(teleObs);
    HttpResponse<JsonNode> jsonResponse = null;

    String path = Constants.BLOODPRESSURE_PATH;
    try {
      jsonResponse = Unirest.post(baseURL + path).
          header("Accept", MimeMediaType.APPLICATION_JSON).
          header("Content-Type", MimeMediaType.APPLICATION_JSON).
          body(payload).asJson();
    } catch (UnirestException e) {
      throw new IPCException("POST failed for 'processAndStore'", e);
    }
    
    int statusCode = jsonResponse.getStatus();
    if (statusCode != HttpServletResponse.SC_CREATED) {
      throw new IPCException("POST did not return CREATED but "+statusCode);
    }
    
    // Extract the id of the measurement from the Location header
    String location = jsonResponse.getHeaders().getFirst("Location");
    // Format: URI ending in /bp/{id}, thus let us split on '/'
    // and pick the last entry
    String parts[] = location.split("/");
    String teleObsID = parts[parts.length-1];

    return teleObsID;
  }

  @Override
  public TeleObservation getObservation(String uniqueId) {
    HttpResponse<JsonNode> jsonResponse = null;

    String path = Constants.BLOODPRESSURE_PATH + uniqueId;
    try {
      jsonResponse = Unirest.get(baseURL + path).
          header("Accept", MimeMediaType.APPLICATION_JSON).
          header("Content-Type", MimeMediaType.APPLICATION_JSON).asJson();
    } catch (UnirestException e) {
      throw new IPCException("GET failed", e);
    }
    
    int statusCode = jsonResponse.getStatus();

    TeleObservation teleObs = null;
    if (statusCode == HttpServletResponse.SC_OK) {
      // Demarshal the returned json
      String payload = jsonResponse.getBody().toString();
      teleObs = gson.fromJson(payload, TeleObservation.class);
    }
    return teleObs;
  }

  @Override
  public List<TeleObservation> getObservationsFor(String patientId, TimeInterval interval) {
    // TODO Implementation of 'getObservationsFor' left as an exercise
    return null;
  }

  @Override
  public boolean correct(String uniqueId, TeleObservation teleObs) {
    String payload = gson.toJson(teleObs);
    HttpResponse<JsonNode> jsonResponse = null;

    // PUT on path /bp/{id}
    String path = Constants.BLOODPRESSURE_PATH + uniqueId;
    try {
      jsonResponse = Unirest.put(baseURL + path).
          header("Accept", MimeMediaType.APPLICATION_JSON).
          header("Content-Type", MimeMediaType.APPLICATION_JSON).
          body(payload).asJson();
    } catch (UnirestException e) {
      throw new IPCException("PUT failed", e);
    }
    
    int statusCode = jsonResponse.getStatus();

    return statusCode == HttpServletResponse.SC_OK;
  }

  @Override
  public boolean delete(String uniqueId) {
    String path = Constants.BLOODPRESSURE_PATH + uniqueId;

    HttpResponse<JsonNode> jsonResponse = null;
    try {
      jsonResponse = Unirest.delete(baseURL + path).
          header("Accept", MimeMediaType.APPLICATION_JSON).
          header("Content-Type", MimeMediaType.APPLICATION_JSON).
          asJson();
    } catch (UnirestException e) {
      throw new IPCException("DELETE failed", e);
    }
    
    int statusCode = jsonResponse.getStatus();
    
    return statusCode == HttpServletResponse.SC_NO_CONTENT;
  }

}
