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
