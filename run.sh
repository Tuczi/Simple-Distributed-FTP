#!/bin/sh
#build
ant -f simple_distributed_ftp.xml build.fulltest

#run servers
cd out/jar
for i in {0..3}; do
    echo "Starting server $i"
    java -jar server$i/server.jar &
done

#run client
echo "Starting client"
sleep 1
java -jar client.jar GET testfile.txt downloaded.txt
echo "Done: client"

#cleanup
kill $(jobs -p)
