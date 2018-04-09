package telemed.domain;

/**
 * A clinical physical quantity, representing a single measured clinical value.
 * <p>
 * A clinical quantity is characterized by a value, a unit, and a id identifying
 * the quantity that has been measured.
 * <p>
 * Example: If I measure that my weight is 77.3 kg then the clinical quantity is
 * (77.3,"kg","weight").
 * <p>
 * Clinically there are libraries of 'codes' that identify the identity of what
 * has been measured, for instance LOINC (loinc.org).
 * <p>
 * Based upon the implementation of PQ from HL7/CDA standard.
 */
public final class ClinicalQuantity {
  private final double value;
  private final String unit;

  /**
   * The code identifying the specific physical quantity measured. Example: FEV1
   * is code 20150-9 in LOINC. Note - the actual code system defining the
   * interpretation of this code is defined by the codeSystem property of the
   * enclosing StandardTeleObservation object.
   */
  private final String code;
  
  /**
   * The displayname of the code. Example: FEV1.
   */
  private final String displayName;
  
  /**
   * Construct a read-only ClinicalQuantity that measures some specific clinical
   * value denoted by its code in some coding system (like UIPAC, LOINC, SNOMED
   * CT, etc.).
   * <p>
   * The unit must be UCUM coded, see the Regenstrief Institute website.
   * 
   * @param value
   *          value, e.g. 200
   * @param unit
   *          unit, e.g. "mg"
   * @param code
   *          the code that identifies the clinical quantity this object
   *          represents, e.g. "20150-9" represents FEV1 in LOINC coding system
   * @param displayName
   *          the human readable name of the clinical quantity, e.g. "FEV1"
   */
  public ClinicalQuantity(double value, String unit,
      String code, String displayName) {
    super();
    this.value = value;
    this.unit = unit;
    this.code = code;
    this.displayName = displayName;
  }
  /**
   * The value measured
   * @return the value of the quantity
   */
  public double getValue() {
    return value;
  }
  /** Get the unit of the measured quantity,
   * in UCUM format
   * @return the unit of the measured quantity
   */
  public String getUnit() {
    return unit;
  }  
  /** Get the code that identifies what quantity
   * has been measured
   * @return the code for the value
   */
  public String getCode() {
    return code;
  }
  /** Get the human readable name of the quantity
   * measured
   * @return the display name
   */
  public String getDisplayName() {
    return displayName;
  }

  public String toString() {
      return getDisplayName()+":"+getValue() + " "+ getUnit();
  }
}
