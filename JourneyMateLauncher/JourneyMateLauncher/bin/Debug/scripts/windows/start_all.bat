@echo off
echo Iniciando TODO JourneyMate...

REM ============================
REM 1. WEB silencioso
REM ============================
start "" /min cmd /c "cd /d C:\Projects\JourneyMate-TFG\journeymate-frontend && npm run dev >nul 2>&1"

:wait_port
powershell -command "(Invoke-WebRequest -Uri http://localhost:5173 -UseBasicParsing -TimeoutSec 1) >$null 2>&1"
if %errorlevel% neq 0 (
    timeout /t 1 >nul
    goto wait_port
)

start "" http://localhost:5173

REM ============================
REM 2. BACKEND silencioso
REM ============================
start "" /min cmd /c "cd /d C:\Projects\JourneyMate-TFG\JourneyMate && mvn spring-boot:run >nul 2>&1"

REM ============================
REM 3. EMULADOR visible
REM ============================
set EMULATOR="%LOCALAPPDATA%\Android\Sdk\emulator\emulator.exe"
for /f "tokens=*" %%a in ('%EMULATOR% -list-avds') do (
    set AVD=%%a
    goto AVD_FOUND
)

:AVD_FOUND
start "" %EMULATOR% -avd %AVD%

"%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe" wait-for-device

REM ============================
REM 4. FLUTTER visible
REM ============================
start "" cmd /k "cd /d C:\Projects\JourneyMate-TFG\journeymate_mobile && flutter run -d emulator-5554"

exit
