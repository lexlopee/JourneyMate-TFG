@echo off

:: 1. Iniciar Docker
call "%~dp0start_docker.bat"
timeout /t 3 >nul

:: 2. LANZAR BACKEND
start "Backend Server" cmd /k "%~dp0start_backend.bat"

:: 3. Docker Compose
cd /d "%~dp0..\..\..\..\JourneyMate"
docker compose up -d >nul 2>&1

:: 4. Base de datos
call "%~dp0wait_for_postgres.bat"

:: 5. LANZAR FRONTEND WEB
cd /d "%~dp0..\..\..\..\journeymate-frontend"
start "Frontend Web" cmd /k "npm run dev"

:: Esperar a que Vite responda correctamente
:wait_vite
echo Esperando a que el servidor Web este listo...
powershell -command "try { $r = Invoke-WebRequest -Uri 'http://localhost:5173' -UseBasicParsing -TimeoutSec 2; if ($r.StatusCode -eq 200) { exit 0 } else { exit 1 } } catch { exit 1 }"

if %errorlevel% neq 0 (
    timeout /t 2 >nul
    goto wait_vite
)

:: Pequeña pausa extra para asegurar que el DOM cargue bien
timeout /t 1 >nul
start "" "http://localhost:5173"

:: 6. LANZAR EMULADOR Y MÓVIL
cd /d "%LOCALAPPDATA%\Android\Sdk\emulator"
for /f "tokens=*" %%a in ('emulator.exe -list-avds') do (
    set AVD=%%a
    goto AVD_FOUND
)

:AVD_FOUND
start "" emulator.exe -avd %AVD%
echo Esperando a que el emulador este listo...
"%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe" wait-for-device

:: 7. EJECUTAR APP FLUTTER
cd /d "%~dp0..\..\..\..\journeymate_mobile"
start "Flutter Mobile" cmd /k "flutter run -d emulator-5554"

exit