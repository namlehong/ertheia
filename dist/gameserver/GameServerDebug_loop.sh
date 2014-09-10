#!/bin/bash

while :;
do
	java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -server -Dfile.encoding=UTF-8 -Xmx4G -cp config:./blood_lib/*:./lib/* blood.Blood > log/stdout.log 2>&1

	[ $? -ne 2 ] && break
	sleep 30;
done

