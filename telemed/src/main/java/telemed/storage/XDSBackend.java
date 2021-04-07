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

package telemed.storage;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.*;

import org.w3c.dom.Document;

/**
 * Facade for the XDS (Cross-Enterprise Document Storage) system. This is a
 * database system that stores XML documents representing the tele observations
 * for patients. XDS operates with two databases: the registry in which meta
 * data is stored for all documents in a set of repositories; and the repository
 * (-ies) that stores the actual documents.
 * <p>
 * This architecture allows scaling, as a single registry can index a vast
 * number of individual repositories that may be distributed geographically,
 * e.g. a single registry can all find health information for a given patient
 * even though the individual repositories with the data are located in
 * different hospitals.
 * <p>
 * The present interface is of course a very scaled down variant of a real XDS
 * interface, especially on the query side. Several minor modification has also
 * been made to suit the case study better. One notable change is that a real
 * XDS does not support changing documents, it is an immutable database.
 */
public interface XDSBackend {
  

  /**
   * Store observation in XMLformat (HL7) in a XDS repository and ensure that
   * the metadata for it is stored in the registry.
   * 
   * @param metaData
   *          the meta data to store in registry to allow queries to be made
   * 
   * @param observationAsHL7
   *          the clinical document in HL7 format that is to be stored in the
   *          repository
   * 
   * @return uniqueId a unique id generated for the document, allows operations
   *         to be applied to that particular document in the repository, see
   *         correctDocument
   */
  String provideAndRegisterDocument(MetaData metaData, Document observationAsHL7);
  
  /**
   * Query the XDS for all documents whose metadata fulfill criteria: A) the id
   * of the person equals personID B) the time interval [start;end]
   * 
   * @param personID
   *          id of the person searched for
   * @param start
   *          begin of time interval
   * @param end
   *          end of time interval
   * @return list of all documents that fulfil criteria
   */
  List<Document> retriveDocumentSet(String personID, OffsetDateTime start, OffsetDateTime end);

  /**
   * Given a uniqueId assigned to a document, fetch that particular document.
   * 
   * @param uniqueId
   *          the id of the document
   * @return the document as it is stored in the repository
   */
  Document retriveDocument(String uniqueId);

  /** Valid corrections of documents using the correctDocument method */
  enum Operation { UPDATE, DELETE }

  /**
   * Correct the document with given uniqueId by applying the given operation.
   * Metadata is not changed. NOTE: XDS does not allow updating existing
   * documents, this method is included as it suits the case study.
   * 
   * @param uniqueId
   *          id of the document
   * @param operation
   *          the operation to apply to that document in the repository
   * @param doc
   *          in case of an update operation, the correct document
   * @return true in case the operation went well. If false, the document was
   *         not found in the repository
   */
  boolean correctDocument(String uniqueId, Operation operation, Document doc);
}
