package telemed.common;

/**
 * The names of the valid operations (i.e. method calls) in the TeleMed system.
<#if type == "code">

<#include "/data/author.txt">
</#if>
 */
public class OperationNames {
  // Method names are prefixed with the type of the method receiver ('telemed') which
  // can be used in when serveral different types of objects are present at the server side
  // and is also helpful in case of failure on the server side where log files can be
  // inspected.
  public static final String PROCESS_AND_STORE_OPERATION = "telemed-process-and-store";
  public static final String GET_OBSERVATIONS_FOR_OPERATION = "telemed-get-observation-for";
  public static final String CORRECT_OPERATION = "telemed-correct";
  public static final String GET_OBSERVATION_OPERATION = "telemed-get-observation";
  public static final String DELETE_OPERATION = "telemed-delete";

}
