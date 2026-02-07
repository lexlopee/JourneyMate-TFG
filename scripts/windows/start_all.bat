@echo off
cd /d "%~dp0"

echo ============================================
echo   Iniciando TODO el entorno JourneyMate
echo ============================================
echo.

echo [1/4] Arrancando WEB (Vite)...
start "" cmd /k "cd ..\..\journeymate-frontend && npm run dev"

echo.
echo [2/4] Esperando a que Vite esté disponible...

:wait_port
powershell -command "(Invoke-WebRequest -Uri http://localhost:5173 -UseBasicParsing -TimeoutSec 1) >$null 2>&1"
if %errorlevel% neq 0 (
    timeout /t 1 >nul
    goto wait_port
)

echo Vite está listo. Abriendo navegador...
start "" http://localhost:5173

echo.
echo [3/4] Arrancando emulador Android...

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

start "" %EMULATOR% -avd %AVD%

echo Esperando a que el emulador arranque...
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
echo [4/4] Ejecutando Flutter en nueva terminal...
start "" cmd /k "cd ..\..\journeymate_mobile && flutter run -d emulator-5554"

exit
