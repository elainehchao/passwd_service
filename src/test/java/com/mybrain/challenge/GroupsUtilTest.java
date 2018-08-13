package com.mybrain.challenge;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.*;
import org.json.*;

/**
 * Unit test for simple App.
 */
public class GroupsUtilTest
    extends TestCase
{
    private GroupsUtil mGroupsUtil;

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public GroupsUtilTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( GroupsUtilTest.class );
    }

    protected void setUp() {
        mGroupsUtil = new GroupsUtil("src/test/java/com/mybrain/challenge/groups_test1.txt");
    }

    /**
     * Test to check passwdutil get users for multiple users
     * will return the proper json string
     */
    public void testGetGroupsMultipleUsersReturnsJsonString()
    {
        JSONArray expectedArray = new JSONArray();
        JSONObject expectedObject1 = new JSONObject()
          .put(GroupsUtil.NAME, "certusers")
          .put(GroupsUtil.GID, "29")
          .put(GroupsUtil.MEMBERS, new String[]{"root", "_jabber", "_postfix", "_cyrus", "_calendar", "_dovecot"});
        expectedArray.put(expectedObject1);

        JSONObject expectedObject2 = new JSONObject()
          .put(GroupsUtil.NAME, "netusers")
          .put(GroupsUtil.GID, "52")
          .put(GroupsUtil.MEMBERS, new String[0]);
        expectedArray.put(expectedObject2);

        JSONObject expectedObject3 = new JSONObject()
          .put(GroupsUtil.NAME, "_www")
          .put(GroupsUtil.GID, "70")
          .put(GroupsUtil.MEMBERS, new String[]{"_devicemgr", "_teamsserver"});
        expectedArray.put(expectedObject3);

        JSONObject expectedObject4 = new JSONObject()
          .put(GroupsUtil.NAME, "operator")
          .put(GroupsUtil.GID, "-5")
          .put(GroupsUtil.MEMBERS, new String[] {"root"});
        expectedArray.put(expectedObject4);
        assertEquals(expectedArray.toString(), mGroupsUtil.getGroups());
    }

    public void testGetGroupWithPositiveGID() {
        JSONArray expectedArray = new JSONArray();
        JSONObject expectedObject3 = new JSONObject()
          .put(GroupsUtil.NAME, "_www")
          .put(GroupsUtil.GID, "70")
          .put(GroupsUtil.MEMBERS, new String[]{"_devicemgr", "_teamsserver"});
        expectedArray.put(expectedObject3);
        assertEquals(expectedArray.toString(), mGroupsUtil.getGroupForGID("70"));
    }

    public void testGetGroupWithNegativeGID() {
        JSONArray expectedArray = new JSONArray();
        JSONObject expectedObject4 = new JSONObject()
          .put(GroupsUtil.NAME, "operator")
          .put(GroupsUtil.GID, "-5")
          .put(GroupsUtil.MEMBERS, new String[] {"root"});
        expectedArray.put(expectedObject4);
        assertEquals(expectedArray.toString(), mGroupsUtil.getGroupForGID("-5"));
    }

    public void testQueryGroupWithName() {
        JSONArray expectedArray = new JSONArray();
        JSONObject expectedObject2 = new JSONObject()
          .put(GroupsUtil.NAME, "netusers")
          .put(GroupsUtil.GID, "52")
          .put(GroupsUtil.MEMBERS, new String[0]);
        expectedArray.put(expectedObject2);

        JSONObject query = new JSONObject()
          .put("name", "netusers");
        assertEquals(expectedArray.toString(), mGroupsUtil.getGroupsForQuery(query));
    }

    public void testQueryGroupWithGID() {
        JSONArray expectedArray = new JSONArray();
        JSONObject expectedObject2 = new JSONObject()
          .put(GroupsUtil.NAME, "netusers")
          .put(GroupsUtil.GID, "52")
          .put(GroupsUtil.MEMBERS, new String[0]);
        expectedArray.put(expectedObject2);

        JSONObject query = new JSONObject()
          .put("gid", "52");
        assertEquals(expectedArray.toString(), mGroupsUtil.getGroupsForQuery(query));
    }

    public void testQueryGroupWithMembers() {
        JSONArray expectedArray = new JSONArray();
        JSONObject expectedObject1 = new JSONObject()
          .put(GroupsUtil.NAME, "certusers")
          .put(GroupsUtil.GID, "29")
          .put(GroupsUtil.MEMBERS, new String[]{"root", "_jabber", "_postfix", "_cyrus", "_calendar", "_dovecot"});
        expectedArray.put(expectedObject1);

        JSONObject query = new JSONObject()
          .put(GroupsUtil.MEMBERS, new String[]{"root", "_jabber", "_postfix", "_cyrus", "_calendar", "_dovecot"});
        assertEquals(expectedArray.toString(), mGroupsUtil.getGroupsForQuery(query));
    }

    public void testQueryGroupWithShellMultipleSameMembers() {
        JSONArray expectedArray = new JSONArray();
        JSONObject expectedObject1 = new JSONObject()
          .put(GroupsUtil.NAME, "certusers")
          .put(GroupsUtil.GID, "29")
          .put(GroupsUtil.MEMBERS, new String[]{"root", "_jabber", "_postfix", "_cyrus", "_calendar", "_dovecot"});
        expectedArray.put(expectedObject1);

        JSONObject expectedObject4 = new JSONObject()
          .put(GroupsUtil.NAME, "operator")
          .put(GroupsUtil.GID, "-5")
          .put(GroupsUtil.MEMBERS, new String[] {"root"});
        expectedArray.put(expectedObject4);

        JSONObject query = new JSONObject()
          .put(GroupsUtil.MEMBERS, new String[]{"root"});
        assertEquals(expectedArray.toString(), mGroupsUtil.getGroupsForQuery(query));
    }
}
