#!/bin/bash

if [ $# -ne 2 ];
then 
    echo "Usage: <absolute path to executable> <path to pid file>"
    exit 1
fi

RUNNING=false
EXEC=$1
PID_FILE=$2
# Just the end of the executable
SHORT=$(echo $1 | sed -r 's/.*\/([^\/]*)$/\1/g')

# PID_FILE exists
if [ -a $PID_FILE ]; 
then 
    # pid in PID_FILE is actually running
    if [ $(cat $PID_FILE | xargs -I '{}' ps --pid '{}' | wc -l) -gt 1 ];
    then    
	RUNNING=true
    fi
fi
# if not running then start
if [ "$RUNNING" == "false" ];
then
    $EXEC &
    sleep 2
    pgrep $SHORT > run.pid
fi