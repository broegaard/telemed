/*
 * Copyright (C) 2018-2021. Henrik Bærbak Christensen, Aarhus University.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package frds.broker;

/**
 * The ServerRequestHandler role in the Broker pattern.
 * <p>
 * Responsibility: To define a specific IPC protocol and listen to any incoming
 * network messages, and forward them to an associated Invoker instance, and
 * return Invoker reply messages on the network.
 * It is associated with a ClientRequestHandler on the client side of the
 * network.
 * <p>
 * However implemented, it should in the start() method
 * spawn thread(s) to handle incoming network requests.
 */
public interface ServerRequestHandler {

  /** Start the main thread of processing
   * incoming requests. Will automatically join the
   * main thread.
   */
  void start();

  /** Close the request processing.
   *
   */
  void stop();

  /** Set the port that the request handler should listen
   * to; and define invoker to do the upcall to.
   * @param port the TPC/IP port number to listen to.
   * @param invoker the Invoker to delegate to.
   */
   void setPortAndInvoker(int port, Invoker invoker);


  /** Set the port that the request handler should listen
   * to; and define invoker to do the upcall to.
   * Since v 3.0 of Broker, default method for backward
   * compatibility.
   * @param port the TPC/IP port number to listen to.
   * @param invoker the Invoker to delegate to.
   * @param useTLS if 'true' then TLS communication is requested
   */
  default void setPortAndInvoker(int port, Invoker invoker, boolean useTLS) {
    throw new UnsupportedOperationException("TLS support not available in FRDS.Broker before v 3.0");
  }
}
