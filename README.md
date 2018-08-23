# Passwd Service

This project is a simple Passwd Service that receives and parses HTTP requests for data from the server's /etc/passwd and /etc/groups files.

## Getting Started

The following are build and running instructions to get started with Passwd Service.

### Prerequisites

Java Runtime Environment

### Installing

These installation and runtime instructions are for OSX

1. Install Maven

https://maven.apache.org/install.html

To run and build the client, use the following instructions.

1. Build the application

```
mvn package
```
2. Run the application
```
java -cp target/passwd_service-1.0-SNAPSHOT.jar com.mybrain.challenge.PasswdService <port> <passwdfile> <groupsfile>
```

The following log sample shows what a successfully started server will see. Without any parameters passed in, the default port is 8030, and the default passwd and groups files are the /etc system files.

```
Input port: 8030
Input passwd file path: src/test/java/com/mybrain/challenge/passwd_test1.txt
Input group file path: src/test/java/com/mybrain/challenge/groups_test1.txt
Going to create server socket for port = 8030
```
To run a client, you can enter the following into a web browser.

```
localhost:8030/users
```

The following requests are supported by the Passwd Service.

1. Get all users in the passwd file
```
localhost:8030/users
```

2. Query for users based on a filter for any of the following: name, uid, gid, comment, home, and/or shell.
```
localhost:8030/users/query?name=root&shell=/bin/sh
```
3. Get user from uid
```
localhost:8030/users/3
```
4. Get the group information for a user's uid
```
localhost:8030/users/3/groups
```
5. Get all the groups in the groups file
```
localhost:8030/groups
```
6. Query for groups based on a filter for any of the following: name, gid, member
```
localhost:8030/groups/query?name=group1&member=root&member=root2
```
7. Get a group for a gid
```
localhost:8030/groups/3
```

## Running the tests

Running the following command will run the unit tests for the service. There are unit tests for the PasswdUtil.java and GroupsUtil.java
```
mvn package
```

## Built With

* [Maven](https://maven.apache.org/) - Dependency Management

## Authors

* **Elaine Chao**

## Acknowledgments

* [Java2s](http://www.java2s.com/Code/Java/Network-Protocol/AverysimpleWebserverWhenitreceivesaHTTPrequestitsendstherequestbackasthereply.htm)
