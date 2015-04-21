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

import org.apache.maven.surefire.report.ReportEntry;
import org.apache.maven.surefire.report.SimpleReportEntry;

import junit.framework.TestCase;

/**
 * Testing the method getOutput
 */
public class WrappedReportEntryOutputTest
    extends TestCase
{

    public void testGetOutput()
	throws Exception
    {
        ReportEntry reportEntry = new SimpleReportEntry( "fud", "testSum(surefire.testcase.NonJunitParamsTest)" );
        WrappedReportEntry wr = new WrappedReportEntry( reportEntry, ReportEntryType.FAILURE, 12, null, null );
        final String output = wr.getOutput( false );
	System.out.println( output );
        assertTrue( output.contains( "testSum(surefire.testcase.NonJunitParamsTest)  Time elapsed: 0.012 sec  <<< FAILURE!" ) );
    }


}
