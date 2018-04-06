package telemed.main;

import frs.broker.ClientRequestHandler;
import frs.broker.Requestor;
import frs.broker.marshall.json.StandardJSONRequestor;
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

  public HomeClientTemplate(String[] args, int port) {
    parseCommandlineParameters(args);

    System.out.println("HomeClient: Asked to do operation "+operation+" for patient "+patientId);

    ClientRequestHandler clientRequestHandler
            = createClientRequestHandler(hostname, port);
    Requestor requestor = new StandardJSONRequestor(clientRequestHandler);
    
    TeleMed ts = new TeleMedProxy(requestor);

    if (operation.equals("store")) {
      TeleObservation to = new TeleObservation(patientId, systolic, diastolic);
      ts.processAndStore(to);
    } else {
      List<TeleObservation> teleObsList = ts.getObservationsFor(patientId, TimeInterval.LAST_WEEK);
      teleObsList.stream().forEach( (to) -> {
        System.out.println(to);
      });
    }
    System.out.println("HomeClient - completed.");
  }

  public abstract ClientRequestHandler createClientRequestHandler(String hostname, int port);

  private void parseCommandlineParameters(String[] args) {
    if (args.length < 5) {
      explainAndFail();
    }

    operation = args[0];
    patientId = args[1];
    systolic = Double.parseDouble(args[2]);
    diastolic = Double.parseDouble(args[3]);
    hostname = args[4];
  }

  private static void explainAndFail() {
    System.out.println("Usage: HomeClient <operation> <pttid> <systolic> <diastolic> <host>");
    System.out.println("    operation := 'store' | 'fetch'");
    System.out.println("      'store' will store bloodpressure on tele med server");
    System.out.println("      'fetch' will fetch last weeks observations");
    System.out.println("    <pptid> is patient ID");
    System.out.println("    <systolic> is systolic blood pressure");
    System.out.println("    <diatolic> is diatolic blood pressure");
    System.out.println("    <host> is name/ip of app server host. Port 37321 is hardwired.");
    System.exit(-1);
  }
  
}