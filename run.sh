#!/bin/sh
#build
ant -f simple_distributed_ftp.xml build.fulltest

#run servers
cd out/jar
for i in {0..3}; do
    echo "Starting server $i"
    java -Djava.security.policy=java.security.AllPermission -jar server$i/server.jar &
done

#run client
sleep 1 #wait for servers
echo "Starting client GET"
java -Djava.security.policy=java.security.AllPermission -jar client.jar GET testfile.txt downloaded.txt
echo "Done: client GET"

echo "Starting client PUT"
echo "This is test text for upload." > upload.txt
java -Djava.security.policy=java.security.AllPermission -jar client.jar PUT upload.txt testfile2.txt
echo "Done: client PUT"

echo "Starting client PUT2"
echo "This is modified text test text for upload." > upload2.txt
java -Djava.security.policy=java.security.AllPermission -jar client.jar PUT upload2.txt testfile3.txt
echo "Done: client PUT2"

#cleanup
kill $(jobs -p)
