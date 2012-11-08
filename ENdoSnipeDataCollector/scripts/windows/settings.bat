@echo off
set BASEDIR=%~dp0
set SERVICENAME="ENdoSnipeDataCollector"
set DISPLAYNAME="ENdoSnipe Data Collector"
set DESC=ENdoSnipe DataCollector サービス
set JAR=%BASEDIR%..\lib\endosnipe-datacollector.jar
set MAIN=jp.co.acroquest.endosnipe.collector.Bootstrap
set LOGDIR=%BASEDIR%..\logs
set BASEPATH=%BASEDIR:\=/%
set JVMOPTIONS="-Dcollector.property=%BASEDIR%../conf/collector.properties;-Dlog4j.configuration=file:///%BASEPATH%/../conf/log4j.properties"
set JVMMS=64
set JVMMX=256
