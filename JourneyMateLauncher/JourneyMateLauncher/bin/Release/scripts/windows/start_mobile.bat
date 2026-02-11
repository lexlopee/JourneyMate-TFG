@echo off
echo Iniciando entorno MOVIL JourneyMate...

set EMULATOR="%LOCALAPPDATA%\Android\Sdk\emulator\emulator.exe"

for /f "tokens=*" %%a in ('%EMULATOR% -list-avds') do (
    set AVD=%%a
    goto AVD_FOUND
)

:AVD_FOUND
start "" %EMULATOR% -avd %AVD%

"%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe" wait-for-device

cd /d "C:\Projects\JourneyMate-TFG\journeymate_mobile"

REM Flutter necesita terminal, pero la minimizamos
start "" /min cmd /k "flutter run -d emulator-5554"

exit
