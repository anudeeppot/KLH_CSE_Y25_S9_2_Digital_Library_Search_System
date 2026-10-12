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
echo   Starting CLI Interactive Mode (Main)...
echo ===================================================
echo.
java Main
pause
