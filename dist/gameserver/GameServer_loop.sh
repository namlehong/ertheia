#!/bin/bash

while :;
do
	java -server -Dfile.encoding=UTF-8 -Xmx4G -cp config:./blood_lib/*:./lib/* l2s.gameserver.GameServer > log/stdout.log 2>&1

	[ $? -ne 2 ] && break
	sleep 30;
done

