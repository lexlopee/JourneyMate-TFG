@echo off

cd /d "%~dp0..\..\..\..\JourneyMate"
docker compose up -d

start "" /min cmd /c "mvn spring-boot:run"

cd /d "%~dp0..\..\..\..\journeymate_mobile"

set EMULATOR="%LOCALAPPDATA%\Android\Sdk\emulator\emulator.exe"

for /f "tokens=*" %%a in ('%EMULATOR% -list-avds') do (
    set AVD=%%a
    goto AVD_FOUND
)

:AVD_FOUND
start "" %EMULATOR% -avd %AVD%

"%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe" wait-for-device

start "" /min cmd /c "flutter run -d emulator-5554"

exit
