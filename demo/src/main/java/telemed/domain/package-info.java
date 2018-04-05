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
    measured in mm(Hg).

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