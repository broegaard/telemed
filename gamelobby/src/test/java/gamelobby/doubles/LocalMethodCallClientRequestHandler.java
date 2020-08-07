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

package gamelobby.doubles;

import frds.broker.ClientRequestHandler;
import frds.broker.Invoker;
import frds.broker.ReplyObject;
import frds.broker.RequestObject;

/**
 * A test double implementation of the ClientRequestHandler which simply
 * forwards any calls directly to an associated invoker, thus allowing the full
 * stack of remote calls implementations to be tested without the need of real
 * IPC.
 * <p>
 * Note that no ServerRequestHandler is involved as the server side IPC is
 * 'nothing' in case of normal method calls.
 * <p>
 *   Also acts as a spy to allow inspecting the request and reply objects
 *   being passed.
 <#if type == "code">

 <#include "/data/author.txt">
 </#if>
 */
public class LocalMethodCallClientRequestHandler implements ClientRequestHandler {

  private final Invoker invoker;

  public LocalMethodCallClientRequestHandler(Invoker invoker) {
    this.invoker = invoker;
  }

  @Override
  public String sendToServerAndAwaitReply(String request) {
    return invoker.handleRequest(request);
  }

  @Override
  public void setServer(String hostname, int port) {
    // not used
  }

  @Override
  public void close() {

  }
}
