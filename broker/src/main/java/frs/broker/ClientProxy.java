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
