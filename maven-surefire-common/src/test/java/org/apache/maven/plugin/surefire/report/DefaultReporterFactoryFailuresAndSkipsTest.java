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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.apache.maven.plugin.surefire.StartupReportConfiguration;
import org.apache.maven.surefire.report.RunStatistics;
import org.apache.maven.surefire.report.SafeThrowable;
import org.apache.maven.surefire.report.StackTraceWriter;

import static org.apache.maven.plugin.surefire.report.DefaultReporterFactory.TestResultType.error;
import static org.apache.maven.plugin.surefire.report.DefaultReporterFactory.TestResultType.failure;
import static org.apache.maven.plugin.surefire.report.DefaultReporterFactory.TestResultType.flake;
import static org.apache.maven.plugin.surefire.report.DefaultReporterFactory.TestResultType.skipped;
import static org.apache.maven.plugin.surefire.report.DefaultReporterFactory.TestResultType.success;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DefaultReporterFactoryFailuresAndSkipsTest
    extends TestCase
{

    private final static String TEST_ONE = "testOne";

    private final static String TEST_TWO = "testTwo";

    private final static String TEST_THREE = "testThree";

    private final static String TEST_FOUR = "testFour";

    private final static String ASSERTION_FAIL = "assertionFail";

    private final static String ERROR = "error";

    public void testMergeTestHistoryResultWithFailuresAndSkips()
    {
        StartupReportConfiguration reportConfig = new StartupReportConfiguration( true, true, "PLAIN", false, false, new File("target"), false, null, "TESTHASH",
                                                                                                 false, 1 );

        DefaultReporterFactory factory = new DefaultReporterFactory( reportConfig );

        // First run, two tests failed and one passed and one was skipped
        List<TestMethodStats> firstRunStats = new ArrayList<TestMethodStats>();
        firstRunStats.add( new TestMethodStats( TEST_ONE, ReportEntryType.FAILURE, new DummyStackTraceWriter( ASSERTION_FAIL ) ) );
        firstRunStats.add( new TestMethodStats( TEST_TWO, ReportEntryType.SKIPPED, null) );
        firstRunStats.add(
            new TestMethodStats( TEST_THREE, ReportEntryType.FAILURE, new DummyStackTraceWriter( ASSERTION_FAIL ) ) );
        firstRunStats.add(
            new TestMethodStats( TEST_FOUR, ReportEntryType.SUCCESS, null ) );

        // Second run, one passed
        List<TestMethodStats> secondRunStats = new ArrayList<TestMethodStats>();
        secondRunStats.add(
            new TestMethodStats( TEST_ONE, ReportEntryType.FAILURE, new DummyStackTraceWriter( ASSERTION_FAIL ) ) );
        secondRunStats.add(
            new TestMethodStats( TEST_THREE, ReportEntryType.SUCCESS, null ) );


        TestSetRunListener firstRunListener = mock( TestSetRunListener.class );
        TestSetRunListener secondRunListener = mock( TestSetRunListener.class );

        when( firstRunListener.getTestMethodStats() ).thenReturn( firstRunStats );
        when( secondRunListener.getTestMethodStats() ).thenReturn( secondRunStats );

        factory.addListener( firstRunListener );
        factory.addListener( secondRunListener );

        factory.mergeTestHistoryResult();
        RunStatistics mergedStatistics = factory.getGlobalRunStatistics();

        // Only TEST_THREE is a failing test, other three are flaky tests
        assertEquals( 4, mergedStatistics.getCompletedCount() );
        assertEquals( 0, mergedStatistics.getErrors() );
        assertEquals( 1, mergedStatistics.getFailures() );
        assertEquals( 1, mergedStatistics.getFlakes() );
        assertEquals( 1, mergedStatistics.getSkipped() );

    }

    static class DummyStackTraceWriter
        implements StackTraceWriter
    {

        private final String stackTrace;

        public DummyStackTraceWriter( String stackTrace )
        {
            this.stackTrace = stackTrace;
        }

        public String writeTraceToString()
        {
            return "";
        }

        public String writeTrimmedTraceToString()
        {
            return "";
        }

        public String smartTrimmedStackTrace()
        {
            return stackTrace;
        }

        public SafeThrowable getThrowable()
        {
            return null;
        }
    }
}
