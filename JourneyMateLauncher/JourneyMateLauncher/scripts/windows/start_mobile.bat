@echo off

call "%~dp0start_docker.bat"
timeout /t 3 >nul

REM ARRANCAR BACKEND EN TERMINAL SEPARADA (Igual que en la web)
start "" cmd /k "%~dp0start_backend.bat"

cd /d "%~dp0..\..\..\..\JourneyMate"
docker compose up -d >nul 2>&1

call "%~dp0wait_for_postgres.bat"

cd /d "%LOCALAPPDATA%\Android\Sdk\emulator"
for /f "tokens=*" %%a in ('emulator.exe -list-avds') do (
    set AVD=%%a
    goto AVD_FOUND
)

:AVD_FOUND
start "" emulator.exe -avd %AVD%

"%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe" wait-for-device

cd /d "%~dp0..\..\..\..\journeymate_mobile"

REM Arrancar Flutter en una nueva ventana. Así podrás ver los logs de la app, 
REM recargar (Hot Reload con 'r') y saber si hay errores.
start "" cmd /k "flutter run -d emulator-5554"

exit