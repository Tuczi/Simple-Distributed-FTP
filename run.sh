#!/bin/sh
ant -f simple_distributed_ftp.xml build.fulltest
cd out/jar
for i in {0..3}; do
	echo "Starting server $i"
	java -jar Server$i.jar &
done
java -jar Client.jar
