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
  This package contains 
  domain objects relevant for the tele medicine domain.
 <p>
 The domain inspiration and part of the code is from the
 Net4Care project: <a href="www.net4care.dk">www.net4care.dk</a>.

  <p>
    The central domain object is the <b>TeleObservation</b> that
    represents a patient's measurement of some vital signs at home.
  <p>
    The present domain model is greatly simplified, only having a
    single measurement type, namely blood pressure. A blood pressure
    measurement contains two measured quantities (instances
    of <b>ClinicalQuantity</b>): Systolic and Diastolic pressure, both
    measured in units of mm(Hg).

  <p>
    The interface <b>TeleMed</b> represents the central role in our
    system in that it supports storing tele observations and executing
    queries for tele observations.
  <p>
    Please note that for the sake of discussing REST the TeleMed allows
    updating and deleting blood pressure measurements - as these
    database operations are part of the REST paradigm. However, in a
    real clinical systems, measurements are never changed nor deleted once
    they are made, rather they are overruled by new events, to keep
    the history of events intact.
*/
package telemed.domain;