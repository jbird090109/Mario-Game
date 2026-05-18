@echo off
set "JAVA_HOME=C:\Program Files\Java\jdk-14.0.1"
set "PATH=%JAVA_HOME%\bin;%PATH%"

if not exist bin\Main.class (
    echo Game not compiled yet. Running compile.bat first...
    call "%~dp0compile.bat"
)

cd /d "%~dp0"
java -cp bin Main
