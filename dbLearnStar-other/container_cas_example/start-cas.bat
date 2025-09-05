@echo off
setlocal

REM === Settings you can change ===
set "CONTAINER_NAME=cas-dev"
set "HOST_PORT=8080"
set "IMAGE=apereo/cas:6.6.7"

REM === Paths mapped into the container ===
set "HOST_SERVICES=%cd%\services"
set "HOST_CONFIG=%cd%\config"

REM === Clean up any old container (ignore errors) ===
podman rm -f "%CONTAINER_NAME%" >NUL 2>NUL

REM === Run (replace 'podman' with 'docker' if you use Docker Desktop) ===
podman run --name "%CONTAINER_NAME%" ^
  -v "%HOST_SERVICES%:/etc/cas/services" ^
  -v "%HOST_CONFIG%:/etc/cas/config" ^
  --rm ^
  -p %HOST_PORT%:8080 ^
  -d ^
  %IMAGE% ^
    --cas.standalone.configuration-directory=/etc/cas/config ^
    --server.ssl.enabled=false ^
    --server.port=8080 ^
    --management.server.port=10000 ^
    --cas.service-registry.core.init-from-json=true ^
    --cas.service-registry.json.location=file:/etc/cas/services

echo.
podman ps
echo.
echo CAS should be reachable at: http://localhost:%HOST_PORT%/cas/login
endlocal
