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

/**
  This package contains the main roles and implementations in the
  application server tier. The central class is <b>TeleMedServant</b> which
  implements the application server side of the TeleMed interface.
  <p>
    The Builder/Director interfaces define the Builder pattern for
    constructing representations of TeleObservations in other
    formats, used to convert into the storage format.
  <p>
    Implementations of the Builder allows HL7 and MetaData representations
    to be built.
  <p>Please note that the HL7 format built is not correct, it only
  mimics a small subset of proper HL7
*/
package telemed.server;