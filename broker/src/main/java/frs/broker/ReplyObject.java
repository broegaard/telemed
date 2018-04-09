package frs.broker;

/**
 * Reply object encapsulates the reply from the server - a status code (we reuse
 * the HTTP status codes as they already define a standardized set of codes), a
 * potential error description elaborating the status code, and in case the
 * reply is a valid reply from the server, a payload which is the text returned
 * from the server.
 * <p>
 * The payload needs to be demarshalled by the Requestor to convert it into
 * domain objects and types.
 */
public class ReplyObject {

  private String payload;
  private String errorDescription;
  private final int statusCode;

  /* Include version identity of payload to allow
   * marshalling robustness in future formats.
   */
  private int versionIdentity = Constants.MARSHALLING_VERSION;

  /**
   * Create a reply with the given status code. If the status code represents a
   * valid reply, the description is assigned to the payload, otherwise it is
   * assigned to the error description.
   * 
   * @param statusCode
   *          HTTP status code of the reply
   * @param description
   *          associated text, either the payload or the error description
   */
  public ReplyObject(int statusCode, String description) {
    this.statusCode = statusCode;
    payload = errorDescription = null;
    if (isSuccess())
      payload = description;
    else
      errorDescription = description;
  }

  public boolean isSuccess() {
    // 2xx codes are success
    return statusCode < 300;
  }

  public String getPayload() {
    return payload;
  }

  public String errorDescription() {
    return errorDescription;
  }

  public int getStatusCode() {
    return statusCode;
  }
  
  @Override
  public String toString() {
    return "ReplyObject [payload=" + payload + ", errorDescription="
            + errorDescription + ", responseCode="
            + statusCode + "]";
  }

  public int getVersionIdentity() {
    return versionIdentity;
  }
}
