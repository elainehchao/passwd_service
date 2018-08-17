package com.mybrain.challenge;

import java.io.*;
import org.json.*;
import java.util.Arrays;

public class GroupsUtil {

    public static final String NAME = "name";
    public static final String GID = "gid";
    public static final String MEMBERS = "members";

    private String mGroupsFilePath;
    private JSONArray mEntries;

    public GroupsUtil() {
        mGroupsFilePath = "/etc/group";
        loadEntries();
    }

    public GroupsUtil(String filePath) {
        mGroupsFilePath = filePath;
        loadEntries();
    }

    private BufferedReader fileToString() throws FileNotFoundException {
        File file = null;
        BufferedReader reader = null;
        file = new File(mGroupsFilePath);
        reader = new BufferedReader(new FileReader(file));
        return reader;
    }

    private void loadEntries() {
        BufferedReader reader = null;
        try {
            reader = fileToString();
            String entry;
            String[] splitEntry;
            mEntries = new JSONArray();
            try {
                while ((entry = reader.readLine()) != null) {
                    splitEntry = entry.split(":");

                    // 2nd entry is password entry, so skipt this
                    JSONObject entryObject = new JSONObject()
                      .put(NAME, splitEntry[0])
                      .put(GID, splitEntry[2]);

                    if (splitEntry.length == 4) {
                        entryObject.put(MEMBERS, splitEntry[3].split(","));
                    } else {
                        entryObject.put(MEMBERS, new String[0]);
                    }
                    mEntries.put(entryObject);
                }
            } catch (IOException excep) {
                System.err.println("IO Exception");
            }
        } catch (FileNotFoundException exception) {
            System.err.println("Group File not found");
        }
    }

    public String getGroups() {
        checkFileUpdated();
        if (mEntries != null) {
            return mEntries.toString();
        }
        return "";
    }

    public String getGroupsForQuery(JSONObject queryParams) {
        checkFileUpdated();
        if (mEntries != null) {
            int queryMask = 0;

            String queryGID = "";
            try {
                queryGID = queryParams.getString(GID);
                queryMask = setBit(queryMask, 0);
            } catch (JSONException excep) {
                System.out.println("No GID in query");
            }
            // uid is unique, so if it's set, return this
            if (isSet(queryMask, 0)) {
                return getGroupForGID(queryParams.getString(GID));
            }

            String queryName = "";
            try {
                queryName = queryParams.getString(NAME);
                queryMask = setBit(queryMask, 1);
            } catch (JSONException excep) {
                System.out.println("No Name in query");
            }

            String[] queryMember = null;
            try {
                queryMember = (String[]) queryParams.get(MEMBERS);
                queryMask = setBit(queryMask, 2);
                for (int i = 0; i < queryMember.length; i++) {
                    System.out.println("Query member: " + queryMember[i]);
                }
            } catch (JSONException excep) {
                System.out.println("No Members in query");
            }

            JSONArray results = new JSONArray();
            // System.out.println("Bit mask " + queryMask);
            for (int i = 0; i < mEntries.length(); i++) {
                JSONObject currentEntry = (JSONObject) mEntries.get(i);
                if ((!isSet(queryMask, 1) || currentEntry.get(NAME).equals(queryName))
                && (!isSet(queryMask, 0) || currentEntry.get(GID).equals(queryGID))
                && (!isSet(queryMask, 2) || Arrays.asList((String[]) currentEntry.get(MEMBERS)).containsAll(Arrays.asList(queryMember)))) {
                    results.put(currentEntry);
                }
            }

            return results.toString();
        }
        return "";
    }

    /**
    *
    * Return whether the bit in a bit mask is set. The bit mask
    * assumes the following index meanings
    *
    * 0 = GID
    * 1 = Name
    * 2 = Member
    *
    * @param mask the bit mask
    * @param position the position to check if it is set
    */
    private boolean isSet(int mask, int position) {
        return ((mask >> position) & 1) != 0;
    }

    /**
    *
    * Sets the bit in the query bit mask
    *
    * 0 = GID
    * 1 = Name
    * 2 = Member
    *
    * @param mask the bit mask
    * @param position the position to set
    */
    private int setBit(int mask, int position) {
        return mask | (1 << position);
    }

    public String getGroupForGID(String gid) {
        checkFileUpdated();
        if (mEntries != null) {
            JSONArray results = new JSONArray();
            for (int i = 0; i < mEntries.length(); i++) {
                if (((JSONObject) mEntries.get(i)).get(GID).equals(gid)) {
                    results.put(mEntries.get(i));
                    break;
                }
            }
            return results.toString();
        }
        return "";
    }

    public void checkFileUpdated() {
        File file = new File(mGroupsFilePath);
        System.out.println("File last read: " + mFileReadTimestamp);
        System.out.println("File last modified " + file.lastModified());
        if (mFileReadTimestamp < file.lastModified()) {
            loadEntries();
        }
    }
}
