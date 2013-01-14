#!/bin/sh

JAVELIN_JAR=../lib/javelin.jar

if [ "$JAVA_HOME" = "" ] ; then
	JAVA_CMD=java
else
	JAVA_CMD="$JAVA_HOME"/bin/java
fi

"$JAVA_CMD" -Xmx64m -Xms64m "-javaagent:$JAVELIN_JAR" -cp "$JAVELIN_JAR" jp.co.acroquest.endosnipe.javelin.agent.AgentMain
