@echo off

REM 프로젝트 디렉토리 설정
set PROJECT_DIR=D:\projects\ChannelRateMonitor

REM 클래스파일 저장 경로 설정
set TARGET_DIR=%PROJECT_DIR%\bin\classes

REM 소스파일 경로 설정
set SOURCE_DIR=%PROJECT_DIR%\src\main\java

REM 라이브러리 경로 설정 (lib 하위 모든 JAR 파일 포함)
set LIB_DIR=%PROJECT_DIR%\lib\*

REM 출력 경로 생성
if not exist %TARGET_DIR% (
    mkdir %TARGET_DIR%
)

REM 모든 Java 소스 파일 컴파일
javac -d %TARGET_DIR% -classpath "%LIB_DIR%;%TARGET_DIR%" -encoding UTF-8 %SOURCE_DIR%\com\hansol\channelmonitor\*.java

REM 컴파일 완료 메시지 출력
if %errorlevel% neq 0 (
    echo Compilation failed.
) else (
    echo Compilation successful.
)
