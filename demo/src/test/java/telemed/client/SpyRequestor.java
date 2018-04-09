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

package telemed.client;

import java.lang.reflect.Type;

/** This is a Test Spy - it merely notes down what parameters
 * are called for later verification by JUnit test code.
 */
public class SpyRequestor implements frs.broker.Requestor {
  // I leave these package visible, to allow test code
  // to inspect their values!
  String lastObjectId;
  String lastOperationName;
  Object[] lastArgument;
  Type lastType;

  @Override
  public <T> T sendRequestAndAwaitReply(String objectId, String operationName,
                                        Type typeOfReturnValue, Object... argument) {
    lastObjectId = objectId;
    lastOperationName = operationName;
    lastArgument = argument;
    lastType = typeOfReturnValue;
    return null;
  }

  @Override
  public void close() {

  }
}
