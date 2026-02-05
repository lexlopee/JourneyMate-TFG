@echo off
echo Arrancando Docker Desktop...
start "" "C:\Program Files\Docker\Docker\Docker Desktop.exe"
timeout /t 5 >nul

echo Arrancando contenedores...
cd JourneyMate
docker compose up -d
cd ..

echo Arrancando React (Vite)...
cd journeymate-frontend
start "" cmd /k "npm run dev -- --open"
cd ..
