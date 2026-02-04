@echo off
echo Arrancando Docker Desktop...
start "" "C:\Program Files\Docker\Docker\Docker Desktop.exe"
timeout /t 5 >nul

echo Arrancando Flutter en Android...
cd journeymate_mobile
flutter run -d android
cd ..
