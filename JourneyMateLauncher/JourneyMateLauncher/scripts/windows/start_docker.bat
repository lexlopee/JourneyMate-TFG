@echo off
echo ============================
echo   Iniciando Docker...
echo ============================

REM 1. Buscar Docker Desktop
set DOCKER_PATH=

if exist "%ProgramData%\Microsoft\Windows\Start Menu\Programs\Docker Desktop.lnk" (
    set DOCKER_PATH="%ProgramData%\Microsoft\Windows\Start Menu\Programs\Docker Desktop.lnk"
)
if exist "%AppData%\Microsoft\Windows\Start Menu\Programs\Docker Desktop.lnk" (
    set DOCKER_PATH="%AppData%\Microsoft\Windows\Start Menu\Programs\Docker Desktop.lnk"
)
if exist "%LocalAppData%\Docker\Docker Desktop.lnk" (
    set DOCKER_PATH="%LocalAppData%\Docker\Docker Desktop.lnk"
)

REM 2. Abrir Docker Desktop si existe
if defined DOCKER_PATH (
    echo Abriendo Docker Desktop...
    start "" %DOCKER_PATH%
)

REM 3. Esperar a que Docker esté listo
echo Esperando a que Docker arranque...
:wait_docker
docker info >nul 2>&1
if %errorlevel% neq 0 (
    timeout /t 2 >nul
    goto wait_docker
)

echo Docker está listo.

REM 4. Levantar contenedores
echo Levantando contenedores con docker compose...
cd /d "%~dp0..\..\..\..\JourneyMate"
docker compose up -d

echo Contenedores levantados.
exit /b 0
