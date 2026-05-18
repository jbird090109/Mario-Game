@echo off
set "JAVA_HOME=C:\Program Files\Java\jdk-14.0.1"
set "PATH=%JAVA_HOME%\bin;%PATH%"

if not exist bin mkdir bin

echo Compiling Super Mario World...
javac -d bin src\*.java
if errorlevel 1 (
    echo.
    echo Compilation failed.
    pause
    exit /b 1
)

echo.
echo Done! Run the game with: run.bat
pause
