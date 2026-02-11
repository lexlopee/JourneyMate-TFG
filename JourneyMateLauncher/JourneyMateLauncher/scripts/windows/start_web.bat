@echo off
echo Iniciando WEB JourneyMate...

cd /d "%~dp0..\..\..\..\journeymate-frontend"

start "" /min cmd /k "npm run dev"

echo Esperando a que Vite arranque...

:wait_port
powershell -command "(Invoke-WebRequest -Uri http://localhost:5173 -UseBasicParsing -TimeoutSec 1) >$null 2>&1"
if %errorlevel% neq 0 (
    timeout /t 1 >nul
    goto wait_port
)

start "" http://localhost:5173
exit
