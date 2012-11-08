@echo off
call settings.bat

echo %DESC% をサービスに登録しています...
ENdoSnipeDataCollector.exe //IS// --Description "%DESC%" --DisplayName %DISPLAYNAME% --Startup manual --Jvm auto --Classpath %JAR% --StartClass %MAIN% --StartParams start --StartMode jvm --StartPath %BASEDIR% --StopClass %MAIN% --StopParams stop --StopMode jvm --JvmOptions %JVMOPTIONS% --JvmMs %JVMMS% --JvmMx %JVMMX% --LogPath %LOGDIR% --LogLevel warn --StdOutput auto --StdError auto
