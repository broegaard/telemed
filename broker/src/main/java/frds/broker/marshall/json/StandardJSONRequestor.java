/*
 * Copyright (C) 2018 Henrik Bærbak Christensen, baerbak.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package frds.broker.marshall.json;

import java.lang.reflect.Type;
import com.google.gson.Gson;
import frds.broker.*;
import frds.broker.*;

/** Implementation of the Requestor role that uses JSON
 * (and the Gson library) for marshaling.
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
                                        Type typeOfReturnValue,
                                        Object... arguments) {
    // Perform marshalling
    String marshalledArgumentList = gson.toJson(arguments);
    RequestObject request =
            new RequestObject(objectId, operationName, marshalledArgumentList);
    String marshalledRequest = gson.toJson(request);

    // Ask CRH to do the network call
    String marshalledReply =
            clientRequestHandler.sendToServerAndAwaitReply(marshalledRequest);

    // Demarshall reply
    ReplyObject reply = gson.fromJson(marshalledReply, ReplyObject.class);

    // First, verify that the request succeeded
    if (!reply.isSuccess()) {
      throw new IPCException(reply.getStatusCode(),
          "Failure during client requesting operation '"
                  + operationName
                  + "'. ErrorMessage is: "
                  + reply.errorDescription());
    }
    // No errors - so get the payload of the reply
    String payload = reply.getPayload();

    // and demarshall the returned value
    T returnValue = null;
    if (typeOfReturnValue != null)
      returnValue = gson.fromJson(payload, typeOfReturnValue);
    return returnValue;
  }

  @Override
  public void close() {
    clientRequestHandler.close();
  }

}
