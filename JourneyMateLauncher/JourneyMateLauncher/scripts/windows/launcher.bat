@echo off

cd /d "%~dp0..\..\..\..\JourneyMate"

echo Apagando Docker...
docker compose down --remove-orphans >nul 2>&1

echo Matando Java...
taskkill /IM java.exe /F >nul 2>&1
taskkill /IM javaw.exe /F >nul 2>&1

echo Matando Maven...
taskkill /IM mvn.exe /F >nul 2>&1

echo Matando Node...
taskkill /IM node.exe /F >nul 2>&1

echo Matando Emulador...
taskkill /IM qemu-system-x86_64.exe /F >nul 2>&1
taskkill /IM adb.exe /F >nul 2>&1

echo Matando Flutter...
taskkill /IM flutter.exe /F >nul 2>&1
taskkill /IM dart.exe /F >nul 2>&1

exit
