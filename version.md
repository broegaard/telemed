FRDS.Broker Library Version History
====

  * Version 1.1: Initial Release.
  
  * Version 1.2: The IPCException may now contain HTTP status code.
  
  * Version 1.3: Empty constructors for the request handlers introduced.

  * Version 1.4: Marshalling format version can now be set.
  
  * Version 1.5: Changed the logging output for
                 URITunnelServerRequestHandler, modified the toString
                 of request and reply objects.
                 
  * Version 1.6: Added response time calculations in
                 URITunnelServerRequestHandler, and output it in logs.

  * Version 1.7: Changed log output format to key-value in 
                 URITunnelServerRequestHandler

  * Version 2.0: Major overhaul of internal broker role interfaces,
                 to clean up the issue that marshalling was partially
                 done in the CRH and SRH. From this version on, marshalling
                 and demarshalling is only made in the Requestor and
                 Invoker layer of the pattern, while CRH and SRH handles
                 Strings only. 
                 THIS VERSION BREAKS THE CRH and INVOKER API. Therefore
                 client code must be rewritten to utilize this version.
