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

package telemed.domain;

import frds.broker.IPCException;

import java.util.List;

/**
 * The central role in the TeleMed medical system, the application server that
 * supports storing for all the tele observations made by all patients as well
 * as queries. Note that a couple of 'CRUD' operations are included (get,
 * correct, delete) to demonstrate REST based client-server architectures. These
 * are not part of Story 1 and Story 2 in the FRS book initial treatment of
 * the Broker pattern.
 * <p>
 *   The domain and part of the codebase is from Net4Care: www.net4care.dk.
 * <p>
 *   Note that all methods in this interface may throw the unchecked
 *   exception 'IPCException' to signal communication problems on the
 *   network, a server side failure, etc.
 */
public interface TeleMed {

  /**
   * Process a tele observation into the HL7 format and store it 
   * in the XDS database tier.
   * 
   * @param teleObs
   *          the tele observation to process and store
   * @return the id of the stored observation
   * @throws IPCException in case of any IPC problems
   */
  String processAndStore(TeleObservation teleObs);

  /**
   * Retrieve all observations for the given time interval for the 
   * given patient.
   * 
   * @param patientId
   *          the ID of the patient to retrieve observations for
   * @param interval
   *          define the time interval that measurements are 
   *          wanted for
   * @return list of all observations
   * @throws IPCException in case of any IPC problems
   */
  List<TeleObservation> getObservationsFor(String patientId, 
	  TimeInterval interval);

  
  /**
   * Return the tele observation with the assigned ID
   * 
   * @param uniqueId
   *          the unique id of the tele observation
   * @return the tele observation or null in case it is not present
   * @throws IPCException in case of any IPC problems
   */
  TeleObservation getObservation(String uniqueId);

  /**
   * Correct an existing observation, note that the time stamp 
   * changes are ignored
   * 
   * @param uniqueId
   *          id of the tele observation
   * @param to
   *          the new values to overwrite with
   * @return true in case the correction was successful
   * @throws IPCException in case of any IPC problems
   */
  boolean correct(String uniqueId, TeleObservation to);

  /**
   * Delete an observation
   * 
   * @param uniqueId
   *          the id of the tele observation to delete
   * @return true if the observation was found and deleted
   * @throws IPCException in case of any IPC problems
   */
  boolean delete(String uniqueId);
}
