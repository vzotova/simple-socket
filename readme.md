Test appliaction

Application consists of 3 parts:
project - contains parent pom;
server - server-side;
client - client-side.

For building application you need to use Maven. Run mvn package in 'project' directory.
Will be built server.jar and client.jar in target folders in server and client projects.
Also will be copied all dependencies into target/lib folder in both projects.

For starting server run command "java -jar server.jar [<port> [<max_sessions>]]" in server/target folder.
For starting server run command "java -jar client.jar [<port>]" in client/target folder.