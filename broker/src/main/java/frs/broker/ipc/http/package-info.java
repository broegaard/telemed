/** This package contains HTTP based delegate implementations of the client and
    server request handler roles, using URI tunneling.  URI tunneling
    means HTTP is used as a pure network transport protocol, all
    method information is hidden in the POST body, and all requests are
    forwarded to the same path on the web server.
*/
package frs.broker.ipc.http;