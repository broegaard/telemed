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

package saip.main;

import telemed.domain.TeleMed;
import telemed.domain.TeleObservation;
import telemed.domain.TimeInterval;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/** A decorator which introduces the PEHack, that is,
 * every timestamp from the client is overwritten with
 * current time. This is because our JMeter tool will
 * send identical payloads (time is fixed in all
 * measurements) which will severely mess up the
 * retrieval scenario.
 */

public class PEHackDecorator implements TeleMed {
  @Override
  public String processAndStore(TeleObservation teleObs) {
    // Performance testing hack: Overwrite client side
    // time stamp with present time
    teleObs.setTime(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

    return delegate.processAndStore(teleObs);
  }

  @Override
  public List<TeleObservation> getObservationsFor(String patientId, TimeInterval interval) {
    return delegate.getObservationsFor(patientId, interval);
  }

  @Override
  public TeleObservation getObservation(String uniqueId) {
    return delegate.getObservation(uniqueId);
  }

  @Override
  public boolean correct(String uniqueId, TeleObservation to) {
    return delegate.correct(uniqueId, to);
  }

  @Override
  public boolean delete(String uniqueId) {
    return delegate.delete(uniqueId);
  }

  private final TeleMed delegate;

  public PEHackDecorator(TeleMed tsServant) {
    delegate = tsServant;
  }
}
