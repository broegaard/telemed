package frs.broker.marshall.json;

import java.lang.reflect.Type;
import com.google.gson.Gson;
import frs.broker.*;

/** Implementation of the Requestor role that uses JSON
 * (and the Gson library) for marshaling.

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
public class StandardJSONRequestor implements Requestor {

  private final Gson gson;
  private final ClientRequestHandler clientRequestHandler;

  public StandardJSONRequestor(ClientRequestHandler crh) {
    this.clientRequestHandler = crh;
    this.gson = new Gson();
  }

  @Override
  public <T> T sendRequestAndAwaitReply(String objectId,
                                        String operationName,
      Type typeOfReturnValue, Object... argument) {
    // Marshal all parameters into a JSONArray of
    // potentially mixed types
    String asJson = gson.toJson(argument);

    T returnValue = null;
    RequestObject request =
            new RequestObject(objectId, operationName, asJson);

    // Do the IPC to the server using my client request handler
    ReplyObject replyFrom =
            clientRequestHandler.sendToServer(request);

    // First, verify that the request succeeded
    if (!replyFrom.isSuccess()) {
      throw new IPCException(
          "Failure during client requesting operation '"
                  + operationName
                  + "'. ErrorMessage is: "
                  + replyFrom.errorDescription());
    }
    // Demarshall the reply from the server
    String payload = replyFrom.getPayload();

    // Construct the return value by asking Gson to interpret JSON
    // and make the cast into the generic type T
    if (typeOfReturnValue != null)
      returnValue = gson.fromJson(payload, typeOfReturnValue);
    return returnValue;
  }

  @Override
  public void close() {
    clientRequestHandler.close();
  }

}
