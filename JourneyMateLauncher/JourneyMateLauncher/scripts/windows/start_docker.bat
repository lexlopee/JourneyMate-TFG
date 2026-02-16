@echo off
set DOCKER_PATH=

if exist "%ProgramData%\Microsoft\Windows\Start Menu\Programs\Docker Desktop.lnk" (
    set DOCKER_PATH="%ProgramData%\Microsoft\Windows\Start Menu\Programs\Docker Desktop.lnk"
)
if exist "%ProgramData%\Microsoft\Windows\Start Menu\Programs\Docker\Docker Desktop.lnk" (
    set DOCKER_PATH="%ProgramData%\Microsoft\Windows\Start Menu\Programs\Docker\Docker Desktop.lnk"
)

if exist "%AppData%\Microsoft\Windows\Start Menu\Programs\Docker Desktop.lnk" (
    set DOCKER_PATH="%AppData%\Microsoft\Windows\Start Menu\Programs\Docker Desktop.lnk"
)
if exist "%AppData%\Microsoft\Windows\Start Menu\Programs\Docker\Docker Desktop.lnk" (
    set DOCKER_PATH="%AppData%\Microsoft\Windows\Start Menu\Programs\Docker\Docker Desktop.lnk"
)

if exist "%LocalAppData%\Docker\Docker Desktop.lnk" (
    set DOCKER_PATH="%LocalAppData%\Docker\Docker Desktop.lnk"
)

if defined DOCKER_PATH (
    start "" %DOCKER_PATH%
    exit /b 0
)

exit /b 1
