#!/bin/sh

SHDIR="${0%/*}"

if [ "$SHDIR" = "$0" ] ; then
  SHDIR=.
fi

JAVELIN_JAR=${SHDIR}/../lib/javelin.jar
ENS_COMMON=${SHDIR}/../lib/javelin.jar

if [ "$JAVA_HOME" = "" ] ; then
	JAVA_CMD=java
else
	JAVA_CMD="$JAVA_HOME"/bin/java
fi

"$JAVA_CMD" -cp "$JAVELIN_JAR":"$ENS_COMMON" jp.co.acroquest.endosnipe.javelin.SerializeViewer ${SHDIR}/../data/serialize.dat
