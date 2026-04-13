# Genesis Backend API

API REST construida con Spring Boot para gestionar usuarios, planes, suscripciones, wallet de tokens, operaciones y metricas.

## Stack tecnico

- Java 22
- Spring Boot 4
- Spring Security + JWT
- Spring Data JPA (Hibernate)
- MySQL 8 (Docker)
- Maven Wrapper

## Estructura relevante

- API contract: [open-api.yml](open-api.yml)
- SQL DDL: [scripts/mysql/create_tables.sql](scripts/mysql/create_tables.sql)
- SQL seed (datos ejemplo): [scripts/mysql/seed_entities.sql](scripts/mysql/seed_entities.sql)
- Config app: [src/main/resources/application.properties](src/main/resources/application.properties)
- Docker: [docker-compose.yml](docker-compose.yml)

## Prerrequisitos

- Docker Desktop (o Docker Engine + Compose)
- Java 22 instalado y disponible en PATH
- Git (opcional)

## 1) Levantar MySQL con Docker Compose

Desde la raiz del proyecto:

```bash
docker compose up -d mysql
```

Verificar estado:

```bash
docker compose ps
```

La base queda expuesta en:

- Host: localhost
- Puerto: 3307
- DB: genesis_db
- Usuario: genesis_user
- Password: Genesis123*

## 2) Crear esquema SQL (opcional pero recomendado en ambiente limpio)

El proyecto tiene `spring.jpa.hibernate.ddl-auto=update`, por lo que Hibernate puede crear/actualizar tablas al iniciar.

Si quieres forzar el esquema exacto actual por script:

PowerShell:

```powershell
Get-Content scripts/mysql/create_tables.sql | docker compose exec -T mysql mysql -u genesis_user -p"Genesis123*" genesis_db
```

Bash:

```bash
docker compose exec -T mysql sh -c 'mysql -u genesis_user -p"Genesis123*" genesis_db' < scripts/mysql/create_tables.sql
```

## 3) Cargar datos iniciales (opcional)

Si quieres datos de ejemplo adicionales:

PowerShell:

```powershell
Get-Content scripts/mysql/seed_entities.sql | docker compose exec -T mysql mysql -u genesis_user -p"Genesis123*" genesis_db
```

Bash:

```bash
docker compose exec -T mysql sh -c 'mysql -u genesis_user -p"Genesis123*" genesis_db' < scripts/mysql/seed_entities.sql
```

Nota: ademas del seed SQL, el proyecto tiene un inicializador automatico en [src/main/java/com/breaze/genesis/config/DataInitializer.java](src/main/java/com/breaze/genesis/config/DataInitializer.java) que crea catalogos base y un admin por defecto si no existe.

## 4) Ejecutar la aplicacion

En Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

En Linux/macOS:

```bash
./mvnw spring-boot:run
```

La API queda en:

- http://localhost:8080

Health check:

- GET http://localhost:8080/api/v1/health

## 5) Usuario admin inicial

Si la BD esta vacia al iniciar, se crea:

- Email: admin@genesis.com
- Password: Admin123*

## 6) Ejecutar pruebas

Windows:

```powershell
.\mvnw.cmd test
```

Linux/macOS:

```bash
./mvnw test
```

## 7) Flujo rapido recomendado

1. Levantar MySQL con Compose.
2. (Opcional) Ejecutar DDL + seed.
3. Levantar Spring Boot con Maven Wrapper.
4. Probar `/api/v1/health`.
5. Consumir endpoints segun [open-api.yml](open-api.yml).

## Limpieza de entorno

Parar contenedores:

```bash
docker compose down
```

Parar y borrar volumen de datos:

```bash
docker compose down -v
```

Si necesitas recrear solo el volumen principal:

```bash
docker volume rm genesis_mysql_data
```
