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
start "" cmd /k "call start_web.bat"
goto menu

:movil
call :stop_silent
start "" cmd /k "call start_mobile.bat"
goto menu

:todo
call :stop_silent
start "" cmd /k "call start_all.bat"
goto menu

:stop
call :stop_silent
echo Servicios detenidos correctamente.
timeout /t 1 >nul
goto menu

:stop_silent
echo Cerrando procesos anteriores...

REM Cerrar Vite
taskkill /IM node.exe /F >nul 2>&1

REM Cerrar Flutter
taskkill /IM dart.exe /F >nul 2>&1
taskkill /IM flutter.exe /F >nul 2>&1

REM Cerrar emulador Android
taskkill /IM qemu-system-x86_64.exe /F >nul 2>&1

REM Parar Docker
cd JourneyMate
docker compose down >nul 2>&1
cd ..

exit /b

:salir
echo Saliendo del launcher. Hasta luego!
exit
