package frs.broker;

/**
 * The Servant role in the Broker pattern. This is solely a marker
 * interface as the set of methods is defined by which ever interface the
 * server side domain object implements.
 * <p>
 * Responsibility: To define the domain implementation on the server side
 * of a role which may be invoked from a remote client. It is called
 * from the Invoker.
 */
public interface Servant {}
