@echo off
echo ============================================
echo   Iniciando entorno de desarrollo JourneyMate
echo ============================================

echo.
echo [1/3] Arrancando Docker Desktop...
start "" "C:\Program Files\Docker\Docker\Docker Desktop.exe"
timeout /t 5 >nul


echo.
echo [2/3] Detectando emulador Android...

set EMULATOR="%LOCALAPPDATA%\Android\Sdk\emulator\emulator.exe"

for /f "tokens=*" %%a in ('%EMULATOR% -list-avds') do (
    set AVD=%%a
    goto AVD_FOUND
)

:AVD_FOUND
if "%AVD%"=="" (
    echo ERROR: No hay emuladores creados.
    pause
    exit /b
)

echo Emulador encontrado: %AVD%
echo Iniciando emulador...
start "" %EMULATOR% -avd %AVD% -netdelay none -netspeed full

echo Esperando a que el emulador se conecte...
"%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe" wait-for-device

echo Esperando a que Android termine de arrancar...
:bootcheck
for /f "tokens=1" %%b in ('"%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe" shell getprop sys.boot_completed') do (
    if "%%b"=="1" goto bootdone
)
timeout /t 2 >nul
goto bootcheck

:bootdone
echo Android arrancado correctamente.

echo.
echo [3/3] Ejecutando Flutter...
cd journeymate_mobile
call flutter run

cd ..
echo Proyecto cerrado. Volviendo a la carpeta original.
pause
