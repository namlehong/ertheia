#!/bin/bash

while :;
do
	java -server -Dfile.encoding=UTF-8 -Xmx1G -cp config:./lib/*:lameguard.jar com.lameguard.LameGuard l2s.gameserver.GameServer > log/stdout.log 2>&1

	[ $? -ne 2 ] && break
	sleep 30;
done

