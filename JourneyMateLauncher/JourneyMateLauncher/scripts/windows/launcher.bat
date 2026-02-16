@echo off

docker compose down --remove-orphans >nul 2>&1

for /f "tokens=*" %%i in ('docker ps -q') do docker stop %%i >nul 2>&1
for /f "tokens=*" %%i in ('docker ps -aq') do docker rm %%i >nul 2>&1

taskkill /IM java.exe /F >nul 2>&1
taskkill /IM mvn.exe /F >nul 2>&1

taskkill /IM node.exe /F >nul 2>&1

taskkill /IM qemu-system-x86_64.exe /F >nul 2>&1
taskkill /IM adb.exe /F >nul 2>&1

taskkill /IM flutter.exe /F >nul 2>&1
taskkill /IM dart.exe /F >nul 2>&1

taskkill /IM cmd.exe /F >nul 2>&1
taskkill /IM powershell.exe /F >nul 2>&1

exit
