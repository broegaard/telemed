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

package telemed.main;

import frds.broker.ClientRequestHandler;
import frds.broker.Requestor;
import frds.broker.marshall.json.StandardJSONRequestor;
import telemed.client.TeleMedProxy;
import telemed.domain.TeleMed;
import telemed.domain.TeleObservation;
import telemed.domain.TimeInterval;

import java.util.List;

/**
 * Template Method for the main HomeClient application.
 * 
 * @author Henrik Baerbak Christensen, Aarhus University
 *
 */
public abstract class HomeClientTemplate {

  private String operation;
  private double diastolic;
  private double systolic;
  private String patientId;
  private String hostname;
  private String protocol;

  public HomeClientTemplate(String[] args, int port) {
    parseCommandlineParameters(args);

    System.out.println("HomeClient: Asked to do operation "+operation+" for patient "+patientId);

    ClientRequestHandler clientRequestHandler
            = createClientRequestHandler(hostname, port, protocol);
    Requestor requestor = new StandardJSONRequestor(clientRequestHandler);
    
    TeleMed ts = new TeleMedProxy(requestor);

    if (operation.equals("store")) {
      TeleObservation to = new TeleObservation(patientId, systolic, diastolic);
      ts.processAndStore(to);
    } else {
      List<TeleObservation> teleObsList = ts.getObservationsFor(patientId, TimeInterval.LAST_WEEK);
      teleObsList.forEach( (to) -> {
        System.out.println(to);
      });
    }
    System.out.println("HomeClient - completed.");
  }

  public abstract ClientRequestHandler createClientRequestHandler(String hostname, int port, String protocol);

  private void parseCommandlineParameters(String[] args) {
    if (args.length < 6) {
      explainAndFail();
    }

    operation = args[0];
    patientId = args[1];
    systolic = Double.parseDouble(args[2]);
    diastolic = Double.parseDouble(args[3]);
    hostname = args[4];
    protocol = args[5];
  }

  private static void explainAndFail() {
    System.out.println("Usage: HomeClient <operation> <pttid> <systolic> <diastolic> <host> <protocol>");
    System.out.println("    operation := 'store' | 'fetch'");
    System.out.println("      'store' will store bloodpressure on tele med server");
    System.out.println("      'fetch' will fetch last weeks observations");
    System.out.println("    <pptid> is patient ID");
    System.out.println("    <systolic> is systolic blood pressure");
    System.out.println("    <diatolic> is diatolic blood pressure");
    System.out.println("    <host> is name/ip of app server host. Port is hardwired to 37321 (socket) or 4567 (uri tunnel)");
    System.out.println("    <protocol> is either 'http' or 'https'. Only applicable to the uri tunnel variant.");
    System.exit(-1);
  }
  
}
