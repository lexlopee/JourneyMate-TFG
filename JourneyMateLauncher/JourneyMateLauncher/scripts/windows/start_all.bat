@echo off
echo Iniciando TODO JourneyMate...

REM WEB minimizado
start "" /min cmd /k "cd /d C:\Projects\JourneyMate-TFG\journeymate-frontend && npm run dev"

REM Esperar Vite
:wait_port
powershell -command "(Invoke-WebRequest -Uri http://localhost:5173 -UseBasicParsing -TimeoutSec 1) >$null 2>&1"
if %errorlevel% neq 0 (
    timeout /t 1 >nul
    goto wait_port
)

start "" http://localhost:5173

REM BACKEND minimizado
start "" /min cmd /k "cd /d C:\Projects\JourneyMate-TFG\JourneyMate && mvn spring-boot:run"

REM EMULADOR visible
set EMULATOR="%LOCALAPPDATA%\Android\Sdk\emulator\emulator.exe"
for /f "tokens=*" %%a in ('%EMULATOR% -list-avds') do (
    set AVD=%%a
    goto AVD_FOUND
)

:AVD_FOUND
start "" %EMULATOR% -avd %AVD%

"%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe" wait-for-device

REM FLUTTER minimizado
start "" /min cmd /k "cd /d C:\Projects\JourneyMate-TFG\journeymate_mobile && flutter run -d emulator-5554"

exit
