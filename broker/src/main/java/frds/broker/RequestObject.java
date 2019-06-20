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

package frds.broker;

import java.util.StringJoiner;

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
  private final int versionIdentity = Versioning.MARSHALLING_VERSION;

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
    return new StringJoiner(", ", RequestObject.class.getSimpleName() + "[", "]")
            .add("operationName='" + operationName + "'")
            .add("payload='" + payload + "'")
            .add("objectId='" + objectId + "'")
            .add("versionIdentity=" + versionIdentity)
            .toString();
  }

  public int getVersionIdentity() {
    return versionIdentity;
  }
}
