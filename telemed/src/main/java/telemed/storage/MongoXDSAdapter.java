package telemed.storage;

import java.time.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.w3c.dom.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.*;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;

import telemed.server.XMLUtility;
import telemed.storage.*;

/** An Adapter that implements the XDSBackend interface and translates
 * storage and query to the Mongo DB format.
 *
 * MongoDB is a NoSQL database system, please
 * consult http://http://www.mongodb.org/.
 * 
 * @author Henrik Baerbak Christensen, Aarhus University.
 *
 */
public class MongoXDSAdapter implements XDSBackend {

  private static final String HL7_KEY = "hl7";
  private static final String TIMESTAMP_KEY = "timestamp";
  private static final String PID_KEY = "pid";
  private MongoClient client;
  private MongoDatabase db;
  private MongoCollection<org.bson.Document> collection;

  /**
   * Construct an XDS that is based upon a MongoDB
   * running on the given hostname and port
   * @param hostname the name of the node on which
   * MongoDB is running
   * @param port the port that MongoDB is listening on
   */
  public MongoXDSAdapter(String hostname, int port) {
    client = new MongoClient(hostname, port);
    // Get database 'xds' and its collection 'tm16'
    db = client.getDatabase("xds");
    collection = db.getCollection("tm16");
  }

  @Override
  public String provideAndRegisterDocument(MetaData metaData,
      Document observationAsHL7) {
    org.bson.Document d = new org.bson.Document();
    d.append(PID_KEY, metaData.getPersonID());
    d.append(TIMESTAMP_KEY, metaData.getTimestamp());
    d.append(HL7_KEY, XMLUtility.convertXMLDocumentToString(observationAsHL7));

    collection.insertOne(d);
    
    // Mongo assigns a unique id, we just return that
    String id = d.get("_id").toString();
    
    return id;
  }

  @Override
  public List<Document> retriveDocumentSet(String personID, OffsetDateTime startTime,
      OffsetDateTime endTime) {
    Instant instant; Date d;
    
    // TODO: Duplicated code, same code is in the FakeObject implementation
    OffsetDateTime start2 = startTime;
    instant = start2.toInstant();
    d = Date.from(instant);
    long start = d.getTime();

    OffsetDateTime end2 = endTime;
    instant = end2.toInstant();
    d = Date.from(instant);
    long end = d.getTime();

    List<Document> returnList = new ArrayList<Document>();
    MongoCursor<org.bson.Document> cursor = 
        collection.
        // Find with given personID and in time interval
        find(
            and(
                eq(PID_KEY, personID),
                    and(
                        gt(TIMESTAMP_KEY, start), 
                        lte(TIMESTAMP_KEY, end)))
            ).
        iterator();
    try {
        while (cursor.hasNext()) {
          org.bson.Document bson = cursor.next();
          String hl7 = bson.getString(HL7_KEY);
          Document doc = XMLUtility.convertXMLStringToDocument(hl7);
          returnList.add(doc);
        }
    } finally {
        cursor.close();
    }
    return returnList;
  }

  @Override
  public Document retriveDocument(String uniqueId) {
    ObjectId asBsonID = new ObjectId(uniqueId);
    org.bson.Document doc = collection.find(eq("_id", asBsonID)).first();
    if ( doc == null ) { return null; }
    String hl7 = doc.getString(HL7_KEY);
    return XMLUtility.convertXMLStringToDocument(hl7);
  }

  @Override
  public boolean correctDocument(String uniqueId, Operation operation,
      Document doc) {
    ObjectId asBsonID = new ObjectId(uniqueId);

    if (operation == Operation.UPDATE) {
      UpdateResult ur =
          collection.updateOne(eq("_id", asBsonID), 
              set(HL7_KEY, XMLUtility.convertXMLDocumentToString(doc)));
      return ur.getModifiedCount() == 1;
    } else { 
      DeleteResult dr = 
          collection.deleteOne(eq("_id", asBsonID));
      return dr.getDeletedCount() == 1;
    }
  }

  
  /** Never-ever use this, it will drop the collection!
   * 
   * @param secret must be set to the secret password
   * to do the actual dropping of the collection.
   */
  public void dropTheDb(String secret) {
    if (secret.equals("yes-i-am-testing")) {
      collection.drop();
    }
  }

}
