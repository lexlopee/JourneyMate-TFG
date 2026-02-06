@echo off
echo ============================================
echo   Iniciando entorno WEB JourneyMate
echo ============================================

echo.
echo [1/3] Arrancando Docker Desktop...
start "" "C:\Program Files\Docker\Docker\Docker Desktop.exe"
timeout /t 5 >nul

echo.
echo [2/3] Arrancando contenedores Docker...
cd JourneyMate
docker compose up -d
cd ..

echo.
echo [3/3] Arrancando React (Vite)...
cd journeymate-frontend

REM 1) Arrancar Vite en la MISMA terminal
start "" /B cmd /c "npm run dev"

echo Esperando a que Vite esté disponible...

REM 2) Esperar a que el puerto 5173 responda
:wait_port
powershell -command "(Invoke-WebRequest -Uri http://localhost:5173 -UseBasicParsing -TimeoutSec 1) >$null 2>&1"
if %errorlevel% neq 0 (
    timeout /t 1 >nul
    goto wait_port
)

REM 3) Abrir navegador
start "" http://localhost:5173

cd ..
