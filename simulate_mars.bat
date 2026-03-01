@echo off
color 0B
echo ==========================================
echo    STZ ROBOTICS - SIMULATION SEQUENCE
echo ==========================================
echo.

echo [1/2] Launching MARS Framework...
start "" "mars_terminal\Release\mars_gcs.exe"

echo.
echo [2/2] Starting WPILib Simulation...
call gradlew simulateJava

if %errorlevel% neq 0 (
    color 0C
    echo.
    echo [FATAL ERROR] Simulation crashed or failed to compile!
    pause
    exit /b %errorlevel%
)

echo.
echo Simulation Closed.
timeout /t 3 >nobreak