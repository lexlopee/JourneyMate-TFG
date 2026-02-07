@echo off
cd /d "%~dp0"

echo ============================================
echo   Iniciando entorno WEB JourneyMate
echo ============================================
echo.

echo [1/3] Entrando en el proyecto frontend...
cd ..\..\journeymate-frontend

echo.
echo [2/3] Arrancando React (Vite)...
start "" cmd /k "npm run dev"

echo.
echo [3/3] Esperando a que Vite esté disponible...

:wait_port
powershell -command "(Invoke-WebRequest -Uri http://localhost:5173 -UseBasicParsing -TimeoutSec 1) >$null 2>&1"
if %errorlevel% neq 0 (
    timeout /t 1 >nul
    goto wait_port
)

echo Vite está listo. Abriendo navegador...
start "" http://localhost:5173

exit
