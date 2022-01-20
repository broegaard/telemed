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

package frds.broker;

/**
 * Define version identity/number for the marshalling format,
 * allowing mismatches to be identified and handled; e.g.
 * an old client connecting to an updated server that runs
 * a newer marshalling format.
 *
 */
public class Versioning {
  // Default the marshalling format to version 1.
  public static int MARSHALLING_VERSION = 1;

  /** Set the marshalling format version to a given value.
   *
   * @param versionIdentity the version identity to encode
   *                        as format version in the request
   *                        and reply objects.
   */
  public static void SetMarshallingFormatVersion(int versionIdentity) {
    MARSHALLING_VERSION = versionIdentity;
  }
}
