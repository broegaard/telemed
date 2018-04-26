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

/** The superclass of any exceptions occurring during IPC between
 * client and server.
 * <p>
 *   To facilitate communication about root causes, http
 *   status codes can be used using one of the constructors.
 * </p>
 */
public class IPCException extends RuntimeException {

  private static final int NOT_DEFINED = -1;
  private int statusCode;

  public IPCException(int httpStatusCode, String message) {
    super(message);
    statusCode = httpStatusCode;
  }

  public IPCException(String message) {
    super(message);
    statusCode = NOT_DEFINED;
  }

  public IPCException(String message, Throwable exception) {
    super(message, exception);
    statusCode = NOT_DEFINED;
  }

  /** Get the HTTP Status code associated with this
   * exception. In case the failure originated on the
   * server side this may provide further qualification
   * of the type of error. If it has the value NOT_DEFINED
   * then this information is not available.
   * @return HTTP status code or NOT_DEFINED.
   */
  public int getStatusCode() {
    return statusCode;
  }

  private static final long serialVersionUID = 7220436156585793897L;

}
