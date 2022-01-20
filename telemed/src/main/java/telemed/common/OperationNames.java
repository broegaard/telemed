/*
 * Copyright (C) 2018-2021. Henrik Bærbak Christensen, Aarhus University.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package telemed.common;

/**
 * The names of the valid operations (i.e. method calls) in the TeleMed system.
 */
public class OperationNames {
  // Method names are prefixed with the type of the method receiver ('telemed') which
  // can be used in when serveral different types of objects are present at the server side
  // and is also helpful in case of failure on the server side where log files can be
  // inspected.
  public static final String PROCESS_AND_STORE_OPERATION = "telemed-process-and-store";
  public static final String GET_OBSERVATIONS_FOR_OPERATION = "telemed-get-observation-for";
  public static final String CORRECT_OPERATION = "telemed-correct";
  public static final String GET_OBSERVATION_OPERATION = "telemed-get-observation";
  public static final String DELETE_OPERATION = "telemed-delete";

}
