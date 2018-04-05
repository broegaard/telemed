/**
 * This package contains delegates for the Broker roles that
 * obey the REST architectural style. Please observe that REST
 * addresses many cross cutting aspects of the Broker - as it
 * dictates the IPC, it handles part of the methodname and object id
 * marshalling through the URI path and the Location header field,
 * it dictates return types through HTTP status, it dictates
 * CRUD methods to be handled by HTTP verbs, etc.
 * <p>
 *   Therefore the provided delegates implements several roles
 *   at the same time, of the Broker pattern.
 */
package telemed.rest;