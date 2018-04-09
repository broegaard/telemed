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

package telemed.server;

import java.io.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;
import org.xml.sax.InputSource;

/** A set of utility methods for converting to and from our simplified HL7.
 */
public class XMLUtility {
  
  private static final TransformerFactory transfac = TransformerFactory.newInstance();

  /**
   * Convert an XML document to a human readable string with proper indentation.
   * 
   * @param doc
   *          the XML document to convert
   * @return the string representation of the document.
   */
  public static String convertXMLDocumentToString(Node doc)  {

    Transformer trans = null;
    try {
      trans = transfac.newTransformer();
    } catch ( TransformerException e ) {
      throw new RuntimeException(e);
    }
    trans.setOutputProperty(OutputKeys.INDENT, "yes");
    trans.setOutputProperty( "{http://xml.apache.org/xslt}indent-amount", "2" ); 

    //create string from xml tree
    StringWriter sw = new StringWriter();
    StreamResult result = new StreamResult(sw);
    DOMSource source = new DOMSource(doc);
    try {
      trans.transform(source, result);
    } catch ( TransformerException e ) {
      throw new RuntimeException(e);
    }
    //sw.close();

    String xmlString = sw.toString();

    return xmlString;
  }

  private static final DocumentBuilderFactory factory =
      DocumentBuilderFactory.newInstance();

  /** convert a valid XML string into the equivalent Document object
   * 
   * @param xml well formed XML string
   * @return corresponding Document instance or null of failed
   */
  public static Document convertXMLStringToDocument(String xml)  {
    // Convert the XML string to w3c Document
    Document doc = null;
    try {
      DocumentBuilder builder = factory.newDocumentBuilder();
      doc = builder.parse(new InputSource(new StringReader(xml)));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    catch ( Exception e ) {
      throw new RuntimeException(e);
    }
    return doc;
  }

  /** Get the value of a specific attribute with an enclosing node.
   * Example:
   * <p>
   * The PHMR contains a deeply nested observation node like this
{@code
                <observation classCode="OBS" moodCode="EVN">
                  <templateId root="2.16.840.1.113883.10.20.1.31"/>
                  <templateId root="2.16.840.1.113883.10.20.9.8"/>
                  <code code="20150-9" codeSystem="2.16.840.1.113883.6.1" displayName="FEV1"/>
                  <value unit="L" value="3.42" xsi:type="PQ"/>
               </observation>
}
   * The following test will pass: 
   *  assertEquals( "L", getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc( "unit", 1, "value", "observation", phmrDoc) );
  
   * @param attributeName name of the attribute whose value is returned
   * @param nodeIndex the number of the node named 'nodeName' in the child list of node 'enclosingNodeName'
   * @param nodeName name of the node with the attribute
   * @param enclosingNodeName name of the node that encloses the node
   * @param doc the XML document
   * @return the value of the attribute or null if not found.
   */
  public static String getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc( String attributeName,
                                                                               int nodeIndex, String nodeName,
                                                                               String enclosingNodeName, Document doc) {
    NodeList list = doc.getElementsByTagName(enclosingNodeName);
      NodeList childrenOfEnclosed = list.item(nodeIndex).getChildNodes();
      for ( int j = 0; j < childrenOfEnclosed.getLength(); j++ ) {
        if ( childrenOfEnclosed.item(j).getNodeName().equals(nodeName) ) {
          NamedNodeMap nnm = childrenOfEnclosed.item(j).getAttributes();
          return nnm.getNamedItem(attributeName).getNodeValue();
        }
    }
    return null;
  }
}
