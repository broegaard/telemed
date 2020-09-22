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

import java.time.*;

import telemed.domain.*;
import telemed.storage.MetaData;

/**
 * A builder to build metadata for a tele observation.
 */
public class MetadataBuilder implements Builder {
  private MetaData metadata;

  @Override
  public void buildHeader(TeleObservation to) {
    metadata = new MetaData();
  }

  @Override
  public void buildPatientInfo(TeleObservation to) {
    metadata.setPersonID( to.getPatientId() );

    // We need to convert the time to a epoch in milliseconds
    OffsetDateTime dateTime = to.getTime();
    // And then to an instant
    Instant instant = dateTime.toInstant();
    long timestamp = instant.toEpochMilli();
    metadata.setTimestamp(timestamp);
  }

  @Override
  public void buildObservationList(TeleObservation to) {
    // not relevant for metadata
  }

  @Override
  public void appendObservation(ClinicalQuantity quantity) {
    // not relevant for metadata
  }

  public MetaData getResult() {
    return metadata;
    
  }
}
