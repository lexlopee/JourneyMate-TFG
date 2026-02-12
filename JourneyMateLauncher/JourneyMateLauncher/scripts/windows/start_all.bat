@echo off

cd /d "%~dp0..\..\..\..\JourneyMate"
docker compose up -d

cd /d "%~dp0..\..\..\..\journeymate-frontend"
start "" /min cmd /c "npm run dev"

:wait_port
powershell -command "(Invoke-WebRequest -Uri http://localhost:5173 -UseBasicParsing -TimeoutSec 1) >$null 2>&1"
if %errorlevel% neq 0 (
    timeout /t 1 >nul
    goto wait_port
)

start "" http://localhost:5173

set EMULATOR="%LOCALAPPDATA%\Android\Sdk\emulator\emulator.exe"

for /f "tokens=*" %%a in ('%EMULATOR% -list-avds') do (
    set AVD=%%a
    goto AVD_FOUND
)

:AVD_FOUND
start "" %EMULATOR% -avd %AVD%

"%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe" wait-for-device

cd /d "%~dp0..\..\..\..\journeymate_mobile"
start "" /min cmd /c "flutter run -d emulator-5554"

exit
