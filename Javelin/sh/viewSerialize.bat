@echo off

setlocal

set JAVELIN_JAR="%~dp0..\lib\javelin.jar"
set ENS_COMMON="%~dp0..\lib\endosnipe-common-4.0.0.jar"

if "%JAVA_HOME%"=="" (
	rem JAVA_HOMEが設定されていなければ、デフォルトのjavaを使用する
	set JAVA_CMD=java
) else (
	rem JAVA_HOMEが設定されていれば、JAVA_HOME配下のjavaを使用する
	set JAVA_CMD="%JAVA_HOME%\bin\java"
)

%JAVA_CMD% -cp %JAVELIN_JAR%;%ENS_COMMON% jp.co.acroquest.endosnipe.javelin.SerializeViewer "%~dp0..\data\serialize.dat"

endlocal
