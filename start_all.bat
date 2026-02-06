@echo off
echo ============================================
echo   Iniciando TODO el entorno JourneyMate
echo ============================================

echo.
echo [1/6] Arrancando Docker Desktop...
start "" "C:\Program Files\Docker\Docker\Docker Desktop.exe"
timeout /t 5 >nul

echo.
echo [2/6] Arrancando contenedores Docker...
cd JourneyMate
docker compose up -d
cd ..

echo.
echo [3/6] Arrancando React (Vite) en nueva terminal...
cd journeymate-frontend
start "" cmd /k "npm run dev"
cd ..

echo.
echo [4/6] Abriendo navegador cuando Vite esté listo...

:wait_port
powershell -command "(Invoke-WebRequest -Uri http://localhost:5173 -UseBasicParsing -TimeoutSec 1) >$null 2>&1"
if %errorlevel% neq 0 (
    timeout /t 1 >nul
    goto wait_port
)

start "" http://localhost:5173

echo.
echo [5/6] Arrancando emulador Android...

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
echo [6/6] Ejecutando Flutter en Android en nueva terminal...
start "" cmd /k "cd journeymate_mobile && flutter run -d emulator-5554"

echo.
echo ============================================
echo   TODO el entorno está arrancado correctamente
echo ============================================
