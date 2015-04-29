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
import org.apache.maven.surefire.sharedutil.MockTime;
import org.junit.Test;

public class RunOrderPDCIT
    extends SurefireJUnit4IntegrationTestCase
{
    private static final String[] PDC1 = { "ABCDEFGH" };
    private static final String[] PDC2 = { "ABCDEFGH", "IJKLMNOP"};
    private static final String[] PDC3 = { "QRSTUVWX", "IJKLMNOP", "ABCDEFGH"};
    private static final String[] PDC4 = { "ABCDEFGH", "IJKLMNOP", "QRSTUVWX", "YZ_abcd"};
    private static final String[] PDC5 = { "ABCDEFGH", "IJKLMNOP", "QRSTUVWX", "YZ_abcd", "efghijkm"};
    private static final String[] PDC6 = { "ABCDEFGH", "IJKLMNOP", "QRSTUVWX", "YZ_abcd", "efghijkm", "nopqrstu"};
    private static final String[] PDC7 = { "ABCDEFGH", "IJKLMNOP", "QRSTUVWX", "YZ_abcd", "efghijkm", "nopqrstu", "vwxy0123"};
    private static final String[] PDC8 = { "ABCDEFGH", "IJKLMNOP", "QRSTUVWX", "YZ_abcd", "efghijkm", "nopqrstu", "vwxy0123", "z456789"};

    // testing random is left as an exercise to the reader. Patches welcome

    @Test
    public void testPDC1()
        throws Exception
    {
         unpack(1).forkMode( getForkMode() ).runOrder( null ).maven().withFailure().executeTest().verifyTextInLog(
            "There's no RunOrder with the name null." );
    }

    @Test
    public void testPDC2()
        throws Exception
    {
        OutputValidator validator = executeWithRunOrder( "alphabetical", 2 );
        assertTestnamesAppearInSpecificOrder( validator, PDC2 );
    }

    @Test
    public void testPDC3()
        throws Exception
    {
        OutputValidator validator = executeWithRunOrder( "reversealphabetical", 3 );
        assertTestnamesAppearInSpecificOrder( validator, PDC3 );
    }

    @Test
    public void testPDC4()
        throws Exception
    {
        OutputValidator validator = executeWithRunOrder( "random", 4 );
        //The only check is that 4 tests run without failures;
    }

    @Test
    public void testPDC5()
        throws Exception
    {
        MockTime.setCurrentMilliesProperty( "1429340400000" ); //4/18/15 2:0:0
        OutputValidator validator = executeWithRunOrder( "hourly", 5);
        assertTestnamesAppearInSpecificOrder( validator, PDC5 );
        MockTime.setCurrentMilliesPropertyToSystem();
    }

    @Test
    public void testPDC6()
        throws Exception
    {
        OutputValidator validator = executeWithRunOrder( "failedfirst", 6 );
        //The only check is that 6 tests run without failures;
    }

    @Test
    public void testPDC7()
        throws Exception
    {
        OutputValidator validator = executeWithRunOrder( "balanced", 7 );
        //The only check is that 7 tests run without failures;
    }

    @Test
    public void testPDC8()
        throws Exception
    {
        OutputValidator validator = executeWithRunOrder( "filesystem", 8 );
        //The only check is that 8 tests run without failures;
    }

    private OutputValidator executeWithRunOrder( String runOrder, int PDC )
        throws IOException, VerificationException
    {
        return unpack(PDC).forkMode( getForkMode() ).runOrder( runOrder ).executeTest().verifyErrorFree( PDC );
    }

    protected String getForkMode()
    {
        return "once";
    }

    private SurefireLauncher unpack(int i)
    {
        return unpack( "runOrderPDC"+i );
    }

    private void assertTestnamesAppearInSpecificOrder( OutputValidator validator, String[] testnames )
        throws VerificationException
    {
        if ( !validator.stringsAppearInSpecificOrderInLog( testnames ) )
        {
            throw new VerificationException( "Response does not contain expected item" );
        }
    }
}
