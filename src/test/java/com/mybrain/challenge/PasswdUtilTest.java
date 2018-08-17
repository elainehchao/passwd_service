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
    private PasswdUtil mPasswdUtil;

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
        mPasswdUtil = new PasswdUtil("src/test/java/com/mybrain/challenge/passwd_test1.txt");
    }

    /**
     * Test to check passwdutil get users for multiple users
     * will return the proper json string
     */
    public void testGetUsersMultipleUsersReturnsJsonString()
    {
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

        JSONObject expectedObject3 = new JSONObject()
          .put("name", "user")
          .put("uid", "4")
          .put("gid", "0")
          .put("comment", "System User")
          .put("home", "/var/root")
          .put("shell", "/bin/sh");
        expectedArray.put(expectedObject3);

        JSONObject expectedObject4 = new JSONObject()
          .put("name", "user2")
          .put("uid", "5")
          .put("gid", "29")
          .put("comment", "System User2")
          .put("home", "/var/root")
          .put("shell", "/bin/sh");
        expectedArray.put(expectedObject4);
        assertEquals(expectedArray.toString(), mPasswdUtil.getUsers());
    }

    public void testGetUserWithPositiveUID() {
        JSONArray expectedArray = new JSONArray();
        JSONObject expectedObject3 = new JSONObject()
          .put("name", "user")
          .put("uid", "4")
          .put("gid", "0")
          .put("comment", "System User")
          .put("home", "/var/root")
          .put("shell", "/bin/sh");
        expectedArray.put(expectedObject3);
        assertEquals(expectedArray.toString(), mPasswdUtil.getUserForUID("4"));
    }

    public void testGetUserWithNegativeUID() {
        JSONArray expectedArray = new JSONArray();
        JSONObject expectedObject1 = new JSONObject()
          .put("name", "nobody")
          .put("uid", "-2")
          .put("gid", "-2")
          .put("comment", "Unprivileged User")
          .put("home", "/var/empty")
          .put("shell", "/usr/bin/false");
        expectedArray.put(expectedObject1);
        assertEquals(expectedArray.toString(), mPasswdUtil.getUserForUID("-2"));
    }

    public void testQueryUserWithName() {
        JSONArray expectedArray = new JSONArray();
        JSONObject expectedObject1 = new JSONObject()
          .put("name", "nobody")
          .put("uid", "-2")
          .put("gid", "-2")
          .put("comment", "Unprivileged User")
          .put("home", "/var/empty")
          .put("shell", "/usr/bin/false");
        expectedArray.put(expectedObject1);

        JSONObject query = new JSONObject()
          .put("name", "nobody");
        assertEquals(expectedArray.toString(), mPasswdUtil.getUsersForQuery(query));
    }

    public void testQueryUserWithGID() {
        JSONArray expectedArray = new JSONArray();
        JSONObject expectedObject1 = new JSONObject()
          .put("name", "nobody")
          .put("uid", "-2")
          .put("gid", "-2")
          .put("comment", "Unprivileged User")
          .put("home", "/var/empty")
          .put("shell", "/usr/bin/false");
        expectedArray.put(expectedObject1);

        JSONObject query = new JSONObject()
          .put("gid", "-2");
        assertEquals(expectedArray.toString(), mPasswdUtil.getUsersForQuery(query));
    }

    public void testQueryUserWithComment() {
        JSONArray expectedArray = new JSONArray();
        JSONObject expectedObject1 = new JSONObject()
          .put("name", "nobody")
          .put("uid", "-2")
          .put("gid", "-2")
          .put("comment", "Unprivileged User")
          .put("home", "/var/empty")
          .put("shell", "/usr/bin/false");
        expectedArray.put(expectedObject1);

        JSONObject query = new JSONObject()
          .put("comment", "Unprivileged User");
        assertEquals(expectedArray.toString(), mPasswdUtil.getUsersForQuery(query));
    }

    public void testQueryUserWithHome() {
        JSONArray expectedArray = new JSONArray();
        JSONObject expectedObject1 = new JSONObject()
          .put("name", "nobody")
          .put("uid", "-2")
          .put("gid", "-2")
          .put("comment", "Unprivileged User")
          .put("home", "/var/empty")
          .put("shell", "/usr/bin/false");
        expectedArray.put(expectedObject1);

        JSONObject query = new JSONObject()
          .put("home", "/var/empty");
        assertEquals(expectedArray.toString(), mPasswdUtil.getUsersForQuery(query));
    }

    public void testQueryUserWithShell() {
        JSONArray expectedArray = new JSONArray();
        JSONObject expectedObject1 = new JSONObject()
          .put("name", "nobody")
          .put("uid", "-2")
          .put("gid", "-2")
          .put("comment", "Unprivileged User")
          .put("home", "/var/empty")
          .put("shell", "/usr/bin/false");
        expectedArray.put(expectedObject1);

        JSONObject query = new JSONObject()
          .put("shell", "/usr/bin/false");
        assertEquals(expectedArray.toString(), mPasswdUtil.getUsersForQuery(query));
    }

    public void testQueryUserWithShellMultiple() {
        JSONArray expectedArray = new JSONArray();
        JSONObject expectedObject2 = new JSONObject()
          .put("name", "root")
          .put("uid", "0")
          .put("gid", "0")
          .put("comment", "System Administrator")
          .put("home", "/var/root")
          .put("shell", "/bin/sh");
        expectedArray.put(expectedObject2);

        JSONObject expectedObject3 = new JSONObject()
          .put("name", "user")
          .put("uid", "4")
          .put("gid", "0")
          .put("comment", "System User")
          .put("home", "/var/root")
          .put("shell", "/bin/sh");
        expectedArray.put(expectedObject3);

        JSONObject expectedObject4 = new JSONObject()
          .put("name", "user2")
          .put("uid", "5")
          .put("gid", "29")
          .put("comment", "System User2")
          .put("home", "/var/root")
          .put("shell", "/bin/sh");
        expectedArray.put(expectedObject4);

        JSONObject query = new JSONObject()
          .put("shell", "/bin/sh");
        assertEquals(expectedArray.toString(), mPasswdUtil.getUsersForQuery(query));
    }

    public void testQueryUserWithHomeMultiple() {
        JSONArray expectedArray = new JSONArray();
        JSONObject expectedObject2 = new JSONObject()
          .put("name", "root")
          .put("uid", "0")
          .put("gid", "0")
          .put("comment", "System Administrator")
          .put("home", "/var/root")
          .put("shell", "/bin/sh");
        expectedArray.put(expectedObject2);

        JSONObject expectedObject3 = new JSONObject()
          .put("name", "user")
          .put("uid", "4")
          .put("gid", "0")
          .put("comment", "System User")
          .put("home", "/var/root")
          .put("shell", "/bin/sh");
        expectedArray.put(expectedObject3);

        JSONObject expectedObject4 = new JSONObject()
          .put("name", "user2")
          .put("uid", "5")
          .put("gid", "29")
          .put("comment", "System User2")
          .put("home", "/var/root")
          .put("shell", "/bin/sh");
        expectedArray.put(expectedObject4);

        JSONObject query = new JSONObject()
          .put("home", "/var/root");
        assertEquals(expectedArray.toString(), mPasswdUtil.getUsersForQuery(query));
    }

    public void testGetGIDForUser() {
        assertEquals("-2", mPasswdUtil.getGIDForUID("-2"));
    }
}
