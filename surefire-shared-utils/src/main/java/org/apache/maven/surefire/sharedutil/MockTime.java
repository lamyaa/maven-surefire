package org.apache.maven.surefire.sharedutil;

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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Properties;

import org.joda.time.DateTimeUtils;

/**
 * Handles Time Mocking with JodaTime
 *
 */
public class MockTime
{
    public static void setCurrentTime() 
    {
        try 
        {
            Properties prop = new Properties();
            InputStream fileInput = MockTime.class.getResourceAsStream( "/time.properties" );
            
            File altf = new File( System.getProperty( "java.io.tmpdir" ) + "/time.properties" );
            if ( altf.exists() && !altf.isDirectory() ) 
            {
                fileInput = new FileInputStream( altf );
            }
            
            prop.load( fileInput );
            fileInput.close();
            String value = prop.getProperty( "time" );
            DateTimeUtils.setCurrentMillisFixed( Long.parseLong( value ) );
        } 
        catch ( Exception e ) 
        {
            DateTimeUtils.setCurrentMillisSystem();
        }
    }

    public static void setCurrentMilliesProperty( String millies )
    {
       try 
        {
            Properties prop = new Properties();
            prop.setProperty( "time", millies );
            File altf = new File( System.getProperty( "java.io.tmpdir" ) + "/time.properties" );
            FileOutputStream fileOutput = new FileOutputStream( altf ); //alternate file
            prop.store( fileOutput, "Current millies set to user defined value" );
            fileOutput.close();
        } 
        catch ( Exception e ) 
        {
            e.printStackTrace();
        }

    }

    public static void setCurrentMilliesPropertyToSystem()
    {
       try 
        {
            Properties prop = new Properties();
            prop.setProperty( "time", "system" );

            File altf = new File( System.getProperty( "java.io.tmpdir" )  + "/time.properties" );
            FileOutputStream fileOutput = new FileOutputStream( altf ); //alternate file
            prop.store( fileOutput, "Current millies set to system" );
            fileOutput.close();
        } 
        catch ( Exception e ) 
        {
            e.printStackTrace();
        }

    }
}
