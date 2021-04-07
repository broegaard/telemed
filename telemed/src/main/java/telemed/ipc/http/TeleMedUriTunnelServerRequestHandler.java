/*
 * Copyright (C) 2018 - 2021. Henrik Bærbak Christensen, Aarhus University.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package telemed.ipc.http;

import frds.broker.Invoker;
import frds.broker.ServerRequestHandler;
import frds.broker.ipc.http.UriTunnelServerRequestHandler;
import org.w3c.dom.Document;
import telemed.server.XMLUtility;
import telemed.storage.XDSBackend;

import javax.servlet.http.HttpServletResponse;
import java.time.OffsetDateTime;
import java.util.List;

import static spark.Spark.*;

/** ServerRequestHandler implementation using HTTP and URI Tunneling,
 * as well as web server for General Practitioner overview of
 * measurements for a given patient. The latter circumvent
 * the Broker and fetch stored HL7 XML directly from the
 * storage tier. This is not ideal from a perspective of
 * Layered Architecture (it is 'layer-bridging') nor
 * 'Do-not-talk-to-strangers' but illustrate how
 * raw data is most efficiently fetched.
 * <p>
 * Implementation based on the Spark-Java framework.
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
      OffsetDateTime now = OffsetDateTime.now();
      OffsetDateTime someTimeAgo = now.minusDays(7);

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
