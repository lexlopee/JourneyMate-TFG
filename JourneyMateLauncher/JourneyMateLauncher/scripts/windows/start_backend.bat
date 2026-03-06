@echo off
echo ============================
echo   Iniciando Backend...
echo ============================

REM Ir a la carpeta correcta del backend
cd /d "%~dp0..\..\..\..\JourneyMate"

REM Arrancar Spring Boot con Maven Wrapper (mvnw.cmd)
cmd /k mvnw.cmd spring-boot:run
