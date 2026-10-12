
@echo off
title Digital Library Search System
cd /d "%~dp0"

echo ==========================================
echo Compiling Digital Library Search System...
echo ==========================================
javac --add-modules jdk.httpserver *.java

if errorlevel 1 (
    echo.
    echo ERROR: Compilation failed.
    pause
    exit /b 1
)

echo.
echo Starting Java backend...
start "Digital Library Server" /D "%CD%" cmd /k "java WebServer"

echo Waiting for the backend to become ready...

powershell -NoProfile -ExecutionPolicy Bypass -Command "$ready=$false; for($i=0; $i -lt 30; $i++){try{$null=Invoke-RestMethod -Uri 'http://localhost:8080/api/status' -TimeoutSec 1; $ready=$true; break}catch{Start-Sleep -Seconds 1}}; if($ready){Start-Process 'http://localhost:8080'}else{Write-Host 'ERROR: Server did not become ready. Check the Java server window.'; exit 1}"

if errorlevel 1 pause
