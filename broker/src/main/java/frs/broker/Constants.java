package frs.broker;

/**
 * Various constants used in for Marshalling.
 *
 */
public class Constants {
  // To allow request handlers to spot version updates of
  // the marshalling format we add version number on
  // it. Thereby code may scan the received raw message
  // from the network to see if the format is proper.
  public static final int MARSHALLING_VERSION = 1;
}
