@echo off
echo ===================================================
echo   Compiling Digital Library Search System...
echo ===================================================
javac --add-modules jdk.httpserver *.java
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Compilation failed!
    pause
    exit /b %ERRORLEVEL%
)

echo.
echo ===================================================
echo   Starting Digital Library Web Server...
echo   Open your browser at: http://localhost:8080
echo ===================================================
echo.
start http://localhost:8080
java WebServer
pause
