package com.mybrain.challenge;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import org.json.JSONObject;
import java.util.List;
import java.util.ArrayList;

public class PasswdService {

    private static PasswdUtil mPasswdUtil;
    private static GroupsUtil mGroupsUtil;

    public static void main (String[] args) {
        System.out.println("Hello World");
        try {

            // default
            int port = 8030;
            mPasswdUtil = new PasswdUtil("src/test/java/com/mybrain/challenge/passwd_test1.txt");
            mGroupsUtil = new GroupsUtil("src/test/java/com/mybrain/challenge/groups_test1.txt");
            // Get the port to listen on
            if (args.length > 0) {
                port = Integer.parseInt(args[0]);
            }

            System.out.println("Going to create server socket for port = " + port);
            ServerSocket ss = new ServerSocket(port);

            while (true) {
                Socket client = ss.accept();

                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                PrintWriter out = new PrintWriter(client.getOutputStream());

                out.print("HTTP/1.1 200 \r\n"); // Version & status code
                out.print("Content-Type: text/plain\r\n"); // The type of data
                out.print("Connection: close\r\n"); // Will close stream
                out.print("\r\n"); // End of headers

                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    sb.append(line);
                    if (line.length() == 0) {
                        break;
                    }
                }

                int requestStartIndex = sb.indexOf("GET") + "GET".length() + 2;
                int requestEndIndex = sb.indexOf(" ", requestStartIndex);
                // System.out.println("Request full string: " + sb.toString() + " " + requestStartIndex + " " + requestEndIndex);
                String request = sb.substring(requestStartIndex, requestEndIndex);
                // System.out.println("Process groups request " + request);
                int index = request.indexOf("/");
                if (index > 0) {
                    if (request.substring(0, index).equals("users")) {
                        out.print(processUserRequest(request));
                    } else if (request.substring(0, index).equals("groups")) {
                        out.print(processGroupsRequest(request));
                    }
                }
                out.close(); // Flush and close the output stream
                in.close(); // Close the input stream
                client.close(); // Close the socket
            }
        } catch (Exception excep) {
            System.err.println(excep);
            excep.printStackTrace();
        }

        // handle Get /users/<uid>/groups

        // handle Get /groups

        // handle Get /groups queury

        // handle Get /groups/<gid>
    }

    /**
    * Processes the client's user request
    *
    * Possible request formats:
    *
    * users
    * users/<uid>
    * users/query?<query>
    */
    private static String processUserRequest(String request) {
        String response = "";
        int index = request.indexOf("/");
        if (index < 0) {
            // there are no additional parts to the request, get all users
            response = mPasswdUtil.getUsers();
        } else {
            String userRequest = request.substring(index + 1);
            // System.out.println("Prcess user request " + userRequest);
            if (userRequest.indexOf("query") < 0) {
                // not query, request for UID
                response = mPasswdUtil.getUserForUID(userRequest);
            } else {
                int queryEndIndex = userRequest.indexOf("query?") + "query?".length();
                String query = userRequest.substring(queryEndIndex);
                String[] queryParams = query.split("&");
                JSONObject paramObject = new JSONObject();
                for (int i = 0; i < queryParams.length; i++) {
                    String[] individualParam = queryParams[i].split("=");
                    String key = individualParam[0];
                    String value = individualParam[1];
                    paramObject.put(key, value);
                }
                response = mPasswdUtil.getUsersForQuery(paramObject);
            }
        }
        return response;
    }

    /**
    * Processes the client's groups request
    *
    * Possible request formats:
    *
    * groups
    * groups/<gid>
    * groups/query?<query>
    */
    private static String processGroupsRequest(String request) {
        String response = "";
        int index = request.indexOf("/");
        if (index < 0) {
            // there are no additional parts to the request, get all users
            response = mGroupsUtil.getGroups();
        } else {
            String groupRequest = request.substring(index + 1);
            System.out.println("Prcess group request " + groupRequest);
            if (groupRequest.indexOf("query") < 0) {
                // not query, request for GID
                response = mGroupsUtil.getGroupForGID(groupRequest);
            } else {
                int queryEndIndex = groupRequest.indexOf("query?") + "query?".length();
                String query = groupRequest.substring(queryEndIndex);
                String[] queryParams = query.split("&");
                JSONObject paramObject = new JSONObject();
                List<String> members = new ArrayList<String>();
                for (int i = 0; i < queryParams.length; i++) {
                    String[] individualParam = queryParams[i].split("=");
                    String key = individualParam[0];
                    String value = individualParam[1];
                    if (!key.equals("member")) {
                        paramObject.put(key, value);
                    } else {
                        members.add(value);
                    }
                }
                paramObject.put(GroupsUtil.MEMBERS, members.toArray(new String[members.size()]));
                response = mGroupsUtil.getGroupsForQuery(paramObject);
            }
        }
        return response;
    }
}
