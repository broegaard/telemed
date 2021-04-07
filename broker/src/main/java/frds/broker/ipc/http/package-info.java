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

/** This package contains HTTP based delegate implementations of the client and
    server request handler roles, using URI tunneling.  URI tunneling
    means HTTP is used as a pure network transport protocol, all
    method information is hidden in the POST body, and all requests are
    forwarded to the same path on the web server.
*/
package frds.broker.ipc.http;