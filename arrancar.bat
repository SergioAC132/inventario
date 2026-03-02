@echo off
echo Iniciando MediStock...

cd C:\git\inventario
start "Backend" java -jar "target\inventario-0.0.1-SNAPSHOT.jar"

echo Esperando a que el backend inicie...
timeout /t 30 /nobreak

start "Frontend" npx serve C:\git\inventario\front -p 3000 -s --listen tcp://0.0.0.0:3000

timeout /t 3 /nobreak

start "" "http://localhost:3000"