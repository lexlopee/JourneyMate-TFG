@echo off

call "%~dp0start_docker.bat"
timeout /t 10 >nul

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
flutter run -d emulator-5554 >nul 2>&1 &
exit
