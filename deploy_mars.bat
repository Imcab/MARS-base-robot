@echo off
color 0A
echo ==========================================
echo    STZ ROBOTICS - DEPLOYMENT SEQUENCE
echo ==========================================
echo.

echo [1/2] Deploying Java Code to Robot...
call gradlew deploy

if %errorlevel% neq 0 (
    color 0C
    echo.
    echo [FATAL ERROR] Deploy failed! Fix your Java code first.
    pause
    exit /b %errorlevel%
)

echo.
echo [2/2] Launching MARS Framework...
start "" "mars_terminal\Release\mars_gcs.exe"

echo.
echo Sequence Complete. Good luck, Captain!
timeout /t 3 >nobreak