package frs.broker;

/**
 * The Invoker role in the Broker Pattern.
 * It is responsible for demarshalling the request
 * from the client, invoke the proper method on the proper Servant(s), and return a
 * reply object that encapsulates the result of the method call.
 * <p>
 * The server request handler will call the invoker's handleRequest method after
 * having received a request on the network.
 * <p>
 * To increase robustness, the request's version identity should be supplied
 * as well, in order for the Invoker to follow Poste's principle to be
 * liberal in what you accept.
 */
public interface Invoker {

  /**
   * Handle the incoming request.
   * @param objectId 
   *          the id of the object (in a broad sense) this request is about
   * @param operationName
   *          the name of the operation (method) to call
   * @param payload
   *          the raw payload in the request message, to be demarshalled into
   *          proper parameters 
   * 
   * @return a reply object representing the outcome of the invocation
   */
  ReplyObject handleRequest(String objectId, String operationName, String payload);
}
