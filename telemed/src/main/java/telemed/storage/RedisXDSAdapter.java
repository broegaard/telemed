/*
 * Copyright (C) 2018 - 2023. Henrik Bærbak Christensen, Aarhus University.
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

package telemed.storage;

import org.w3c.dom.Document;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;
import telemed.server.XMLUtility;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/** Redis implementation of the XDS Backend.
 * Schema for the schema less database :)
 *
 * Primary storage of observations is simple (key,value),
 * where key = (pid)+SEPARATOR+(timestamp).
 *
 * Example: SET pid001@08112018 (hl7)
 *
 * Secondary storage allows queries for given pid and
 * time interval (start, end). The secondary index
 * is a sorted set, where insertion ZADDs an entry
 * under key pid with score (timestamp) and member (key)
 *
 * Example: ZADD pid001 08112018 pid001@08112018
 *
 * Queries are then made using ZRANGEBYSCORE
 *
 * Example: ZRANGEBYSCORE pid001 07112018 08112018
 *
 * Note, real long unix epoch timestamps are used,
 * the above shown are just for clarity.
 *
 */

public class RedisXDSAdapter implements XDSBackend {
  public static final char SEPERATOR = '@';
  // the jedis connection pool..
  private static JedisPool pool = null;

  public RedisXDSAdapter(String host, int port) {
    //configure our pool connection
    pool = new JedisPool(host, port);
  }

  @Override
  public String provideAndRegisterDocument(MetaData metaData, Document observationAsHL7) {
    Jedis jedis = pool.getResource();
    String uniqueKey = null;
    try {
      // Construct unique key
      uniqueKey = metaData.getPersonID() + SEPERATOR + metaData.getTimestamp();
      //save to redis
      jedis.set(uniqueKey, XMLUtility.convertXMLDocumentToString(observationAsHL7));

      // Update secondary index to allow search in time ranges
      jedis.zadd(metaData.getPersonID(), metaData.getTimestamp(), uniqueKey);

    } catch (JedisException e) {
      //if something wrong happen, return it back to the pool
      if (null != jedis) {

        jedis.close();
      }
    } finally {
      ///it's important to return the Jedis instance to the pool once you've finished using it
      if (null != jedis)
        jedis.close();
    }
    return uniqueKey;
  }

  @Override
  public Document retriveDocument(String uniqueId) {
    Jedis jedis = pool.getResource();
    Document document = null;
    try {
      String hl7 = jedis.get(uniqueId);
      if (hl7 != null) {
        document = XMLUtility.convertXMLStringToDocument(hl7);
      }
    } catch (JedisException e) {
      //if something wrong happen, return it back to the pool
      if (null != jedis) {

        jedis.close();
      }
    } finally {
      ///it's important to return the Jedis instance to the pool once you've finished using it
      if (null != jedis)
        jedis.close();
    }
    return document;
  }

  @Override
  public List<Document> retriveDocumentSet(String personID, OffsetDateTime startTime, OffsetDateTime endTime) {
    Jedis jedis = pool.getResource();

    // TODO: Duplicated code, same code is in the FakeObject implementation
    Instant instant;
    Date d;

    // ZoneId cet = ZoneId.of("Europe/Paris");
    ZonedDateTime start2 = startTime.toZonedDateTime();
    instant = start2.toInstant();
    d = Date.from(instant);
    long start = d.getTime();

    ZonedDateTime end2 = endTime.toZonedDateTime();
    instant = end2.toInstant();
    d = Date.from(instant);
    long end = d.getTime();

    List<Document> returnList = new ArrayList<Document>();

    try {
      List<String> setOfId = jedis.zrangeByScore(personID, start, end);
      // System.out.println(" --> retrieved set of # " + setOfId.size());
      // iterate
      for (String key : setOfId) {
        // System.out.println("    -> retrieveing on key: " + key);
        String hl7 = jedis.get(key);
        Document document = XMLUtility.convertXMLStringToDocument(hl7);
        returnList.add(document);
      }

    } catch (JedisException e) {
      //if something wrong happen, return it back to the pool
      if (null != jedis) {
        jedis.close();
      }
    } finally {
      ///it's important to return the Jedis instance to the pool once you've finished using it
      if (null != jedis)
        jedis.close();
    }
    return returnList;
  }

  @Override
  public boolean correctDocument(String uniqueId, Operation operation, Document doc) {
    Jedis jedis = pool.getResource();
    boolean isOk = false;
    try {
      if (operation == Operation.UPDATE) {
        String reply = jedis.set(uniqueId, XMLUtility.convertXMLDocumentToString(doc));
        isOk = reply.equals("OK");
      } else {
        long reply = jedis.del(uniqueId);
        isOk = reply == 1L;
        // Ups, need to update secondary index!
        // System.out.println("  ## Need to remove on unique id: " + uniqueId);
        String pid = uniqueId.substring(0, uniqueId.indexOf(SEPERATOR));
        // System.out.println("  ## Need to remove on key: " + pid);
        String timestamp = uniqueId.substring(uniqueId.indexOf(SEPERATOR) + 1);
        // System.out.println("  ## Need to remove on member: " + timestamp);
        long nextReply = jedis.zrem(pid, uniqueId, timestamp);
        // System.out.println(" --> nextReply = " + nextReply);
      }
    } catch (JedisException e) {
      //if something wrong happen, return it back to the pool
      if (null != jedis) {

        jedis.close();
      }
    } finally {
      ///it's important to return the Jedis instance to the pool once you've finished using it
      if (null != jedis)
        jedis.close();
    }
    return isOk;
  }

  public void dropTheDb(String secret) {
    if (secret.equals("yes-i-am-testing")) {
      Jedis jedis = pool.getResource();
      try {
        jedis.flushAll();
      } catch (JedisException e) {
        //if something wrong happen, return it back to the pool
        if (null != jedis) {

          jedis.close();
        }
      } finally {
        ///it's important to return the Jedis instance to the pool once you've finished using it
        if (null != jedis)
          jedis.close();
      }
    }
  }
}
