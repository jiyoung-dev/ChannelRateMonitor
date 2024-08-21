@echo off
REM 프로젝트 홈디렉토리 설정 
set PROJECT_DIR=D:\projects\ChannelRateMonitor

REM 클래스패스 설정 
set CLASSPATH=%PROJECT_DIR%\bin\classes;%PROJECT_DIR%\lib\*;%PROJECT_DIR%\config

REM 애플리케이션 실행
java -cp "%CLASSPATH%" com.hansol.channelmonitor.ChannelMonitorApplication
