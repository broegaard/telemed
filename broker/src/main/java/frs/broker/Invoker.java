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
