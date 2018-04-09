package frs.broker;

/**
 * Request object, a record type that defines the data defining a request. As
 * this format is close to the on-the-wire format, marshalling and demarshalling
 * is pretty straight forward.
 */
public class RequestObject {

  private final String operationName;
  private final String payload;
  private final String objectId;

  /* Include version identity of payload to allow
   * marshalling robustness in future formats.
   */
  private final int versionIdentity = Constants.MARSHALLING_VERSION;

  public RequestObject( String objectId, String operationName, String payload) {
    this.objectId = objectId;
    this.operationName = operationName;
    this.payload = payload;
  }

  public String getOperationName() {
    return operationName;
  }

  public String getPayload() {
    return payload;
  }

  public String getObjectId() {
    return objectId;
  }

  @Override
  public String toString() {
    return "RequestObject{" +
            "operationName='" + operationName + '\'' +
            ", payload='" + payload + '\'' +
            ", objectId='" + objectId + '\'' +
            ", versionIdentity=" + versionIdentity +
            '}';
  }

  public int getVersionIdentity() {
    return versionIdentity;
  }
}
