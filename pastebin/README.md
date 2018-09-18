PasteBin - a Case Study in the FRDS book
========================================

This is *PasteBin*, a simple case study in using HTTP verbs to handle
information in a web server.

Context
---

Find an explanation to the code base in
the
[Flexible, Reliable, Distributed Software](https://leanpub.com/frds)
book by *Henrik Bærbak Christensen*, Aarhus University.

How to run?
---

Start the web server using Gradle

    gradle pastebin
    
This will start the pastebin server
on [localhost:4567/bin](localhost:4567/bin).
    
    
Next you can use PostMan or Curl to send GET, and POST messages to the
web server. The sequence below is shown using Curl.

### Create a clip (POST)


    csdev@m31:~/proj/frsproject/pastebin$ curl -i -X POST -d '{"contents":"Horse"}' localhost:4567/bin
    HTTP/1.1 201 Created
    Date: Mon, 07 May 2018 09:13:58 GMT
    Location: localhost:4567/bin/100
    Content-Type: application/json
    Transfer-Encoding: chunked
    Server: Jetty(9.4.6.v20170531)

    {"contents":"Horse"}
    
### Retrieve clip (GET)

    csdev@m31:~/proj/frsproject/pastebin$ curl -i localhost:4567/bin/100
    HTTP/1.1 200 OK
    Date: Mon, 07 May 2018 09:15:47 GMT
    Content-Type: application/json
    Transfer-Encoding: chunked
    Server: Jetty(9.4.6.v20170531)

    {"contents":"Horse"}
    
### Retrieve unknown clip (GET)

    csdev@m31:~/proj/frsproject/pastebin$ curl -i localhost:4567/bin/101
    HTTP/1.1 404 Not Found
    Date: Mon, 07 May 2018 09:16:27 GMT
    Content-Type: application/json
    Transfer-Encoding: chunked
    Server: Jetty(9.4.6.v20170531)

    null


