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

/** The server side marshalling implementations based on
 * JSON and GSON library.
 *
 * <p>
 *   The server must be configured with the GameLobbyRootInvoker
 *   which in turn uses sub-invokers for the three roles in
 *   the system.
 * </p>
 *
 * @author Henrik Baerbak Christensen, CS @ AU
 */
package gamelobby.marshall;