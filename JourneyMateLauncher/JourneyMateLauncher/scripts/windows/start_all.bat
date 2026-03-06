@echo off

call "%~dp0start_docker.bat"
timeout /t 3 >nul

call "%~dp0start_backend.bat"

cd /d "%~dp0..\..\..\..\JourneyMate"
docker compose up -d >nul 2>&1

call "%~dp0wait_for_postgres.bat"

cd /d "%~dp0..\..\..\..\journeymate-frontend"

start "" /b cmd /c "npm run dev >nul 2>&1"

:wait_vite
powershell -command ^
"try { ^
    $r = Invoke-WebRequest -Uri 'http://localhost:5173' -UseBasicParsing -TimeoutSec 1; ^
    if ($r.StatusCode -eq 200 -or $r.StatusCode -eq 404) { exit 0 } ^
} catch { exit 1 }"

if %errorlevel% neq 0 (
    timeout /t 1 >nul
    goto wait_vite
)

start "" http://localhost:5173

cd /d "%LOCALAPPDATA%\Android\Sdk\emulator"
for /f "tokens=*" %%a in ('emulator.exe -list-avds') do (
    set AVD=%%a
    goto AVD_FOUND
)

:AVD_FOUND
start "" emulator.exe -avd %AVD%

"%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe" wait-for-device

cd /d "%~dp0..\..\..\..\journeymate_mobile"
start "" /b cmd /c "flutter run -d emulator-5554 >nul 2>&1"

exit
