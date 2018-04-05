package telemed.rest;

import static spark.Spark.*;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.google.gson.*;

import frs.broker.*;
import frs.broker.ipc.http.MimeMediaType;
import telemed.domain.*;
import telemed.ipc.http.Constants;

/** The REST version of the server request handler AND the invoker role of
 * the Broker pattern. It also implements the Invoker role, but it is not
 * mentioned in the implements list as the 'handleRequest' method is only
 * indirectly implemented (through registering routes in Spark).
 <#if type == "code">

 <#include "/data/author.txt">
 </#if>
 */
public class SparkJavaServerRequestHandlerAndInvoker
        implements ServerRequestHandler {

  private final TeleMed teleMed;
  private final int port;
  private String lastVerb;
  private int lastStatusCode;
  private final Gson gson;

  public SparkJavaServerRequestHandlerAndInvoker(int portNumber,
                                                 TeleMed tsServant) {
    this.teleMed = tsServant;
    this.port = portNumber;
    
    gson = new Gson();
  }

  public void registerRoutes() {
    // Set the port to listen to
    port(port);
    
    // POST = processAndStore
    String storeRoute = "/" + Constants.BLOODPRESSURE_PATH;
    post(storeRoute, (req,res) -> {
      lastVerb = req.requestMethod();

      // Demarshall the body into the teleobservation posted
      String body = req.body();
      TeleObservation teleObs = gson.fromJson(body, TeleObservation.class);

      // Upcall to servant
      String id = teleMed.processAndStore(teleObs);

      // Set the CREATED status code
      int statusCode = HttpServletResponse.SC_CREATED;
      res.status(statusCode);
      res.type(MimeMediaType.APPLICATION_JSON);

      // Location = URL of created resource
      res.header("Location", req.host() + "/" + Constants.BLOODPRESSURE_PATH + id);

      lastStatusCode = statusCode;
      // Return the tele observation as confirmation
      return gson.toJson(teleObs);
    });
    
    
    // GET = getObservation
    String getRoute = "/" + Constants.BLOODPRESSURE_PATH + ":id";

    get(getRoute, (req,res) -> {
      String uniqueId = req.params(":id");
      
      lastVerb = req.requestMethod();

      TeleObservation teleObs = teleMed.getObservation(uniqueId);
      
      String returnValue = null;
      
      if (teleObs == null) {
        lastStatusCode = HttpServletResponse.SC_NOT_FOUND;
        returnValue = "{}"; // a null JSON object
      } else {
        lastStatusCode = HttpServletResponse.SC_OK;
        returnValue = gson.toJson(teleObs);
      }
      
      res.status(lastStatusCode);
      res.type(MimeMediaType.APPLICATION_JSON);

      return returnValue;
    });

    // PUT = correct
    String correctRoute = "/" + Constants.BLOODPRESSURE_PATH + ":id";
    
    put(correctRoute, (req,res) -> {
      String body = req.body();
      String uniqueId = req.params(":id");

      lastVerb = req.requestMethod();

      TeleObservation teleObs = gson.fromJson(body, TeleObservation.class);

      boolean isValid = teleMed.correct(uniqueId, teleObs);
      
      lastStatusCode = HttpServletResponse.SC_OK;
      if (!isValid) {
        lastStatusCode = HttpServletResponse.SC_NOT_FOUND;
      }
      
      // Normally: 200 OK
      res.status(lastStatusCode);
      res.type(MimeMediaType.APPLICATION_JSON);
 
      return "{}";
    });

    // DELETE = delete
    String deleteRoute = "/" + Constants.BLOODPRESSURE_PATH + ":id";
    delete(deleteRoute, (req,res) -> {
      String uniqueId = req.params(":id");

      lastVerb = req.requestMethod();

      boolean isValid = teleMed.delete(uniqueId);

      lastStatusCode = HttpServletResponse.SC_NO_CONTENT;
      if (!isValid) {
        lastStatusCode = HttpServletResponse.SC_NOT_FOUND;
      }
      
      res.status(lastStatusCode);
      res.type(MimeMediaType.APPLICATION_JSON);
 
      return "{}";
    });

    // GET HTML = getObservationFor
    String getHtmlRoute = "/bloodpressure/:patientId";

    get(getHtmlRoute, (req,res) -> {
      String patientId = req.params(":patientId");
      
      lastVerb = req.requestMethod();

      List<TeleObservation> theList = teleMed.getObservationsFor(patientId, TimeInterval.LAST_DAY);
      
      lastStatusCode = HttpServletResponse.SC_OK;
      
      res.status(lastStatusCode);
      
      String returnValue = "<h1>Observations for "+patientId+"</h1>\n";
      returnValue += "<ol>";
      for( TeleObservation to : theList ) {
        returnValue += "<li>"+to.toString()+"</li>"; 
      }
      returnValue += "</ol>";
            
      return returnValue;
    });

  }

  public void closedown() {
    stop();
  }

  public int lastStatusCode() {
    return lastStatusCode;
  }

  public String lastHTTPVerb() {
    return lastVerb;
  }

}
