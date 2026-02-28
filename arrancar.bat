@echo off
echo Inicializando Inventario...

cd C:\git\inventario
start "Backend" java -jar "C:\git\inventario\target\inventario-0.0.1-SNAPSHOT.jar"

echo Sistema iniciado correctamente
echo Abre tu navegador en http://localhost:8080