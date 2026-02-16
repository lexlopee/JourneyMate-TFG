@echo off
set HOST=localhost
set PORT=15433

echo Esperando a que PostgreSQL esté listo en %HOST%:%PORT%...

:wait_pg
netstat -ano | findstr :%PORT% | findstr LISTENING >nul 2>&1

if %errorlevel% neq 0 (
    timeout /t 1 >nul
    goto wait_pg
)

echo PostgreSQL está listo.
exit /b 0
