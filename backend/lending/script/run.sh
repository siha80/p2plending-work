#!/bin/sh

project=p2plending-lending

HOME_DIR=$(dirname $(readlink -f $0))
LIB_DIR="$HOME_DIR/lib"
SCALA_HOME="/app/scala"
JAVA_HOME="/app/jdk"

MAIN_CLASS="com.skp.payment.p2plending.lending.launcher"

export PATH=$JAVA_HOME/bin:$SCALA_HOME/bin:$PATH

if [ $# -eq 0 ]; then
    echo "usage: run.sh [start|stop]"
    exit
fi

if [ $1 = 'stop' ] ; then
    echo "${project} stop.........................................."
    ps -ef | grep ${project} | grep -v grep && \
    kill `ps -ef | grep ${project} | grep -v grep | awk '{print $2}'`
    exit
fi

if [ $1 = 'start' ] ; then
    PID=`ps -ef | grep ${project} | grep -v grep | awk '{print $2}'`
    if [ $PID ] ; then
        echo "already running application : $PID"
        exit
    fi
fi

# Get classpath in the defined file, if exist
for i in "$LIB_DIR"/*.jar
do
    CLASSPATH="$CLASSPATH":"$i"
done

echo $SCALA_HOME/bin/scala -cp .$CLASSPATH $MAIN_CLASS GRPC
$SCALA_HOME/bin/scala -cp .$CLASSPATH $MAIN_CLASS GRPC &
