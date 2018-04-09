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

package frs.broker;

/**
 * The ServerRequestHandler role in the Broker pattern. This is solely a
 * marker interface as it may be implemented in numerous ways depending upon
 * choice of library/framework for inter-process communication (IPC).
 * <p>
 * Responsibility: To define a specific IPC protocol and listen to any incoming
 * network messages, and forward them to an associated Invoker instance, and
 * return any ReplyObjects from the Invoker to reply messages on the network.
 * It is associated with a ClientRequestHandler on the client side of the
 * network.
 * <p>
 * However implemented, it should always spawn thread(s) to handle incoming
 * network requests.
 */
public interface ServerRequestHandler {

  /** Start the main thread of processing
   * incoming requests.
   */
  void start();

  /** Close the request processing.
   *
   */
  void stop();
}
