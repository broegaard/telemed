package telemed.doubles;

import frs.broker.*;

/**
 * A test double implementation of the ClientRequestHandler which simply
 * forwards any calls directly to an associated invoker, thus allowing the full
 * stack of remote calls implementations to be tested without the need of real
 * IPC.
 * <p>
 * Note that no ServerRequestHandler is involved as the server side IPC is
 * 'nothing' in case of normal method calls.
 * <p>
 *   Also acts as a spy to allow inspecting the request and reply objects
 *   being passed.
 <#if type == "code">

 <#include "/data/author.txt">
 </#if>
 */
public class LocalMethodCallClientRequestHandler implements ClientRequestHandler {

  private final Invoker invoker;
  private ReplyObject lastReply;
  private RequestObject lastRequest;

  public LocalMethodCallClientRequestHandler(Invoker invoker) {
    this.invoker = invoker;
  }

  @Override
  public ReplyObject sendToServer(RequestObject requestObject) {
    lastRequest = requestObject;
    // The send to the server can be mimicked by a direct method call
    lastReply = invoker.handleRequest(requestObject.getObjectId(), 
        requestObject.getOperationName(), 
        requestObject.getPayload());
    return lastReply;
  }

  @Override
  public void close() {

  }

  public ReplyObject getLastReply() {
    return lastReply;
  }

  public RequestObject getLastRequest() {
    return lastRequest;
  }

}
