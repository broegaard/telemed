package frs.broker;

/**
 * The Client Request Handler role in the Broker pattern.
 * It is responsible for all inter-process-communication
 * (IPC) on behalf of client objects. It is called by the Requestor role. It
 * communicates over the network with an associated ServerRequestHandler on the
 * server side.
 * 

   This source code is from the book 
     "Flexible, Reliable Software:
       Using Patterns and Agile Development"
     published by CRC Press.
   Author: 
     Henrik B Christensen 
     Department of Computer Science
     Aarhus University
   
   Please visit http://www.baerbak.com/ for further information.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
 
       http://www.apache.org/licenses/LICENSE-2.0
 
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

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
