package telemed.ipc;

import com.google.gson.Gson;
import frds.broker.*;
import frds.broker.ipc.http.UriTunnelClientRequestHandler;
import frds.broker.ipc.http.UriTunnelServerRequestHandler;
import frds.broker.ipc.socket.SocketClientRequestHandler;
import frds.broker.ipc.socket.SocketServerRequestHandler;
import org.junit.Test;

import javax.servlet.http.HttpServletResponse;

import java.io.Reader;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/** Test that communication crosses the Socket and
 * HTTP based request handlers.
 *
 * These tests are basically integration type tests and
 * spawn threads for server request handlers.
 *
 * To avoid Linux's slow release of a port connection,
 * different port numbers are used across the full
 * test suite, so we never run into the issue of
 * one test case trying to open a connection on a
 * port that a previous test case has just released, but
 * the OS has not made the actual release yet.
 *
 */
public class TestRequestHandler implements Invoker {

  public static final String OBJECT_ID = "52";
  public static final String CLASS_FOO_METHOD = "Foo_operation";

  public static final String MARSHALLED_PAYLOAD = "{contents: \"null\"}";
  public static final String MARSHALLED_REPLY_OBJECT = "{ reply: \"I will be back.\"}";

  private String lastObjectId;
  private String lastOperationName;
  private String lastPayLoad;

  private Gson gson = new Gson();

  // Integration testing: verifying the IPC implementation of the
  // Socket based SRH and CRH
  @Test
  public void shouldVerifySocketIPC() throws InterruptedException {
    // Given a socket based server request handler
    final int portToUse = 37111;
    Invoker invoker = this; // A self-shunt spy (http://xunitpatterns.com/Test%20Spy.html)
    ServerRequestHandler srh = new SocketServerRequestHandler();
    srh.setPortAndInvoker(portToUse, invoker);
    srh.start();
    // Wait for OS to open the port
    Thread.sleep(500);

    // Given a client request handler
    ClientRequestHandler crh = new SocketClientRequestHandler();
    crh.setServer("localhost", portToUse);

    // When we use the CRH to send a request object to the external socket handler
    RequestObject req = new RequestObject(OBJECT_ID, CLASS_FOO_METHOD, MARSHALLED_PAYLOAD);
    ReplyObject reply =
            gson.fromJson(crh.sendToServerAndAwaitReply(gson.toJson(req)),
                    ReplyObject.class);

    // Then our test spy has indeed recorded the request
    assertThat(lastObjectId, is(OBJECT_ID));
    assertThat(lastOperationName, is(CLASS_FOO_METHOD));
    assertThat(lastPayLoad, is(MARSHALLED_PAYLOAD));

    // Then the reply returned is correct
    assertThat(reply.getStatusCode(), is(HttpServletResponse.SC_ACCEPTED));
    assertThat(reply.isSuccess(), is(true));
    assertThat(reply.getPayload(), is(MARSHALLED_REPLY_OBJECT));

    crh.close();
    srh.stop();
  }

  // Integration testing: verifying the IPC implementation of the
  // HTTP based SRH and CRH
  @Test
  public void shouldVerifyHttpIPC() throws InterruptedException {
    // Given SRH and CRH using the UriTunnel variants
    final int portToUse = 32111;
    Invoker invoker = this; // A self-shunt spy
    ServerRequestHandler srh = new UriTunnelServerRequestHandler();
    srh.setPortAndInvoker(portToUse, invoker);
    srh.start();

    Thread.sleep(500);

    ClientRequestHandler crh = new UriTunnelClientRequestHandler();
    crh.setServer("localhost", portToUse);

    // When we send a request
    RequestObject req = new RequestObject(OBJECT_ID, CLASS_FOO_METHOD, MARSHALLED_PAYLOAD);
    ReplyObject reply =
            gson.fromJson(crh.sendToServerAndAwaitReply(gson.toJson(req)),
                    ReplyObject.class);

    // Then the spy has recorded the upcall
    assertThat(lastObjectId, is(OBJECT_ID));
    assertThat(lastOperationName, is(CLASS_FOO_METHOD));
    assertThat(lastPayLoad, is(MARSHALLED_PAYLOAD));

    // Then the reply is correct
    assertThat(reply.getStatusCode(), is(HttpServletResponse.SC_ACCEPTED));
    assertThat(reply.isSuccess(), is(true));
    assertThat(reply.getPayload(), is(MARSHALLED_REPLY_OBJECT));

    crh.close();
    srh.stop();
  }

  @Test
  public void shouldVerifyToStringOutput() {
    // Mostly to increase code coverage by executing the toString method
    ClientRequestHandler crh = new SocketClientRequestHandler();
    crh.setServer("www.baerbak.com", 37888);
    assertThat(crh.toString(), containsString("frds.broker.ipc.socket.SocketClientRequestHandler"));
    assertThat(crh.toString(), containsString("www.baerbak.com:37888"));

    ServerRequestHandler srh = new SocketServerRequestHandler();
    srh.setPortAndInvoker(38777, null);
    assertThat(srh.toString(), containsString("frds.broker.ipc.socket.SocketServerRequestHandler"));
    assertThat(srh.toString(), containsString("38777"));

    crh = new UriTunnelClientRequestHandler();
    crh.setServer("www.baerbak.com", 37888);
    assertThat(crh.toString(), containsString("frds.broker.ipc.http.UriTunnelClientRequestHandler"));
    assertThat(crh.toString(), containsString("www.baerbak.com:37888"));

    srh = new UriTunnelServerRequestHandler();
    srh.setPortAndInvoker(38777, null);
    assertThat(srh.toString(), containsString("frds.broker.ipc.http.UriTunnelServerRequestHandler"));
    assertThat(srh.toString(), containsString("38777"));
  }


  // Use the test case itself as the test spy, a self-shunt
  // (http://xunitpatterns.com/Test%20Spy.html)
  @Override
  public String handleRequest(String request) {
    RequestObject requestObj = gson.fromJson(request, RequestObject.class);
    this.lastObjectId = requestObj.getObjectId();
    this.lastOperationName = requestObj.getOperationName();
    this.lastPayLoad = requestObj.getPayload();
    ReplyObject reply = new ReplyObject(HttpServletResponse.SC_ACCEPTED, MARSHALLED_REPLY_OBJECT);
    return gson.toJson(reply);
  }
}
