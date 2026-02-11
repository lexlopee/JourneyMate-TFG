@echo off

taskkill /IM node.exe /F >nul 2>&1
taskkill /IM dart.exe /F >nul 2>&1
taskkill /IM flutter.exe /F >nul 2>&1
taskkill /IM java.exe /F >nul 2>&1
taskkill /IM qemu-system-x86_64.exe /F >nul 2>&1
taskkill /IM mvn.cmd /F >nul 2>&1

exit
