package com.mybrain.challenge;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.*;
import org.json.*;

/**
 * Unit test for simple App.
 */
public class PasswdUtilTest
    extends TestCase
{
    // private PasswdUtil mPasswdUtil;

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public PasswdUtilTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( PasswdUtilTest.class );
    }

    protected void setUp() {
        // mPasswdUtil = new PasswdUtil();
    }

    /**
     * Test to check passwdutil get users for a single user
     * will return the proper json string
     */
    public void testGetUsersSingleUserReturnsJsonString()
    {
        String actualString = "nobody:*:-2:-2:Unprivileged User:/var/empty:/usr/bin/false";
        Reader inputString = new StringReader(actualString);
        BufferedReader inputBuffer = new BufferedReader(inputString);

        JSONArray expectedArray = new JSONArray();
        JSONObject expectedObject = new JSONObject()
          .put("name", "nobody")
          .put("uid", "-2")
          .put("gid", "-2")
          .put("comment", "Unprivileged User")
          .put("home", "/var/empty")
          .put("shell", "/usr/bin/false");
        expectedArray.put(expectedObject);

        try {
            assertEquals(expectedArray.toString(), PasswdUtil.getUsers(inputBuffer));
        } catch (IOException exception) {
            fail("IOException thrown");
        }
    }

    /**
     * Test to check passwdutil get users for a single user
     * will return the proper json string
     */
    public void testGetUsersMultipleUsersReturnsJsonString()
    {
        String actualString = "nobody:*:-2:-2:Unprivileged User:/var/empty:/usr/bin/false\r\n"
                            + "root:*:0:0:System Administrator:/var/root:/bin/sh";
        Reader inputString = new StringReader(actualString);
        BufferedReader inputBuffer = new BufferedReader(inputString);

        JSONArray expectedArray = new JSONArray();
        JSONObject expectedObject1 = new JSONObject()
          .put("name", "nobody")
          .put("uid", "-2")
          .put("gid", "-2")
          .put("comment", "Unprivileged User")
          .put("home", "/var/empty")
          .put("shell", "/usr/bin/false");
        expectedArray.put(expectedObject1);

        JSONObject expectedObject2 = new JSONObject()
          .put("name", "root")
          .put("uid", "0")
          .put("gid", "0")
          .put("comment", "System Administrator")
          .put("home", "/var/root")
          .put("shell", "/bin/sh");
        expectedArray.put(expectedObject2);

        try {
            assertEquals(expectedArray.toString(), PasswdUtil.getUsers(inputBuffer));
        } catch (IOException exception) {
            fail("IOException thrown");
        }
    }
}
