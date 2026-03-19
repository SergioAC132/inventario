@echo off
echo Iniciando Inventario...

cd C:\inventario
taskkill /F /IM javaw.exe 2>nul
taskkill /F /IM node.exe 2>nul
timeout /t 2 /nobreak
start /B javaw -jar target\inventario-0.0.1-SNAPSHOT.jar

echo Esperando a que el backend inicie...
timeout /t 30 /nobreak

start /B serve C:\inventario\front -p 3000 -s --listen tcp://0.0.0.0:3000
timeout /t 3 /nobreak >nul
start "" "http://localhost:3000"
exit