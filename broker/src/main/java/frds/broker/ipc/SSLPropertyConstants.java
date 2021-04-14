/*
 * Copyright (C) 2018 - 2021. Henrik Bærbak Christensen, Aarhus University.
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

package frds.broker.ipc;

/** Security system properties, just mimicking the official property names. */
public class SSLPropertyConstants {
  public static final String JAVAX_NET_SSL_KEYSTORE = "javax.net.ssl.keyStore";
  public static final String JAVAX_NET_SSL_KEYSTORE_PASSWORD = "javax.net.ssl.keyStorePassword";
  public static final String JAVAX_NET_SSL_TRUSTSTORE = "javax.net.ssl.trustStore";
  public static final String JAVAX_NET_SSL_TRUSTSTORE_PASSWORD = "javax.net.ssl.trustStorePassword";
}
