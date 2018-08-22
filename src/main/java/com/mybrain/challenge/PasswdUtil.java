package com.mybrain.challenge;

import java.io.*;
import org.json.*;

public class PasswdUtil {

    public static final String UID = "uid";
    public static final String NAME = "name";
    public static final String GID = "gid";
    public static final String COMMENT = "comment";
    public static final String HOME = "home";
    public static final String SHELL = "shell";

    private String mPasswdFilePath;
    private JSONArray mEntries;

    long mFileReadTimestamp = 0L;

    public PasswdUtil() {
        mPasswdFilePath = "/etc/passwd";
        loadEntries();
    }

    public PasswdUtil(String filePath) {
        mPasswdFilePath = filePath;
        loadEntries();
    }

    private BufferedReader fileToString() throws FileNotFoundException {
        File file = null;
        BufferedReader reader = null;
        file = new File(mPasswdFilePath);
        mFileReadTimestamp = file.lastModified();
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
                    if (entry.indexOf("#") < 0) {
                        splitEntry = entry.split(":");

                        // 2nd entry is password entry, so skipt this
                        JSONObject entryObject = new JSONObject()
                          .put(NAME, splitEntry[0])
                          .put(UID, splitEntry[2])
                          .put(GID, splitEntry[3])
                          .put(COMMENT, splitEntry[4])
                          .put(HOME, splitEntry[5])
                          .put(SHELL, splitEntry[6]);
                        mEntries.put(entryObject);
                    }
                }
            } catch (IOException excep) {
                System.err.println("IO Exception");
            }
        } catch (FileNotFoundException exception) {
            System.err.println("Passwd File not found");
        }
    }

    /**
    * Gets all the users in the passwd file
    *
    * @return a Json string representing all
    * the users in the passwd file
    *
    * For example:
    *
    * [{
	*    "name": "root",
	*    "uid": 0,
	*    "gid": 23,
	*    "comment": "i am root",
	*    "home": "/root",
	*    "shell": "/bin/bash"
    * }]
    */
    public String getUsers() {
        checkFileUpdated();
        if (mEntries != null) {
            return mEntries.toString();
        }
        return "";
    }

    public String getUsersForQuery(JSONObject queryParams) {
        checkFileUpdated();
        if (mEntries != null) {
            int queryMask = 0;
            String queryUID = "";
            try {
                queryUID = queryParams.getString(UID);
                queryMask = setBit(queryMask, 0);
            } catch (JSONException excep) {
                System.out.println("No UID in query");
            }
            // uid is unique, so if it's set, return this
            if (isSet(queryMask, 0)) {
                return getUserForUID(queryParams.getString(UID));
            }

            String queryName = "";
            try {
                queryName = queryParams.getString(NAME);
                queryMask = setBit(queryMask, 1);
            } catch (JSONException excep) {
                System.out.println("No Name in query");
            }

            String queryGID = "";
            try {
                queryGID = queryParams.getString(GID);
                queryMask = setBit(queryMask, 2);
            } catch (JSONException excep) {
                System.out.println("No GID in query");
            }

            String queryComment = "";
            try {
                queryComment = queryParams.getString(COMMENT);
                queryMask = setBit(queryMask, 3);
            } catch (JSONException excep) {
                System.out.println("No Comment in query");
            }

            String queryHome = "";
            try {
                queryHome = queryParams.getString(HOME);
                queryMask = setBit(queryMask, 4);
            } catch (JSONException excep) {
                System.out.println("No Home in query");
            }

            String queryShell = "";
            try {
                queryShell = queryParams.getString(SHELL);
                queryMask = setBit(queryMask, 5);
            } catch (JSONException excep) {
                System.out.println("No Shell in query");
            }

            JSONArray results = new JSONArray();
            System.out.println("Bit mask " + queryMask);
            for (int i = 0; i < mEntries.length(); i++) {
                JSONObject currentEntry = (JSONObject) mEntries.get(i);
                if ((!isSet(queryMask, 1) || currentEntry.get(NAME).equals(queryName))
                && (!isSet(queryMask, 0) || currentEntry.get(UID).equals(queryUID))
                && (!isSet(queryMask, 2) || currentEntry.get(GID).equals(queryGID))
                && (!isSet(queryMask, 3) || currentEntry.get(COMMENT).equals(queryComment))
                && (!isSet(queryMask, 4) || currentEntry.get(HOME).equals(queryHome))
                && (!isSet(queryMask, 5) || currentEntry.get(SHELL).equals(queryShell))) {
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
    * 0 = UID
    * 1 = Name
    * 2 = GID
    * 3 = Comment
    * 4 = Home
    * 5 = Shell
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
    * 0 = UID
    * 1 = Name
    * 2 = GID
    * 3 = Comment
    * 4 = Home
    * 5 = Shell
    *
    * @param mask the bit mask
    * @param position the position to set
    */
    private int setBit(int mask, int position) {
        return mask | (1 << position);
    }

    public String getUserForUID(String uid) {
        checkFileUpdated();
        if (mEntries != null) {
            JSONArray results = new JSONArray();
            for (int i = 0; i < mEntries.length(); i++) {
                if (((JSONObject) mEntries.get(i)).get(UID).equals(uid)) {
                    results.put(mEntries.get(i));
                    break;
                }
            }
            if (results.length() > 0) {
              return results.toString();
            } else {
              return "";
            }
        }
        return "";
    }

    public String getGIDForUID(String uid) {
        checkFileUpdated();
        if (mEntries != null) {
            JSONArray results = new JSONArray();
            for (int i = 0; i < mEntries.length(); i++) {
                JSONObject currentObject = (JSONObject) mEntries.get(i);
                if (currentObject.getString(UID).equals(uid)) {
                    return currentObject.getString(GID);
                }
            }
        }
        return "";
    }

    public void checkFileUpdated() {
        File file = new File(mPasswdFilePath);
        System.out.println("File last read: " + mFileReadTimestamp);
        System.out.println("File last modified " + file.lastModified());
        if (mFileReadTimestamp < file.lastModified()) {
            loadEntries();
        }
    }
}
