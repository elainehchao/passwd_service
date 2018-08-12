package com.mybrain.challenge;

import java.io.*;
import org.json.*;

public class PasswdUtil {

    private String mPasswdFilePath;

    public PasswdUtil() {
        mPasswdFilePath = "/etc/passwd";
    }

    public PasswdUtil(String filePath) {
        mPasswdFilePath = filePath;
    }

    private BufferedReader fileToString() {
        File file = null;
        BufferedReader reader = null;
        try {
            file = new File(mPasswdFilePath);
            reader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException excep) {
            System.err.println("File not found");
        }
        return reader;
    }

    /**
    * Gets all the users in the passwd file
    *
    * @return a Json string representing the all
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
    public static String getUsers(BufferedReader reader) throws IOException {
        String entry;
        String[] splitEntry;
        JSONArray jsonArray = new JSONArray();
        while ((entry = reader.readLine()) != null) {
            splitEntry = entry.split(":");

            // 2nd entry is password entry, so skipt this
            JSONObject entryObject = new JSONObject()
              .put("name", splitEntry[0])
              .put("uid", splitEntry[2])
              .put("gid", splitEntry[3])
              .put("comment", splitEntry[4])
              .put("home", splitEntry[5])
              .put("shell", splitEntry[6]);
            jsonArray.put(entryObject);
        }
        return jsonArray.toString();
    }

    public static String getUsersForQuery(JSONObject queryParams) {
        
    }
}
