@echo off
REM Cambiar al directorio donde está este script
cd /d "%~dp0"

echo ============================================
echo   Iniciando entorno MOVIL JourneyMate
echo ============================================

echo.
echo [1/3] Detectando emulador Android...

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
start "" %EMULATOR% -avd %AVD%

echo.
echo [2/3] Esperando a que el emulador arranque...
"%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe" wait-for-device

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
cd ..\..\journeymate_mobile
start "" cmd /k "flutter run -d emulator-5554"

exit
