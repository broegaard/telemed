package frs.broker;

/**
 * Various constants used in for Marshalling.
 *

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
public class Constants {
  // To allow request handlers to spot version updates of
  // the marshalling format we add version number on
  // it. Thereby code may scan the received raw message
  // from the network to see if the format is proper.
  public static final int MARSHALLING_VERSION = 1;
}
