@echo off

rem DEBUGモードでコンパイルするなら、以下のように変更すること
rem call setenv /x64 /debug
call setenv /x64 /release

set CFLAGS=/O2 /LD /DNDEBUG
set INCLUDES=/I"%ProgramFiles%\Java\jdk1.6.0_20\include"
set INCLUDES=%INCLUDES% /I"%ProgramFiles%\Java\jdk1.6.0_20\include\win32" 
set LDFLAGS=/link "%ProgramFiles%\Microsoft SDKs\Windows\v7.1\Lib\x64\Pdh.lib"

cl %CFLAGS% %INCLUDES% PerfCounter.cpp %LDFLAGS%

ren PerfCounter.dll PerfCounter_64.dll

