# Actualizar MediStock — Pasos de despliegue

---

## En el equipo de desarrollo

**1. Reconstruir el front** (solo si hubo cambios en el frontend):

```cmd
cd inventario-front
npm run build
```

**2. Copiar el build a la carpeta front:**

```cmd
xcopy /E /Y dist\* ..\front\
```

**3. Compilar el nuevo JAR:**

```cmd
cd ..
.\mvnw package -DskipTests
```

**4. Archivos listos para copiar al equipo final:**

- `target\inventario-0.0.1-SNAPSHOT.jar`
- La carpeta `front\` completa (solo si hubo cambios en el frontend)

---

## En el equipo final

**1. Detener el sistema:**

Abre el Administrador de Tareas y termina los procesos `javaw.exe` y `node.exe`.

**2. Reemplazar los archivos:**

- Copia el nuevo JAR a `C:\inventario\target\`
- Si hubo cambios en el front, reemplaza el contenido de `C:\inventario\front\`

**3. Verificar el archivo de configuración externo:**

Asegúrate de que exista `C:\inventario\application.properties` con el siguiente contenido:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/inventario?createDatabaseIfNotExist=true&allowPublicKeyRetrieval=true&useSSL=false
spring.datasource.username=inventario
spring.datasource.password=TU_CONTRASENA
```

**4. Arrancar el sistema:**

Ejecuta el `arrancar.bat` y espera 30 segundos antes de abrir el navegador en:

```
http://localhost:3000
```

---

## Notas importantes

- Nunca sobreescribas el `application.properties` externo del equipo final, ese tiene la contraseña correcta de ese equipo.
- Si solo cambiaste código del back, no es necesario reconstruir el front y viceversa.
- Si la IP del equipo final cambia, actualiza el `.env.production` en el equipo de desarrollo, reconstruye el front y copia el nuevo `front\` al equipo final.
