package com.mybrain.challenge;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class PasswdService {

    private static PasswdUtil mPasswdUtil;

    public static void main (String[] args) {
        System.out.println("Hello World");
        try {

            // default
            int port = 8030;
            mPasswdUtil = new PasswdUtil("src/test/java/com/mybrain/challenge/passwd_test1.txt");

            // Get the port to listen on
            if (args.length > 0) {
                port = Integer.parseInt(args[0]);
            }

            System.out.println("Going to create server socket for port = " + port);
            ServerSocket ss = new ServerSocket(port);

            while (true) {
                Socket client = ss.accept();
                // Get input and output streams to talk to the client
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                PrintWriter out = new PrintWriter(client.getOutputStream());

                // Start sending our reply, using the HTTP 1.1 protocol
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

                int queryStartIndex = sb.indexOf("GET") + "GET".length() + 1;
                int queryEndIndex = sb.indexOf(" ", queryStartIndex);
                String query = sb.substring(queryStartIndex, queryEndIndex);
                String[] queryStrings = query.split("/");
                out.print(processQuery(queryStrings));

                out.close(); // Flush and close the output stream
                in.close(); // Close the input stream
                client.close(); // Close the socket
            }
        } catch (Exception excep) {
            System.err.println(excep);
            excep.printStackTrace();
        }

        // Loop and wait for client connections and requests

        // handle Get /users

        // handle Get queury

        // handle Get /users/<uid>

        // handle Get /users/<uid>/groups

        // handle Get /groups

        // handle Get /groups queury

        // handle Get /groups/<gid>
    }

    private static String processQuery(String[] args) {
        String response = "";
        if (args[1].equals("users")) {
            // request for all users
            response = mPasswdUtil.getUsers();
        } else if (args[1].equals("groups")) {

        }
        return response;
    }
}
