package org.apache.maven.surefire.its;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.maven.surefire.its.fixture.OutputValidator;
import org.apache.maven.surefire.its.fixture.SurefireJUnit4IntegrationTestCase;
import org.apache.maven.surefire.its.fixture.SurefireLauncher;
import org.apache.maven.surefire.its.fixture.TestFile;
import org.junit.BeforeClass;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theory;

/**
 * Test forkMode with Theories
 */
@RunWith(Theories.class)
public class ForkModeParameterizedIT
    extends SurefireJUnit4IntegrationTestCase
{

    private OutputValidator outputValidator;

    public static final int NUM_CLASSES_IN_TEST_PROJECT = 6;

    @DataPoints
    public static int[] threadCounts = {2, 3, 4, 5, 6, 7, 8};

    @BeforeClass
    public static void installDumpPidPlugin()
        throws Exception
    {
        unpack( ForkModeParameterizedIT.class, "test-helper-dump-pid-plugin", "plugin" ).executeInstall();
    }

    @Theory
    public void testForkModeOncePerThreadNThreads(int numThreads)
    {
        // Reduced the sleepLength so the test would pass as expected for higher numThreads.
        String[] pids =
            doTest( unpack( getProject() ).forkOncePerThread().threadCount( numThreads ).addGoal( "-DsleepLength=1200" ) );
        assertTrue( numDifferentPids( pids ) <= numThreads );
        assertTrue( numDifferentPids( pids ) >= 2 );
        assertFalse( "pid 1 is not the same as the main process' pid", pids[0].equals( getMainPID() ) );
    }

    @Theory
    public void testForkCountMultipleNoReuse( int numForks )
    {
        String[] pids =
            doTest( unpack( getProject() ).forkCount( numForks ).reuseForks( false ).addGoal( "-DsleepLength=1200" ) );
        assertDifferentPids( pids );
        assertFalse( "pid 1 is not the same as the main process' pid", pids[0].equals( getMainPID() ) );
    }

    @Theory
    public void testForkCountMultipleReuse( int numForks )
    {
        // Had to reduce sleepLength to get the desired effect. Probably flaky.
        String[] pids =
            doTest( unpack( getProject() ).forkCount( numForks ).reuseForks( true ).addGoal( "-DsleepLength=1200" ) );
        assertTrue( numDifferentPids( pids ) <= numForks );
        assertTrue( numDifferentPids( pids ) >= 2 );
        assertFalse( "pid 1 is not the same as the main process' pid", pids[0].equals( getMainPID() ) );
    }

    private int numDifferentPids( String[] pids )
    {
        return new HashSet<String>( Arrays.asList( pids ) ).size();
    }

    private void assertDifferentPids( String[] pids, int numOfDifferentPids )
    {
        assertEquals( "number of different pids is not as expected", numOfDifferentPids, numDifferentPids( pids ) );
    }

    private void assertDifferentPids( String[] pids )
    {
        assertDifferentPids( pids, pids.length );
    }

    private String getMainPID()
    {
        final TestFile targetFile = outputValidator.getTargetFile( "maven.pid" );
        String pid = targetFile.slurpFile();
        return pid + " testValue_1_1";
    }

    private String[] doTest( SurefireLauncher forkMode )
    {
        return doTest( forkMode, NUM_CLASSES_IN_TEST_PROJECT );
    }

    private String[] doTest( SurefireLauncher forkMode, int numClasses )
    {
        forkMode.sysProp( "testProperty", "testValue_${surefire.threadNumber}_${surefire.forkNumber}" );
        forkMode.addGoal( "org.apache.maven.plugins.surefire:maven-dump-pid-plugin:dump-pid" );
        outputValidator = forkMode.executeTest();
        outputValidator.verifyErrorFreeLog().assertTestSuiteResults( numClasses, 0, 0, 0 );
        String[] pids = new String[numClasses];
        for ( int i = 1; i <= pids.length; i++ )
        {
            final TestFile targetFile = outputValidator.getTargetFile( "test" + i + "-pid" );
            String pid = targetFile.slurpFile();
            pids[i - 1] = pid;
        }
        return pids;
    }

    protected String getProject()
    {
        return "fork-mode-parameterized";
    }

}
