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

/**
 * The Invoker role in the Broker Pattern.
 * It is responsible for demarshalling the request
 * from the client, invoke the proper method on the proper Servant(s), and return a
 * marshalled reply that encapsulates the result of the method call.
 * <p>
 * The server request handler will call the invoker's handleRequest method after
 * having received a request on the network.
 * <p>
 * To increase robustness, the request's version identity should be supplied
 * as well, in order for the Invoker to follow Postel's principle to be
 * liberal in what you accept.
 */
public interface Invoker {

  /**
   * Handle the incoming request.
   * @param request
   *          the request in the chosen marshalling format
   * @return the reply in the chosen marshalling format
   */

  String handleRequest(String request);

  @Deprecated
  ReplyObject handleRequestDEATHROW(String objectId, String operationName, String payload);
}
