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

    // We need to convert the local time to CET ZonedDateTime
    ZonedDateTime zdt = to.getTime().atZone(Utility.CET);
    // And then to an instant
    Instant instant = zdt.toInstant();
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
