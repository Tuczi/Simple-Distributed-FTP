# Simple-Distributed-FTP
Simple Distributed application to send file from/to group of servers.
This is one of Distributed Computing Technologies course passing project.
It was written in Java using RMI.


## Run simple test
Simple test description:
- 4 servers instances are running all time
- run 1 client with GET method (download file)
- run 1 client with PUT method (upload file)
- run 1 client with PUT method (upload existing file - replace longer with shorter file)

To run simple test just execute run.sh script.
It will build project using Apache-Ant build and then start servers and clients.

## Improvements (TODO)
- Replication limit/partition tolerance (files are stored in limited number of servers (for example x=sqrt(n) copies of file, where n is number of servers). All servers stores map<fileId, array<host>>)
- Failure tolerance (upload, download, server instace)
- Dynamic number of servers (add or remove server)

