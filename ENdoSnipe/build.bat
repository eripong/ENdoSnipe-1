@echo off

rem ---------------------------------------------------------------------------
rem この環境変数はビルドスクリプトによって自動的に build.properties と
rem 各プロジェクトの MANIFEST.INF へ反映されます
rem ---------------------------------------------------------------------------
set VER=5.0.3
set BUILD=002
rem ---------------------------------------------------------------------------

if exist "C:\Program Files (x86)" goto 64BIT

:32BIT
echo 32 bit environment
set JAVA_HOME_50=C:\Program Files\Java\jdk1.5.0_22
set JAVA_HOME_60=C:\Program Files\Java\jdk1.6.0_38
goto SETTING

:64BIT
echo 64 bit environment
set JAVA_HOME_50=C:\Program Files (x86)\Java\jdk1.5.0_22
set JAVA_HOME_60=C:\Program Files (x86)\Java\jdk1.6.0_38

:SETTING


set JAVA_HOME=%JAVA_HOME_50%
set WORK_DIR=%~dp0deploy
set TAGS=Version_%VER%-%BUILD%
set PATH=%JAVA_HOME%\bin;%PATH%

if exist "%WORK_DIR%" rmdir "%WORK_DIR%" /S /Q


echo ビルドを開始します。
echo ===============================
echo ●JAVAバージョン(5.0)
"%JAVA_HOME_50%\bin\java" -version
echo ●JAVAバージョン(6.0)
"%JAVA_HOME_60%\bin\java" -version
echo ●タグ
echo %TAGS%
echo ===============================

pause

echo Antを実行します。

call build_java.bat

set JAVA_HOME=%JAVA_HOME_60%
set PATH=%JAVA_HOME%\bin;%PATH%

cd ..\WebDashboard

call ant dist

copy target\WebDashboard.war ..\ENdoSnipe\release

echo ビルドが完了しました。

pause
