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