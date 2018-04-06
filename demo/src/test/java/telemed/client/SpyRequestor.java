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
