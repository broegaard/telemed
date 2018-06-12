package telemed.ipc;

import frds.broker.*;
import frds.broker.ipc.http.UriTunnelClientRequestHandler;
import frds.broker.ipc.http.UriTunnelServerRequestHandler;
import frds.broker.ipc.socket.SocketClientRequestHandler;
import frds.broker.ipc.socket.SocketServerRequestHandler;
import org.junit.Test;

import javax.servlet.http.HttpServletResponse;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/** Test that communication crosses the Socket and
 * HTTP based request handlers.
 * These tests are more integration type tests and
 * spawn threads for server request handlers.
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

  @Test
  public void shouldVerifyToStringOutput() {
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

  @Test
  public void shouldVerifyHttpIPC() throws InterruptedException {
    // Given
    Invoker invoker = this; // A self-shunt spy
    ServerRequestHandler srh = new UriTunnelServerRequestHandler();
    srh.setPortAndInvoker(7654, invoker);
    srh.start();

    Thread.sleep(500);

    ClientRequestHandler crh = new UriTunnelClientRequestHandler();
    crh.setServer("localhost", 7654);

    // When
    RequestObject req = new RequestObject(OBJECT_ID, CLASS_FOO_METHOD, MARSHALLED_PAYLOAD);
    ReplyObject reply = crh.sendToServer(req);

    // Then
    assertThat(lastObjectId, is(OBJECT_ID));
    assertThat(lastOperationName, is(CLASS_FOO_METHOD));
    assertThat(lastPayLoad, is(MARSHALLED_PAYLOAD));

    assertThat(reply.getStatusCode(), is(HttpServletResponse.SC_ACCEPTED));
    assertThat(reply.isSuccess(), is(true));
    assertThat(reply.getPayload(), is(MARSHALLED_REPLY_OBJECT));

    srh.stop();
  }

  @Test
  public void shouldVerifySocketIPC() throws InterruptedException {
    // Given
    Invoker invoker = this; // A self-shunt spy
    ServerRequestHandler srh = new SocketServerRequestHandler();
    srh.setPortAndInvoker(37111, invoker);
    srh.start();

    Thread.sleep(500);

    ClientRequestHandler crh = new SocketClientRequestHandler();
    crh.setServer("localhost", 37111);

    // When
    RequestObject req = new RequestObject(OBJECT_ID, CLASS_FOO_METHOD, MARSHALLED_PAYLOAD);
    ReplyObject reply = crh.sendToServer(req);

    // Then
    assertThat(lastObjectId, is(OBJECT_ID));
    assertThat(lastOperationName, is(CLASS_FOO_METHOD));
    assertThat(lastPayLoad, is(MARSHALLED_PAYLOAD));

    assertThat(reply.getStatusCode(), is(HttpServletResponse.SC_ACCEPTED));
    assertThat(reply.isSuccess(), is(true));
    assertThat(reply.getPayload(), is(MARSHALLED_REPLY_OBJECT));

    srh.stop();
  }

  @Override
  public ReplyObject handleRequest(String objectId, String operationName, String payload) {
    this.lastObjectId = objectId;
    this.lastOperationName = operationName;
    this.lastPayLoad = payload;
    return new ReplyObject(HttpServletResponse.SC_ACCEPTED, MARSHALLED_REPLY_OBJECT);
  }
}
