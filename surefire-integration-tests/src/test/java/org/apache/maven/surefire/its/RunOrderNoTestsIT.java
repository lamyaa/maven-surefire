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

import java.io.IOException;
import java.util.Calendar;
import org.apache.maven.it.VerificationException;
import org.apache.maven.surefire.its.fixture.OutputValidator;
import org.apache.maven.surefire.its.fixture.SurefireJUnit4IntegrationTestCase;
import org.apache.maven.surefire.its.fixture.SurefireLauncher;
import org.junit.Test;

/**
 * Verifies the runOrder setting and its effect
 *
 * @author Kristian Rosenvold
 */
public class RunOrderNoTestsIT
    extends SurefireJUnit4IntegrationTestCase
{
    private static final String[] TESTS_IN_ALPHABETICAL_ORDER = { "TA", "TB", "TC" };

    private static final String[] TESTS_IN_REVERSE_ALPHABETICAL_ORDER = { "TC", "TB", "TA" };

    // testing random is left as an exercise to the reader. Patches welcome

    @Test
    public void testAlphabetical()
        throws Exception
    {
        OutputValidator validator = executeWithRunOrder( "alphabetical" );
        validator.verifyErrorFreeLog();
    }

    @Test
    public void testReverseAlphabetical()
        throws Exception
    {
        OutputValidator validator = executeWithRunOrder( "reversealphabetical" );
        validator.verifyErrorFreeLog();
    }

    @Test
    public void testHourly()
        throws Exception
    {
        OutputValidator validator = executeWithRunOrder( "hourly" );
        validator.verifyErrorFreeLog();
    }

    @Test
    public void testRandom()
        throws Exception
    {
        OutputValidator validator = executeWithRunOrder( "random" );
        validator.verifyErrorFreeLog();
    }

    @Test
    public void testBalanced()
        throws Exception
    {
        OutputValidator validator = executeWithRunOrder( "balanced" );
        validator.verifyErrorFreeLog();
    }

    @Test
    public void testFailedFirst()
        throws Exception
    {
        OutputValidator validator = executeWithRunOrder( "failedfirst" );
        validator.verifyErrorFreeLog();
    }
    
    @Test
    public void testNonExistingRunOrder()
        throws Exception
    {
        OutputValidator validator = executeWithRunOrder( "nonexisting" );
        validator.verifyErrorFreeLog();
    }

    private OutputValidator executeWithRunOrder( String runOrder )
        throws IOException, VerificationException
    {
        return unpack().forkMode( getForkMode() ).runOrder( runOrder ).executeTest();
    }

    protected String getForkMode()
    {
        return "once";
    }

    private SurefireLauncher unpack()
    {
        return unpack( "runOrderNoTests" );
    }

}
