#!/bin/bash
# myapp daemon
# chkconfig: 345 20 80
# description: myapp daemon
# processname: myapp

SCRIPT_DIR=$(readlink -f $(dirname ${BASH_SOURCE[0]}))
DAEMON_PATH=$SCRIPT_DIR

DATA_PATH=$SCRIPT_DIR

DAEMON=./StartGameServerDebug.sh
DAEMONOPTS="-my opts"

NAME=l2gs
DESC="My l2gs daemon manage"
PIDFILE=$DAEMON_PATH/$NAME.pid
SCRIPTNAME=/etc/init.d/$NAME

case "$1" in
start)
	printf "%-50s" "Starting $NAME..."
	cd $DAEMON_PATH
	PID=`$DAEMON $DAEMONOPTS > /dev/null 2>&1`
;;
status)
        printf "%-50s" "Checking $NAME..."
        if [ -f $PIDFILE ]; then
            PID=`cat $PIDFILE`
            if [ -z "`ps axf | grep ${PID} | grep -v grep`" ]; then
                printf "%s\n" "Process dead but pidfile exists"
            else
                echo "Running"
            fi
        else
            printf "%s\n" "Service not running"
        fi
;;
stop)
        printf "%-50s" "Stopping $NAME"
            PID=`cat $PIDFILE`
            cd $DAEMON_PATH
        if [ -f $PIDFILE ]; then
            kill -HUP $PID
            printf "%s\n" "Ok"
            rm -f $PIDFILE
        else
            printf "%s\n" "pidfile not found"
        fi
;;

forcestop)
        printf "%-50s" "Stopping $NAME"
            PID=`cat $PIDFILE`
            cd $DAEMON_PATH
        if [ -f $PIDFILE ]; then
            kill -9 $PID
            printf "%s\n" "Ok"
            rm -f $PIDFILE
        else
            printf "%s\n" "pidfile not found"
        fi
;;

restart)
  	$0 stop
  	$0 start
;;

forcerestart)
  	$0 forcestop
  	$0 start
  	$0 watch
;;

update)
	echo "update main folder"
	git pull
;;

watch)
	tail -f $DAEMON_PATH/log/stdout.log
;;


*)
        echo "Usage: $0 {status|start|stop|restart|forcerestart|update|watch}"
        exit 1
esac