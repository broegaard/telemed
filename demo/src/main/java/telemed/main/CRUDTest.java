package telemed.main;

import telemed.domain.*;
import telemed.rest.*;

/**
 * A demo of the CRUD operations offered by REST. Requires
 * a running server.
 <#if type == "code">

 <#include "/data/author.txt">
 </#if>
 */
public class CRUDTest {

  private static final String NANCY_PATIENT_ID = "pid01";

  public static void main(String[] args) {
    if (args.length < 2) {
      explainAndDie();
    }
    int port = 4567;

    System.out.println("CRUDTest: Do the Create Read Update Delete operations.");
    String host = args[0];
    boolean noDelete = args[1].equals("true");

    TeleMed teleMed;
    teleMed = new TeleMedRESTProxy(host, port);

    // Next go over a set of CRUD operations, optionally skipping the 'delete'
    TeleObservation teleObs1 = new TeleObservation(NANCY_PATIENT_ID, 120, 75);
    
    // POST 
    System.out.println("Create: Perform a POST of tele observation: \n -> "+teleObs1);
    String id = teleMed.processAndStore(teleObs1);
    System.out.println(" -> was assigned id: "+id);
    
    // GET
    System.out.println("Read: Perform GET of id: "+id);
    TeleObservation to = teleMed.getObservation(id);    
    System.out.println(" -> Returned: "+to);

    // PUT
    System.out.println("Update: Perform PUT of id: "+id);
    to = new TeleObservation(NANCY_PATIENT_ID, 227.0, 91.0);
    boolean isValid = teleMed.correct(id, to);
    System.out.println(" -> outcome: "+isValid);
    
    // validate that contents has changed.
    to = teleMed.getObservation(id);
    System.out.println(" -> New value: "+to);

    boolean doTheDelete = ! noDelete;
    if (doTheDelete) {
      // DELETE
      System.out.println("Delete: Perform DELETE of id: "+id);
      isValid = teleMed.delete(id);
      System.out.println(" -> outcome: "+isValid);

      // validate that no observation exists with that id
      to = teleMed.getObservation(id);
      System.out.println(" -> New value: "+to);
    }
    
    System.out.println("CRUDTest - completed.");
  }

  private static void explainAndDie() {
    System.out.println("Usage: CRUDTest {host} {no-delete}");
    System.out.println("       host = name/ip of host");
    System.out.println("       no-delete = 'true'|'false'; if 'true' the observation will not be deleted");
    System.exit(-1);
  }
}
