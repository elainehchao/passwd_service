package com.mybrain.challenge;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import org.json.JSONObject;
import java.util.List;
import java.util.ArrayList;

/**
*
* Main Passwd Service that accepts and processes
* requests. This service returns a repsonse when there
* is one available and returns a 404 error if the request
* is malformed or if there is no response to a uid or gid
* request.
*
*/
public class PasswdService {

    private static PasswdUtil mPasswdUtil;
    private static GroupsUtil mGroupsUtil;

    /**
    * Main function. This function opens
    * a socket and hendles incoming requests
    */
    public static void main (String[] args) {
        try {

            // default
            int port = 8030;

            // defaults
            String passwdFilePath = "/etc/passwd";
            String groupFilePath = "/etc/group";


            if (args.length > 0) {
                port = Integer.parseInt(args[0]);
                System.out.println("Input port: " + port);
                if (args.length > 1) {
                    passwdFilePath = args[1];
                    System.out.println("Input passwd file path: " + passwdFilePath);
                    if (args.length > 2) {
                        groupFilePath = args[2];
                        System.out.println("Input group file path: " + groupFilePath);
                    } else {
                        System.out.println("No group file path specified, using default " + groupFilePath);
                    }
                } else {
                    System.out.println("No passwd file path speficied, using default " + passwdFilePath);
                }
            } else {
                System.out.println("No port specified, using default " + port);
            }

            mPasswdUtil = new PasswdUtil(passwdFilePath);
            mGroupsUtil = new GroupsUtil(groupFilePath);

            System.out.println("Going to create server socket for port = " + port);
            ServerSocket ss = new ServerSocket(port);

            while (true) {
                Socket client = ss.accept();

                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                PrintWriter out = new PrintWriter(client.getOutputStream());

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
                System.out.println("Request full string: " + sb.toString() + " " + requestStartIndex + " " + requestEndIndex);
                String responsePayload = "";
                if (requestStartIndex >= 0 && requestEndIndex >= 0) {
                    String request = sb.substring(requestStartIndex, requestEndIndex);
                    System.out.println("Process groups request " + request);
                    ERequestType requestType = getRequestType(request);
                    if (requestType == ERequestType.USER) {
                        responsePayload = processUserRequest(request);
                    } else if (requestType == ERequestType.GROUPS) {
                        responsePayload = processGroupsRequest(request);
                    }
                }

                System.out.println("RESPONSE PAYLOAD: " + responsePayload);
                if (!responsePayload.equals("")) {
                    get200OKHeader(out);
                    out.print(responsePayload);
                } else {
                    get404ErrorHeader(out);
                }

                out.close();
                in.close();
                client.close();
            }
        } catch (Exception excep) {
            System.err.println(excep);
            excep.printStackTrace();
        }

    }

    /**
    *
    * Get the request type
    *
    * @return the request type
    *
    */
    private static ERequestType getRequestType(String fullRequest) {
        if (fullRequest.indexOf("users") >= 0) {
            return ERequestType.USER;
        } else if (fullRequest.indexOf("groups") >= 0) {
            return ERequestType.GROUPS;
        }
        return ERequestType.INVALID;
    }

    /**
    *
    * Populate the repsonse with the 200OK Header
    *
    */
    private static void get200OKHeader(PrintWriter out) {
      System.out.println("200OK");
      out.print("HTTP/1.1 200 \r\n");
      out.print("Content-Type: text/plain\r\n");
      out.print("Connection: close\r\n");
      out.print("\r\n");
    }

    /**
    *
    * Populate the response with the 404 Error Header
    *
    */
    private static void get404ErrorHeader(PrintWriter out) {
      System.out.println("404");
      out.print("HTTP/1.1 404 \r\n");
      out.print("Content-Type: text/plain\r\n");
      out.print("Connection: close\r\n");
      out.print("\r\n");
    }

    /**
    * Processes the client's user request
    *
    * Possible request formats:
    *
    * users
    * users/<uid>
    * users/query?<query>
    * users/<uid>/groups
    */
    private static String processUserRequest(String request) {
        String response = "";
        int index = request.indexOf("/");
        if (index < 0) {
            // there are no additional parts to the request, get all users
            System.out.println("Passwdutil get users");
            response = mPasswdUtil.getUsers();
        } else {
            String userRequest = request.substring(index + 1);
            System.out.println("Prcess user request " + userRequest);
            if (userRequest.indexOf("query") < 0) {

                if (userRequest.indexOf("groups") < 0) {
                    // not query, request for UID
                    response = mPasswdUtil.getUserForUID(userRequest);
                } else {
                    int index2 = userRequest.indexOf("/");
                    if (index2 >= 0) {
                        String uid = userRequest.substring(0, index);
                        System.out.println("Parse uid: " + uid + " " + mPasswdUtil.getGIDForUID(uid));
                        response = mGroupsUtil.getGroupForGID(mPasswdUtil.getGIDForUID(uid));
                    }
                }
            } else {
                int index2 = userRequest.indexOf("query?");
                if (index2 >= 0) {
                    int queryEndIndex = index + "query?".length();
                    String query = userRequest.substring(queryEndIndex);
                    String[] queryParams = query.split("&");
                    JSONObject paramObject = new JSONObject();
                    for (int i = 0; i < queryParams.length; i++) {
                        String[] individualParam = queryParams[i].split("=");
                        System.out.println("Individual param " + individualParam.length);
                        if (individualParam.length == 2) {
                            String key = individualParam[0];
                            String value = individualParam[1];
                            paramObject.put(key, value);
                        }
                    }
                    response = mPasswdUtil.getUsersForQuery(paramObject);
                }
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
            System.out.println("Process group request " + groupRequest);
            if (groupRequest.indexOf("query") < 0) {
                // not query, request for GID
                response = mGroupsUtil.getGroupForGID(groupRequest);
            } else {
                int index2 = groupRequest.indexOf("query?");
                if (index2 >= 0){
                    int queryEndIndex = index + "query?".length();
                    String query = groupRequest.substring(queryEndIndex);
                    String[] queryParams = query.split("&");
                    JSONObject paramObject = new JSONObject();
                    List<String> members = new ArrayList<String>();
                    for (int i = 0; i < queryParams.length; i++) {
                        String[] individualParam = queryParams[i].split("=");
                        if (individualParam.length == 2) {
                            String key = individualParam[0];
                            String value = individualParam[1];
                            if (!key.equals("member")) {
                                paramObject.put(key, value);
                            } else {
                                members.add(value);
                            }
                        }
                    }
                    paramObject.put(GroupsUtil.MEMBERS, members.toArray(new String[members.size()]));
                    response = mGroupsUtil.getGroupsForQuery(paramObject);
                }
            }
        }
        return response;
    }
}
