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
import java.util.Random;
import org.apache.maven.it.VerificationException;
import org.apache.maven.surefire.its.fixture.OutputValidator;
import org.apache.maven.surefire.its.fixture.SurefireJUnit4IntegrationTestCase;
import org.apache.maven.surefire.its.fixture.SurefireLauncher;
import org.apache.maven.surefire.sharedutil.MockTime;
import org.junit.Test;

import static org.junit.Assume.assumeTrue;

import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.experimental.theories.DataPoints;

import org.junit.runner.RunWith;

/**
 * Verifies the runOrder setting and its effect
 *
 * @author Kristian Rosenvold
 */
@RunWith(Theories.class)
public class RunOrderIT
    extends SurefireJUnit4IntegrationTestCase
{
    private static final String[] TESTS_IN_ALPHABETICAL_ORDER = { "TA", "TB", "TC" };

    private static final String[] TESTS_IN_REVERSE_ALPHABETICAL_ORDER = { "TC", "TB", "TA" };

    // cs498dm: Added for parameterized test
    @DataPoints
    public static final String[] RUN_ORDERS = {
        "alphabetical", "reversealphabetical", "random", "hourly", "failedfirst", "balanced", "filesystem"
    };

    // testing random is left as an exercise to the reader. Patches welcome

    // cs498dm: Added for parameterized test
    @Theory
    public void testThatAllRun( String runOrder )
        throws Exception
    {
        OutputValidator validator = executeWithRunOrder( runOrder );
        assertTestNamesAppear( validator, TESTS_IN_ALPHABETICAL_ORDER );
    }

    @Test
    public void testAlphabetical()
        throws Exception
    {
        OutputValidator validator = executeWithRunOrder( "alphabetical" );
        assertTestnamesAppearInSpecificOrder( validator, TESTS_IN_ALPHABETICAL_ORDER );
    }

    @Test
    public void testReverseAlphabetical()
        throws Exception
    {
        OutputValidator validator = executeWithRunOrder( "reversealphabetical" );
        assertTestnamesAppearInSpecificOrder( validator, TESTS_IN_REVERSE_ALPHABETICAL_ORDER );
    }

    @Test
    public void testHourly()
        throws Exception
    {
        MockTime.setCurrentMilliesPropertyToSystem();
        int startHour = Calendar.getInstance().get( Calendar.HOUR_OF_DAY );
        OutputValidator validator = executeWithRunOrder( "hourly" );
        int endHour = Calendar.getInstance().get( Calendar.HOUR_OF_DAY );

        // Otherwise, race condition, cannot test when hour changed mid-run
        assumeTrue( startHour == endHour );

        String[] testnames =
            ( ( startHour % 2 ) == 0 ) ? TESTS_IN_ALPHABETICAL_ORDER : TESTS_IN_REVERSE_ALPHABETICAL_ORDER;
        assertTestnamesAppearInSpecificOrder( validator, testnames );
    }

    @Test
    public void testHourly0()
        throws Exception
    {
        MockTime.setCurrentMilliesProperty( "1429333200000" ); //4/18/15 0:0:0
        OutputValidator validator = executeWithRunOrder( "hourly" );
        String[] testnames = TESTS_IN_ALPHABETICAL_ORDER;
        assertTestnamesAppearInSpecificOrder( validator, testnames );
        MockTime.setCurrentMilliesPropertyToSystem();
    }

    @Test
    public void testHourly23()
        throws Exception
    {
        MockTime.setCurrentMilliesProperty( "1429416000000" ); //4/18/15 23:0:0
        OutputValidator validator = executeWithRunOrder( "hourly" );
        String[] testnames = TESTS_IN_REVERSE_ALPHABETICAL_ORDER;
        assertTestnamesAppearInSpecificOrder( validator, testnames );
        MockTime.setCurrentMilliesPropertyToSystem();
    }

    @Test
    public void testHourly1()
        throws Exception
    {
        MockTime.setCurrentMilliesProperty( "1429336800000" ); //4/18/15 1:0:0
        OutputValidator validator = executeWithRunOrder( "hourly" );
        String[] testnames = TESTS_IN_REVERSE_ALPHABETICAL_ORDER;
        assertTestnamesAppearInSpecificOrder( validator, testnames );
        MockTime.setCurrentMilliesPropertyToSystem();
    }

    @Test
    public void testHourly2()
        throws Exception
    {
        MockTime.setCurrentMilliesProperty( "1429340400000" ); //4/18/15 2:0:0
        OutputValidator validator = executeWithRunOrder( "hourly" );
        String[] testnames = TESTS_IN_ALPHABETICAL_ORDER;
        assertTestnamesAppearInSpecificOrder( validator, testnames );
        MockTime.setCurrentMilliesPropertyToSystem();
    }

    // cs498dm: Added for parameterized test
    public static Random rand = new Random();
    @DataPoints
    public static Long[] timesInMillis() {
        int NUM_CASES = 20;
        Long[] result = new Long[NUM_CASES];
        for (int i = 0; i < result.length; ++i)
        {
            result[i] = rand.nextLong();
            if ( result[i] < 0 )
                result[i] = -result[i];
        }
        return result;
    }

    // cs498dm: Added for parameterized test
    @Theory
    public void testHourlyParameterized ( Long timeInMillis )
        throws Exception
    {
        MockTime.setCurrentMilliesProperty( String.valueOf(timeInMillis) );
        OutputValidator validator = executeWithRunOrder( "hourly" );
        String[] testnames = isEvenHour( timeInMillis ) ?
            TESTS_IN_ALPHABETICAL_ORDER :
            TESTS_IN_REVERSE_ALPHABETICAL_ORDER;
        assertTestnamesAppearInSpecificOrder( validator, testnames );
        MockTime.setCurrentMilliesPropertyToSystem();
    }

    // cs498dm: Added for parameterized test
    private boolean isEvenHour( Long timeInMillis )
    {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timeInMillis);
        return c.get(Calendar.HOUR_OF_DAY) % 2 == 0;
    }

    @Test
    public void testNonExistingRunOrder()
        throws Exception
    {
        unpack().forkMode( getForkMode() ).runOrder( "nonExistingRunOrder" ).maven().withFailure().executeTest().verifyTextInLog(
            "There's no RunOrder with the name nonExistingRunOrder." );
    }

    private OutputValidator executeWithRunOrder( String runOrder )
        throws IOException, VerificationException
    {
        return unpack().forkMode( getForkMode() ).runOrder( runOrder ).executeTest().verifyErrorFree( 3 );
    }

    protected String getForkMode()
    {
        return "once";
    }

    private SurefireLauncher unpack()
    {
        return unpack( "runOrder" );
    }

    private void assertTestnamesAppearInSpecificOrder( OutputValidator validator, String[] testnames )
        throws VerificationException
    {
        if ( !validator.stringsAppearInSpecificOrderInLog( testnames ) )
        {
            throw new VerificationException( "Response does not contain expected item" );
        }
    }

    // cs498dm: Added for parameterized test
    private void assertTestNamesAppear( OutputValidator validator, String[] testnames )
        throws VerificationException
    {
        for ( String testname : testnames )
        {
            validator.verifyTextInLog( testname );
        }
    }
}
