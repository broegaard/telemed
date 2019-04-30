package telemed.rest;

import frds.broker.ClientProxy;
import frds.broker.IPCException;
import frds.broker.ipc.http.MimeMediaType;
import telemed.domain.TeleMed;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.mashape.unirest.http.*;
import com.mashape.unirest.http.exceptions.UnirestException;
import telemed.domain.TeleObservation;
import telemed.domain.TimeInterval;
import telemed.ipc.http.Constants;

/**
 * A Proxy for TeleMed based upon REST. As REST handles most of the Broker
 * layers, it makes sense to merge all three roles: Proxy, Requester, and
 * ClientRequestHandler into single abstraction, and that is what is done here.
 *
 * @author Henrik Baerbak Christensen, Aarhus University.
 *
 */
public class TeleMedRESTProxy implements TeleMed, ClientProxy {

    private String baseURL;
    private Gson gson;

    public TeleMedRESTProxy(String hostname, int port) {
        baseURL = "http://"+hostname+":"+port+"/";
        gson = new Gson();
    }

    @Override
    public String processAndStore(TeleObservation teleObs) {
        String payload = gson.toJson(teleObs);
        HttpResponse<JsonNode> jsonResponse = null;

        String path = Constants.BLOODPRESSURE_PATH;
        try {
            jsonResponse = Unirest.post(baseURL+path).
                    header("Accept", MimeMediaType.APPLICATION_JSON).
                    header("Content-type", MimeMediaType.APPLICATION_JSON).
                    body(payload).asJson();
        } catch (UnirestException e) {
            throw new IPCException("UniRest POST failed for 'processAndStore'", e);
        }

        // TODO: Verify returned status code
        int statusCode = jsonResponse.getStatus();

        // String body = jsonResponse.getBody().toString();

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
            jsonResponse = Unirest.get(baseURL+path).
                    header("Accept", MimeMediaType.APPLICATION_JSON).
                    header("Content-type", MimeMediaType.APPLICATION_JSON).asJson();
        } catch (UnirestException e) {
            throw new IPCException("Unitest GET failed", e);
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
        // TODO Implementation of 'getObservationsFor' pending
        return null;
    }


    @Override
    public boolean correct(String uniqueId, TeleObservation teleObs) {
        String payload = gson.toJson(teleObs);
        HttpResponse<JsonNode> jsonResponse = null;

        // PUT on path /bp/{id}
        String path = Constants.BLOODPRESSURE_PATH + uniqueId;
        try {
            jsonResponse = Unirest.put(baseURL+path).
                    header("Accept", MimeMediaType.APPLICATION_JSON).
                    header("Content-type", MimeMediaType.APPLICATION_JSON).
                    body(payload).asJson();
        } catch (UnirestException e) {
            throw new IPCException("Unitest PUT failed", e);
        }

        // TODO: Verify returned status code
        int statusCode = jsonResponse.getStatus();

        // According to RFC 7231, PUT returns 200 OK
        return statusCode == HttpServletResponse.SC_OK;
    }

    @Override
    public boolean delete(String uniqueId) {
        String path = Constants.BLOODPRESSURE_PATH + uniqueId;

        HttpResponse<JsonNode> jsonResponse = null;
        try {
            jsonResponse = Unirest.delete(baseURL+path).
                    header("Accept", MimeMediaType.APPLICATION_JSON).
                    header("Content-type", MimeMediaType.APPLICATION_JSON).
                    asJson();
        } catch (UnirestException e) {
            throw new IPCException("Unitest DELETE failed", e);
        }

        // TODO: Verify returned status code
        int statusCode = jsonResponse.getStatus();

        return statusCode == HttpServletResponse.SC_NO_CONTENT;
    }

}
