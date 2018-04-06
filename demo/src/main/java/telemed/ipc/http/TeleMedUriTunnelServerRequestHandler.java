package telemed.ipc.http;

import frs.broker.Invoker;
import frs.broker.ServerRequestHandler;
import frs.broker.ipc.http.UriTunnelServerRequestHandler;
import org.w3c.dom.Document;
import telemed.server.XMLUtility;
import telemed.storage.XDSBackend;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.List;

import static spark.Spark.*;

/** ServerRequestHandler implementation using HTTP and URI Tunneling,
 * as well as web server for General Practitioner overview of
 * measurements for a given patient. The latter circumwent
 * the Broker and fetch stored HL7 XML directly from the
 * storage tier. This is not ideal from a perspective of
 * Layered Architecture (it is 'layer-bridging') nor
 * 'Do-not-talk-to-strangers' but illustrate how
 * raw data is most efficiently fetched.
 * <p>
 * Implementation based on the Spark-Java framework.
<#if type == "code">

<#include "/data/author.txt">
</#if>
 */
public class TeleMedUriTunnelServerRequestHandler
  extends UriTunnelServerRequestHandler
        implements ServerRequestHandler {

  protected final XDSBackend xds;

  /** Create server request handler that is specific for
   * the TeleMed case, as it also reponds to GET requests
   * on the BLOODPRESSURE_PATH - supporting GPs that
   * browse a specific patient's measurements.
   * @param invoker the server side Invoker delegate
   * @param port the port that this server will respond on
   * @param xds the delegate that plays the XDSBackend role
   */
  public TeleMedUriTunnelServerRequestHandler(Invoker invoker,
                                              int port, XDSBackend xds) {
    super(invoker, port, Constants.BLOODPRESSURE_PATH);
    this.xds = xds;
  }

  public void start() {
    // Register and start the superclass' Broker behavior
    super.start();

    // And add web browser fetching of raw HL7 possible

    // GET is used to access all observations for a given patient
    String getRoute = "/" + Constants.BLOODPRESSURE_PATH
            + ":patientId";
    get(getRoute, (req, res) -> {
      String patientId = req.params(":patientId");
      String html =
              "<html><body><h1>TeleMed</h1><h2>Observations for "
                      + patientId + "</h2>";

      List<Document> list;
      // Calculate the time interval to search within
      LocalDateTime now = LocalDateTime.now();
      LocalDateTime someTimeAgo = now.minusDays(7);

      list = xds.retriveDocumentSet(patientId, someTimeAgo, now);
      html += "<H3>There are " + list.size() + " observations.</H3>\n";
      for ( Document doc1 : list ) {
        html += "<hr/><pre>";
        // Sigh - have to convert all < to the &lt; etc.
        String tmp = XMLUtility.convertXMLDocumentToString(doc1);
        tmp = tmp.replaceAll("<", "&lt;");
        tmp = tmp.replaceAll(">", "&gt;");
        html += tmp;
        html += "</pre>";
      }

      html += "</body></html>";
      
      res.status(HttpServletResponse.SC_OK);
      
      return html;
    });
    
  }
}
