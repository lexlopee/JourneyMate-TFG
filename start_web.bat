@echo off
echo Arrancando Docker Desktop...
start "" "C:\Program Files\Docker\Docker\Docker Desktop.exe"
timeout /t 5 >nul

echo Arrancando React (Vite)...
cd journeymate-frontend
start cmd /k "npm run dev"
cd ..
