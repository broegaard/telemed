/*
 * Copyright (C) 2018 Henrik Bærbak Christensen, baerbak.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package telemed.domain;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * The domain object for a tele observation for blood pressure. This encapsulates
 * a persons identity (which person does this measurements belong to), the time
 * of the observation, and of course the systolic and diastolic measurements.
 */

public class TeleObservation {
  
  private final String patientId;
  private final ClinicalQuantity systolic;
  private final ClinicalQuantity diastolic;

  /* One unfortunate implementation detail sneaks into this domain object,
    namely that Gson cannot correctly deserialize OffsetDateTime objects.
    Therefore, we choose a ISO 8601 string representation to avoid the
    issue.
   */
  private String timeAsISO8601;
  
  /** Construct a tele observation for the given patient and the
   * given blood pressure
   * @param patientId id of the patient
   * @param systolic value of systolic blood pressure in mm(Hg)
   * @param diastolic value of diastolic blood pressure in mm(Hg)
   */
  public TeleObservation(String patientId, double systolic, double diastolic) {
    this.patientId = patientId;
    // Timestamp the observation to 'now' but ignore the milliseconds
    setTime(OffsetDateTime.now());
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
  public OffsetDateTime getTime() {
    OffsetDateTime time = OffsetDateTime.parse(timeAsISO8601);
    return time;
  }

  /**
   * Set the time for this observation, mostly a feature to enable testing.
   * 
   * @param time
   *          the time to set for this observation
   */
  public void setTime(OffsetDateTime time) {
    OffsetDateTime time2 = time.truncatedTo(ChronoUnit.SECONDS);
    this.timeAsISO8601 = time2.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
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
    return "Blood pressure for ID="+getPatientId()+" Measured=("+getSystolic() + ","+getDiastolic()+") at "+timeAsISO8601;
  }
}