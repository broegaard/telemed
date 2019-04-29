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

package telemed.doubles;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

import org.w3c.dom.Document;

import telemed.domain.Utility;
import telemed.storage.*;

/**
 * An fake object implementation of the XDS storage system, that is, it is a
 * lightweight in-memory implementation. (Which of course is not persistent!)
 * <p>
 * It also acts as a 'spy' as the last stored data can be accessed.
 * <p>
 * It is required to have it in the 'src' folder because production code
 * uses this as its storage layer. You may contact Henrik Baerbak for
 * other implementations that uses a real database server.
 * <p>
 * See http://en.wikipedia.org/wiki/Test_double
 */
public class FakeObjectXDSDatabase implements XDSBackend {
  
  private List<Pair> db = new ArrayList<>();

  private Document lastStoredObservation;
  private MetaData lastMetaData;

  // spying on the behavior
  private int countOfProvidedDocuments;
  
  public FakeObjectXDSDatabase() {
    countOfProvidedDocuments = 0;
  }

  class Pair { 
    public final String uniqueId;
    public final MetaData meta;
    public Document doc; 
    public Pair(String uid, MetaData m, Document d) {
      uniqueId = uid;
      meta = m;
      doc = d;
    }
  }

  @Override
  public String provideAndRegisterDocument(MetaData metaData, Document observationAsHL7) {
    lastMetaData = metaData;
    lastStoredObservation = observationAsHL7;

    countOfProvidedDocuments++;
    String uniqueId = "uid-"+countOfProvidedDocuments; 
    // Store the observation in an internal 'database'
    db.add( new Pair(uniqueId, metaData, observationAsHL7));
    return uniqueId;
  }

  /** Spy / retrival interface to get the
   * last stored observation
   * @return last observation that has been stored
   */
  public Document getLastStoredObservation() {
    return lastStoredObservation;
  }

  /** Spy / retrival interface to get the
   * last stored metadata
   * @return last metadata that has been stored
   */
 public MetaData getLastMetaData() {
    return lastMetaData;
  }

  @Override
  public List<Document> retriveDocumentSet(String personID, LocalDateTime startTime,
      LocalDateTime endTime) {
    List<Document> thelist = new ArrayList<>();
    
    Instant instant; Date d;
    
    ZoneId cet = Utility.CET;
    ZonedDateTime start2 = ZonedDateTime.of(startTime, cet);
    instant = start2.toInstant();
    d = Date.from(instant);
    long start = d.getTime();
    
    ZonedDateTime end2 = ZonedDateTime.of(endTime, cet);
    instant = end2.toInstant();
    d = Date.from(instant);
    long end = d.getTime();

    /* Java 8 version, probably not optimal */
    db.stream().
      filter(entry -> {
        MetaData md = entry.meta;
        return (start <= md.getTimestamp() && 
            md.getTimestamp() <= end &&
            personID.equals( md.getPersonID() ));
      }).
      forEach(entry -> thelist.add(entry.doc));
    
    /* Java 7 version of the code :)
    for ( Pair entry : db ) {
      MetaData md = entry.meta;
      if ( start <= md.getTimestamp() && 
          md.getTimestamp() <= end &&
          personID.equals( md.getPersonID() )) {
        thelist.add(entry.doc);
      }
    }
    */
    return thelist;
  }

  @Override
  public Document retriveDocument(String uniqueId) {
    Optional<Pair> foundPairInDB;
    foundPairInDB = findOptionalInDBWithUniqueId(uniqueId);
    Document doc = foundPairInDB.map(pair -> pair.doc).orElse(null);
    return doc;
  }

  private Optional<Pair> findOptionalInDBWithUniqueId(String uniqueId) {
    Optional<Pair> foundPairInDB;
    foundPairInDB = db.stream().
        filter(entry -> entry.uniqueId.equals(uniqueId)).
            findFirst();
    return foundPairInDB;
  }

  @Override
  public boolean correctDocument(String uniqueId, Operation operation, Document doc) {
    Optional<Pair> foundPairInDB;
    foundPairInDB = findOptionalInDBWithUniqueId(uniqueId);
    if (operation == Operation.UPDATE) {
      if (!foundPairInDB.isPresent()) { return false; }
      Pair entry = foundPairInDB.get();
      entry.doc = doc;
    } else if (operation == Operation.DELETE) {
      if (!foundPairInDB.isPresent()) { return false; }
      List<Pair> oneLessList = db.stream().
          filter(entry -> !entry.uniqueId.equals(uniqueId)).
          collect(Collectors.toList());
      db = oneLessList;
    }
    return true;
  }
}
