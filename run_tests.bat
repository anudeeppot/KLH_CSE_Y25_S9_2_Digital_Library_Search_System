@echo off
echo ===================================================
echo   Compiling Algorithm Test Suite...
echo ===================================================
javac --add-modules jdk.httpserver *.java
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Compilation failed!
    pause
    exit /b %ERRORLEVEL%
)

echo.
echo ===================================================
echo   Running Module 5 & Module 6 Test Suite...
echo ===================================================
echo.
java AlgorithmTests
pause
