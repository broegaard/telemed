package telemed.server;

import telemed.domain.TeleObservation;

/** The Director role of the Builder pattern, allowing
 * representations of tele observations to be built.
 <#if type == "code">

 <#include "/data/author.txt">
 </#if>
 */

public class Director {

  /**
   * Construct a representation of a given tele observation using the provided
   * builder.
   * @param to the tele observation to build a representation for
   * @param builder the build to use during the construction process
   */
  public static void construct(TeleObservation to, Builder builder) {
    builder.buildHeader(to);
    builder.buildPatientInfo(to);
    builder.buildObservationList(to);
    builder.appendObservation(to.getSystolic());
    builder.appendObservation(to.getDiastolic());
  }
}
