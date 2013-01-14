@echo off

setlocal

set JAVELIN_JAR="%~dp0..\lib\javelin.jar"

if "%JAVA_HOME%"=="" (
	rem JAVA_HOMEが設定されていなければ、デフォルトのjavaを使用する
	set JAVA_CMD=java
) else (
	rem JAVA_HOMEが設定されていれば、JAVA_HOME配下のjavaを使用する
	set JAVA_CMD="%JAVA_HOME%\bin\java"
)

%JAVA_CMD% -Xmx64m -Xms64m -javaagent:%JAVELIN_JAR% -cp %JAVELIN_JAR% jp.co.acroquest.endosnipe.javelin.agent.AgentMain

endlocal
