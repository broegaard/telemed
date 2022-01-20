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

/**
 * Metadata for a tele observation.
 * Used by the XDS registry to index observations.
 */

public class MetaData {

  private String personID;
  private long timestamp;

  /**
   * Return the id of the person which this
   * metadata is about.
   * @return person identity.
   */
  public String getPersonID() {
    return personID;
  }

  /** Setter for the person identity.
   *
   * @param personID id of person
   */
  public void setPersonID(String personID) {
    this.personID = personID;
  }

  /** Return the timestamp of the tele observation
   * this metadata is about.
   *
   * @return unix epoch timestamp for the
   * associated tele observation
   */
  public long getTimestamp() {
    return timestamp;
  }

  /** Setter for the timestamp of associated
   * tele observation
   * @param timestamp time in unix epoch format
   */
  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  @Override
  public String toString() { 
    return "Metadata ("+getPersonID()+","+getTimestamp()+")";
  }
}
