#!/bin/sh

PROJECT=p2plending-evaluation-$2
DIR=/app/deploy/p2plending_evaluation_server_$2

if [ $# -eq 0 ]; then
    echo "usage: run.sh [start|stop]"
    exit 1
fi

if [ $1 = 'stop' ] ; then
    echo "${PROJECT} stop.........................................."
    ps -ef | grep ${PROJECT} | grep -v grep && \
    kill `ps -ef | grep ${PROJECT} | grep -v grep | awk '{print $2}'`
    exit 0
fi

if [ $1 = 'start' ] ; then
    PID=`ps -ef | grep ${PROJECT} | grep -v grep | awk '{print $2}'`
    if [ $PID ] ; then
        echo "${PROJECT} already running : $PID"
        kill `ps -ef | grep ${PROJECT} | grep -v grep | awk '{print $2}'`
        REPID=`ps -ef | grep ${PROJECT} | grep -v grep | awk '{print $2}'`
        if [ $REPID ] ; then
            echo "not kill ${PROJECT} daemon : $REPID"
            exit 1
        fi
    fi
fi

cd ${DIR}

java -jar -D${PROJECT} -Djsse.enableSNIExtension=false -Dcom.skp.p2plending.$4.http.port.evaluation=$3 -Dakka.profile=$4 ${DIR}/p2plending-evaluation.jar $4 > /dev/null 2>&1 &
