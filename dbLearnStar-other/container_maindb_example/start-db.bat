@echo off
setlocal

REM ---- Settings you can change ----
set "CONTAINER_NAME=dblearnstar_maindb"
set "HOST_PORT=5433"   REM use 5432 if free, else 5433 is fine
set "IMAGE=postgres:17"   REM match the .sh, or keep :16 if you prefer
set "POSTGRES_PASSWORD=postgres"  REM or CHANGEPASS to match .sh

REM ---- Bind-mount for persistent data ----
set "DATA_DIR=%cd%\data"
if not exist "%DATA_DIR%" mkdir "%DATA_DIR%"

REM ---- Start Podman VM (no error if already running) ----
podman machine start >NUL 2>NUL

REM ---- Remove stale container (ignore errors) ----
podman rm -f "%CONTAINER_NAME%" >NUL 2>NUL

REM ---- Run PostgreSQL ----
podman run --name "%CONTAINER_NAME%" ^
  -e POSTGRES_USER=postgres ^
  -e POSTGRES_PASSWORD=%POSTGRES_PASSWORD% ^
  -e PGDATA=/var/lib/postgresql/data/pgdata ^
  -v "%DATA_DIR%:/var/lib/postgresql/data" ^
  -p %HOST_PORT%:5432 ^
  -d "%IMAGE%"

timeout /t 2 >nul
podman ps

echo.
echo Postgres should be reachable at: localhost:%HOST_PORT%
echo Default superuser: postgres / password: %POSTGRES_PASSWORD%
echo To connect: psql -h localhost -p %HOST_PORT% -U postgres
endlocal
