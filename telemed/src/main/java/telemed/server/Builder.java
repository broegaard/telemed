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

package telemed.server;

import telemed.domain.*;

/**
 * The builder role of the Builder pattern, interface for building
 * representations of a tele medical observation.
 */

public interface Builder {
  /**
   * Build the header / wrapper for a representation of a tele observation.
   * 
   * @param to
   *          the tele observation to build a representation for
   */
  void buildHeader(TeleObservation to);

  /**
   * Build the patient information for a representation of a tele observation.
   * 
   * @param to
   *          the tele observation to build a representation for
   */
  void buildPatientInfo(TeleObservation to);

  /**
   * Build the wrapper for a list of measurements of a tele observation.
   * 
   * @param to
   *          the tele observation to build a representation for
   */
  void buildObservationList(TeleObservation to);

  /**
   * Add an observation to the observation list representation of a tele
   * observation.
   * 
   * @param quantity
   *          the clinical quantity to add
   */
  void appendObservation(ClinicalQuantity quantity);
}
