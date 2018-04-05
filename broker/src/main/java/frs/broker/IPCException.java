package frs.broker;

/** The superclass of any exceptions occurring during IPC between
 * client and server.

   This source code is from the book 
     "Flexible, Reliable Software:
       Using Patterns and Agile Development"
     published by CRC Press.
   Author: 
     Henrik B Christensen 
     Department of Computer Science
     Aarhus University
   
   Please visit http://www.baerbak.com/ for further information.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
 
       http://www.apache.org/licenses/LICENSE-2.0
 
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
public class IPCException extends RuntimeException {

  public IPCException(String message, Throwable exception) {
    super(message, exception);
  }

  public IPCException(String message) {
    super(message);
  }

  private static final long serialVersionUID = 7220436156585793897L;

}
