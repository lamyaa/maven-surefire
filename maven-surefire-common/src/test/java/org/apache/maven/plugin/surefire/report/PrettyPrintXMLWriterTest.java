package org.apache.maven.plugin.surefire.report;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.StringWriter;
import java.io.PrintWriter;

import java.util.NoSuchElementException;

import junit.framework.TestCase;
import static junit.framework.Assert.*;

import org.apache.maven.plugin.surefire.report.PrettyPrintXMLWriter;

public class PrettyPrintXMLWriterTest
    extends TestCase
{

    // I would do parameterized tests, but this test project is using JUnit 3.
    // Don't want to mess with compat.

    public void testWriteMarkup()
    {
        StringWriter stringWriter = new StringWriter();
        PrettyPrintXMLWriter ppXML = new PrettyPrintXMLWriter(new PrintWriter(stringWriter));
        ppXML.writeMarkup("foobar");
        assertEquals("foobar", stringWriter.toString());
    }

    public void testWriteTextNoMarkup()
    {
        StringWriter stringWriter = new StringWriter();
        PrettyPrintXMLWriter ppXML = new PrettyPrintXMLWriter(new PrintWriter(stringWriter));
        ppXML.writeText("foobar");
        assertEquals("foobar", stringWriter.toString());
    }

    public void testWriteTextWithMarkup()
    {
        StringWriter stringWriter = new StringWriter();
        PrettyPrintXMLWriter ppXML = new PrettyPrintXMLWriter(new PrintWriter(stringWriter));
        ppXML.writeText("<a>");
        assertEquals("&lt;a&gt;", stringWriter.toString());
    }

    public void testWriteInvalidCharacter()
    {
        StringWriter stringWriter = new StringWriter();
        PrettyPrintXMLWriter ppXML = new PrettyPrintXMLWriter(new PrintWriter(stringWriter));
        ppXML.writeText(String.valueOf('\0'));
        assertEquals("&amp;#0;", stringWriter.toString());
    }


    public void testStartElement()
    {
        StringWriter stringWriter = new StringWriter();
        PrettyPrintXMLWriter ppXML = new PrettyPrintXMLWriter(new PrintWriter(stringWriter));
        ppXML.startElement("start");
        String expected = "<start";
        assertEquals(expected, stringWriter.toString());
    }

    public void testEndWithoutFinishTag()
    {
        StringWriter stringWriter = new StringWriter();
        PrettyPrintXMLWriter ppXML = new PrettyPrintXMLWriter(new PrintWriter(stringWriter));
        ppXML.startElement("start");
        ppXML.endElement();
        String expected = "<start/>";
        assertEquals(expected, stringWriter.toString());
    }

    public void testEndNested()
    {
        StringWriter stringWriter = new StringWriter();
        PrettyPrintXMLWriter ppXML = new PrettyPrintXMLWriter(new PrintWriter(stringWriter));
        ppXML.startElement("start");
        ppXML.startElement("inner");
        ppXML.endElement();
        ppXML.startElement("inner");
        ppXML.endElement();
        ppXML.endElement();
        String expected = "<start>\n  <inner/>\n  <inner/>\n</start>";
        assertEquals(expected, stringWriter.toString());
    }

    public void testAddAttribute()
    {
        StringWriter stringWriter = new StringWriter();
        PrettyPrintXMLWriter ppXML = new PrettyPrintXMLWriter(new PrintWriter(stringWriter));
        ppXML.startElement("start");
        ppXML.addAttribute("foo", "bar");
        ppXML.endElement();
        String expected = "<start foo=\"bar\"/>";
        assertEquals(expected, stringWriter.toString());
    }

    public void testSetEncodingShouldThrow()
    {
        StringWriter stringWriter = new StringWriter();
        PrettyPrintXMLWriter ppXML = new PrettyPrintXMLWriter(new PrintWriter(stringWriter));
        try
        {
            ppXML.setEncoding("UTF-8");
        }
        catch ( RuntimeException e )
        {
            assertEquals( "Not Implemented", e.getMessage() );
            return;
        }
        fail( "setEncoding should throw RuntimeException because it is not implemented" );
    }

    public void testSetDocTypeShouldThrow()
    {
        StringWriter stringWriter = new StringWriter();
        PrettyPrintXMLWriter ppXML = new PrettyPrintXMLWriter(new PrintWriter(stringWriter));
        try
        {
            ppXML.setDocType("note");
        }
        catch ( RuntimeException e )
        {
            assertEquals( "Not Implemented", e.getMessage() );
            return;
        }
        fail( "setDocType should throw RuntimeException because it is not implemented" );
    }

    public void testEscaping()
    {
        StringWriter stringWriter = new StringWriter();
        PrettyPrintXMLWriter ppXML = new PrettyPrintXMLWriter(new PrintWriter(stringWriter));
        String[] pairs = {"<", "&lt;", ">", "&gt;", "&", "&amp;", "\"", "&quot;", "\'", "&apos;"};
        for (int i = 0; i < pairs.length; i += 2)
        {
            ppXML.writeText(pairs[i]);
            assertTrue(stringWriter.toString().endsWith(pairs[i + 1]));
        }
    }

    public void testWhitespaceInEscapedTextIsPreserved()
    {
        StringWriter stringWriter = new StringWriter();
        PrettyPrintXMLWriter ppXML = new PrettyPrintXMLWriter(new PrintWriter(stringWriter));
        String testString = "a\n   bc\r\r \td";
        ppXML.writeText(testString);
        assertEquals(testString, stringWriter.toString());
    }

    // could be considered a bug
    public void testEndElementWithoutElement()
    {
        StringWriter stringWriter = new StringWriter();
        PrettyPrintXMLWriter ppXML = new PrettyPrintXMLWriter(new PrintWriter(stringWriter));
        try
        {
            ppXML.endElement();
        }
        catch ( NoSuchElementException e )
        {
            return;
        }
        fail( "ending a non-existant element should throw an exception" );

    }

    // These three tests would exercise the headers of the XML output, but reaching this functionality is infeasible

    // public void testWritesEncodingHeader()
    // {
    //     String encoding = "UTF-8";
    //     StringWriter stringWriter = new StringWriter();
    //     PrettyPrintXMLWriter ppXML = new PrettyPrintXMLWriter(new PrintWriter(stringWriter), encoding, null);
    //     String expected = "<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>\n";
    //     assertEquals(expected, stringWriter.toString());
    // }

    // public void testWritesDocTypeHeader()
    // {
    //     String docType = "note";
    //     StringWriter stringWriter = new StringWriter();
    //     PrettyPrintXMLWriter ppXML = new PrettyPrintXMLWriter(new PrintWriter(stringWriter), null, docType);
    //     String expected = "<?xml version=\"1.0\"?>\n<!DOCTYPE " + docType + ">\n";
    //     assertEquals(expected, stringWriter.toString());
    // }

    // public void testWritesFullHeader()
    // {
    //     String docType = "note";
    //     StringWriter stringWriter = new StringWriter();
    //     PrettyPrintXMLWriter ppXML = new PrettyPrintXMLWriter(new PrintWriter(stringWriter), null, docType);
    //     String expected = "<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>\n<!DOCTYPE " + docType + ">\n";
    //     assertEquals(expected, stringWriter.toString());
    // }
}
