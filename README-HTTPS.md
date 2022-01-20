Enabling HTTPS/secure connections in FRDS.Broker
================================================

The **FRDS.Broker** is a teaching-oriented framework, emphasizing
fundamental concepts rather than a production quality
focus. Therefore, security does not play a significant role.

However, the broker *can* be configured to support TLS/HTTPS
connections for the HTTP based requesthandlers (URI Tunnel Client- and
ServerRequestHandler). (No such option currently exists for the Socket
based request handlers.)

The guide briefly explains how.

Securing server-side connection / ServerRequestHandler
------------------------------------------------------

First, you need to have a *java keystore* file with your certificate,
either issued by a Certificate Authority or created as a self-signed
certificate. It is beyond the scope of this document to outline how
this is done - refer to relevant books or web literature about the
topic.

For now, assume we have a keystore named `/home/csdev/.keystore` with
password `my-password`.

The default way for the FRDS.Broker to get information about the
keyStore is also the official Java way - through the two system
properties:

  * javax.net.ssl.keyStore
  * javax.net.ssl.keyStorePassword

Finally, you have to use the overloaded constructor of the
UriTunnelServerRequestHandler:

    public UriTunnelServerRequestHandler(Invoker invoker, int port, 
      boolean useTLS, String tunnelRoute) {

and provide boolean value 'true' for the 'useTls' parameter.

**OR** use the

    void setPortAndInvoker(int port, Invoker invoker, boolean useTLS);

method, after the default constructor has been called.

Remember that when using the Gradle build system, running an
application is done in *another thread* so assigning system properties
for the gradle build file as a whole will not work; you will have to
include as systemProperty() call inside the gradle task, like this one
below for the TeleMed system:

    task serverHttp(type: JavaExec) {
      group 'demo'
      description 'Run Http/URI Tunnel based TeleMed server'

      classpath sourceSets.test.runtimeClasspath
      main = 'telemed.main.ServerMainHTTP'
      args db

      systemProperty("javax.net.ssl.keyStore",
                       "/home/csdev/.keystore")
      systemProperty("javax.net.ssl.keyStorePassword",
                       "my-password
    }

If any of these are invalid (wrong file or password) the server will
terminate immediately with an exception.

### Testing server-side connection

Start the Telemed HTTP server, setting the parameter 'tls' to 'true':

    gradle :telemed:serverHTTP -Ptls=true
    
And browse to (https://localhost:4567/bp/pid01) (the 'https' is
important!) using your web browser.

*Again remember:* You have to manually change the serverHttp task
to set the system properties (or set them globally for your OS).

If your certificate is self-signed, your browser will issue a warning,
and allow you to review and trust the certificate before proceeding.

Securing client-side connection / ClientRequestHandler
------------------------------------------------------

The class 'UriTunnelClientRequestHandler' has also overloaded
constructor with a parameter 'useTLS', which must be set to 'true' for
secure communication with the server. You may set this for the telemed
server by

  gradle :telemed:homeHttp -Ptls=true
  
Iff the server's certificate is issued by a Certificate Authority,
this should work right away, and the FRDS.Broker connection is secured
by TLS.

However, if you use a self-signed certificate, you need to point the
homeHttp application to a truststore with the certificate, as outlined
in the next section.

### Trusting self-signed certificates used by the server side

You can tell a Java application to use a trust store by setting the
system properties

  * javax.net.ssl.trustStore
  * javax.net.ssl.trustStorePassword

**Do not confuse with the similar looking properties for the server!**
The server expose a certificate in the **keyStore** while the client
trusts certificates in the **trustStore**.

In gradle this boils down to using `systemProperty` in the JavaExec
task, as outlined in this modified task to execute homeHttp:

    task homeHttp(type: JavaExec) {
      group 'demo'
      description 'Run a single TeleMed client operation (Http)'

      classpath sourceSets.test.runtimeClasspath
      main = 'telemed.main.HomeClientHTTP'
      args op, id, sys, dia, host, protocol

      systemProperty("javax.net.ssl.trustStore",
            "/home/csdev/.keystore")
      systemProperty("javax.net.ssl.trustStorePassword",
            "my-password")
    }

Note that the task above reuses the example keystore file and password
of the server. Thus you need to copy and manage that keystore file,
and make it accessible in all client applications.

Also, this *only works* if your application does not contact other web
services using HTTPS. This is the case for the TeleMed 'homeHttp'
appliation, so it poses no issue here.

If your application, however, does access both a FRDS.Broker server
and contacts other servers using HTTPS, the above scheme will not
work, as the keystore only contains our own certificate, not those
trusted by the normal CA. In that case, you must either get a
certificate issued by a CA; or alternatively you may add your own
certificate to the 'cacerts' truststore of Java.





    
    
