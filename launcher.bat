@echo off
:menu
cls
echo ============================================
echo        LAUNCHER JOURNEYMATE (Windows)
echo ============================================
echo.
echo 1. Arrancar Web
echo 2. Arrancar Movil
echo 3. Arrancar Todo
echo 4. Parar Servicios
echo 5. Salir
echo.
set /p opcion="Selecciona una opcion: "

if "%opcion%"=="1" goto web
if "%opcion%"=="2" goto movil
if "%opcion%"=="3" goto todo
if "%opcion%"=="4" goto stop
if "%opcion%"=="5" goto salir

echo Opcion no valida.
timeout /t 1 >nul
goto menu

:web
call :stop_silent
start "" cmd /k "call scripts\windows\start_web.bat"
goto menu

:movil
call :stop_silent
start "" cmd /k "call scripts\windows\start_mobile.bat"
goto menu

:todo
call :stop_silent
start "" cmd /k "call scripts\windows\start_all.bat"
goto menu

:stop
call :stop_silent
echo Servicios detenidos.
timeout /t 1 >nul
goto menu

:stop_silent
taskkill /IM node.exe /F >nul 2>&1
taskkill /IM dart.exe /F >nul 2>&1
taskkill /IM flutter.exe /F >nul 2>&1
taskkill /IM qemu-system-x86_64.exe /F >nul 2>&1
exit /b

:salir
exit
