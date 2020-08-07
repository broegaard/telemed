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

import java.lang.reflect.Type;

/**
 * The Requestor role in the Broker pattern.
 * <p>
 * Responsibility: To encapsulate marshalling and sending
 * request objects on behalf of the client proxies.
 * The Requestor sends messages using its associated
 * ClientRequestHandler.
 */
public interface Requestor {
  
  /**
   * Marshall the given operation and its parameters into a request object, send
   * it to the remote component, and interpret the answer and convert it back
   * into the return type of generic type T
   * 
   * @param <T>
   *          generic type of the return value
   * @param objectId
   *          the object that this request relates to; not that this may not
   *          necessarily just be the object that the method is called upon
   * @param operationName
   *          the operation (=method) to invoke
   * @param typeOfReturnValue
   *          the java reflection type of the returned type
   * @param arguments
   *          the arguments to the method call
   * @return the return value of the type given by typeOfReturnValue
   */
  <T> T sendRequestAndAwaitReply(String objectId, String operationName,
                                 Type typeOfReturnValue, Object... arguments);

  /**
   * Close the underlying ClientRequestHandler connection.
   */
  void close();
}
