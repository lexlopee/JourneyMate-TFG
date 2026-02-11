@echo off
echo Iniciando entorno MOVIL JourneyMate...

REM Detectar emulador
set EMULATOR="%LOCALAPPDATA%\Android\Sdk\emulator\emulator.exe"

for /f "tokens=*" %%a in ('%EMULATOR% -list-avds') do (
    set AVD=%%a
    goto AVD_FOUND
)

:AVD_FOUND
start "" %EMULATOR% -avd %AVD%

echo Esperando a que arranque...
"%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe" wait-for-device

cd /d "C:\Projects\JourneyMate-TFG\journeymate_mobile"

echo Ejecutando Flutter...
start "" cmd /k "flutter run -d emulator-5554"

exit
