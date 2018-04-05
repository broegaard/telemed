package telemed.domain;

import java.time.*;
import java.time.temporal.ChronoUnit;

/**
 * The domain object for a tele observation for blood pressure. This encapsulates
 * a persons identity (which person does this measurements belong to), the time
 * of the observation (in Central European Time), and of course the systolic and
 * diastolic measurements.
<#if type == "code">

<#include "/data/author.txt">
</#if>
 */

public class TeleObservation {
  
  private final String patientId;
  private final ClinicalQuantity systolic;
  private final ClinicalQuantity diastolic;
  private LocalDateTime time;
  
  /** Construct a tele observation for the given patient and the
   * given blood pressure
   * @param patientId id of the patient
   * @param systolic value of systolic blood pressure in mm(Hg)
   * @param diastolic value of diastolic blood pressure in mm(Hg)
   */
  public TeleObservation(String patientId, double systolic, double diastolic) {
    this.patientId = patientId;
    // Timestamp the observation to 'now' but ignore the milliseconds
    this.time = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    // The codes 'MSC...' are part of a Danish telemedic code system.
    this.systolic = new ClinicalQuantity(systolic, "mm(Hg)","MSC88019","Systolic BP");
    this.diastolic = new ClinicalQuantity(diastolic, "mm(Hg)","MSC88020","Diastolic BP");
  }
  
  /**
   * The identity of the person that this measurement has been made on, a social
   * security number, an id from the electronic patient record system, or similar.
   * 
   * @return person identity
   */
  public String getPatientId() {
    return patientId;
  }
 
  /**
   * The time when this observation was made.
   * 
   * @return time of observation.
   */
  public LocalDateTime getTime() {
    return time;
  }

  /**
   * Set the time for this observation, mostly a feature to enable testing.
   * 
   * @param time
   *          the time to set for this observation
   */
  public void setTime(LocalDateTime time) {
    this.time = time.truncatedTo(ChronoUnit.SECONDS);
  }

  /**
   * The systolic blood pressure
   * 
   * @return the systolic blood pressure
   */
  public ClinicalQuantity getSystolic() {
    return systolic;
  }

  /** The diastolic blood pressure 
   * @return the diastolic blood pressure
   * */
  public ClinicalQuantity getDiastolic() {
    return diastolic;
  }
  
  public String toString() {
    return "Blood pressure for ID="+getPatientId()+" Measured=("+getSystolic() + ","+getDiastolic()+") at "+time;
  }
}