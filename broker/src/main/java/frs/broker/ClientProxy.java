package frs.broker;

/**
 * The ClientProxy role in the Broker pattern. This is just a marker interface
 * as the set of methods is defined by which ever interface the proxy
 * implements.
 * <p>
 * Responsibility: To translate any method calls into RequestObjects that are
 * forwarded to a Requestor instance.
 */
public interface ClientProxy {}
