package frs.broker;

/**
 * The Client Request Handler role in the Broker pattern.
 * It is responsible for all inter-process-communication
 * (IPC) on behalf of client objects. It is called by the Requestor role. It
 * communicates over the network with an associated ServerRequestHandler on the
 * server side.
 * 
 */
public interface ClientRequestHandler {

  /**
   * Send a request, defined by an operation name and a payload (in the chosen
   * marshaling format), to the server's server request handler; await an answer
   * and return a valid reply object. The objectId can be interpreted in a broad
   * sense (not necessarily as the id of 'obj' in 'obj.operation(params)'),
   * depending upon the invoker at the server side.
   * 
   * @param requestObject
   *          the request to send
   * @return a reply from the remote component
   * @throws IPCException
   *           in case some error happened in the IPC
   */
  ReplyObject sendToServer(RequestObject requestObject);

  /**
   * Close the connection to server.
   */
  void close();
}
