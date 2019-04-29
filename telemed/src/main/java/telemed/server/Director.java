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

package telemed.server;

import telemed.domain.TeleObservation;

/** The Director role of the Builder pattern, allowing
 * representations of tele observations to be built.
 */

public class Director {

  /**
   * Construct a representation of a given tele observation using the provided
   * builder.
   * @param to the tele observation to build a representation for
   * @param builder the build to use during the construction process
   */
  public static void construct(TeleObservation to, Builder builder) {
    builder.buildHeader(to);
    builder.buildPatientInfo(to);
    builder.buildObservationList(to);
    builder.appendObservation(to.getSystolic());
    builder.appendObservation(to.getDiastolic());
  }
}
