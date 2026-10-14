# Stratton.app-FCT-DAW

![StrattonAPPlogo2](https://github.com/alvrichh/Stratton.app-FCT-DAW/assets/81918923/f086d67b-73db-46d6-b5d6-05a7eb72d567)

Proyecto final de grado superior de desarrollo de aplicaciones web, partiremos del proyecto final de base de datos del año anterior, ampliándolo con las tecnologías que hemos aprendido este año, utilizando para la seguridad security springboot, y para el frontend angular 17

#### Curso Escolar 2023-2024
#### Autor: [Álvaro Rodríguez Molina](https://github.com/alvrichh)
#### Tutor: [Juan Pablo Domínguez Mayo](https://github.com/profeInformatica101)
#### Fecha de Inicio: 15/03/2024
#### Fecha de Finalización: 12/06/2024

### Enlaces de interés:
<a href="https://www.figma.com/file/abBcwX662jrvwuJvsCnbFK/Stratton.app?type=design&node-id=0%3A1&mode=design&t=w5RrKRyjNq5V1P3H-1">
<img  height="70" src="https://static-00.iconduck.com/assets.00/apps-figma-icon-1024x1024-cb4t8vyj.png" alt="octocat" style="margin-right: 2rem;"/> 
</a>

<a href="https://marvelapp.com/prototype/a63i5gf">
<img  height="70" src="https://cdn.icon-icons.com/icons2/2429/PNG/512/marvel_logo_icon_147266.png" alt="octocat" style="margin-right: 2rem;"/> 
</a>

<a href="https://www.postman.com/alvrich">
<img  height="70" src="https://static-00.iconduck.com/assets.00/postman-icon-497x512-beb7sy75.png" alt="octocat" style="margin-right: 2rem;"/> 
</a>


## Puesta en marcha

### Opción A: Docker (todo en uno)

```bash
cp .env.example .env            # opcional: cambia contraseñas y JWT_SECRET
docker compose up --build
```

| Servicio | URL |
|----------|-----|
| Frontend (Angular + nginx) | http://localhost |
| API REST | http://localhost/api (también http://localhost:8080/api) |
| Swagger UI | http://localhost/swagger-ui.html |
| MySQL | localhost:3306 (usuario `root`, contraseña `DATABASE_PASSWORD`) |

nginx sirve la SPA y hace de proxy de `/api` hacia el backend, por lo que el navegador no necesita CORS.

### Opción B: en local (desarrollo)

Requisitos: JDK 17+, Maven 3.9+, Node 20+, y MySQL/MariaDB (o usa el perfil `h2`, que no necesita base de datos).

```bash
# Backend (http://localhost:8080) con MySQL en localhost:3306, usuario root sin contraseña
cd Backend && mvn spring-boot:run
#   ... o sin MySQL, con H2 en memoria:
cd Backend && mvn spring-boot:run -Dspring-boot.run.profiles=h2

# Frontend (http://localhost:4200)
cd Frontend && npm install && npm start
```

Todas las propiedades del backend se configuran con variables de entorno (`BBDD_HOST`, `BBDD_PORT`, `BBDD_NAME`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`, `DDL_AUTO`, `JWT_SECRET`, `CORS_ALLOWED_ORIGINS`, `SEED_DATA`, `SEED_RESET`, `REGISTRO_ABIERTO`...). Ver `Backend/src/main/resources/application.properties`.

> **Seguridad:** si `JWT_SECRET` no está definido, el backend genera una clave aleatoria en cada arranque (los usuarios tendrán que volver a iniciar sesión al reiniciar). En producción define siempre una clave propia: `openssl rand -base64 32`. Con `REGISTRO_ABIERTO=false` se desactiva el auto-registro y solo el ADMIN puede crear empleados.

### Usuarios de ejemplo

Al arrancar con la base de datos vacía se cargan datos de ejemplo (2 asesorías, 3 comercializadoras, 10 clientes con sus suministros) y estos usuarios:

| Usuario | Contraseña | Rol | Qué puede hacer |
|---------|------------|-----|-----------------|
| `admin` | `admin` | ADMIN | Todo: gestiona empleados y los clientes de cualquier empleado |
| `jefe` | `jefe` | MANAGER | Ve y gestiona los clientes de todos los empleados |
| `usuario` | `usuario` | USER | Gestiona únicamente sus propios clientes y suministros |

### Tests

```bash
cd Backend && mvn test          # 21 tests de integración (JUnit + MockMvc sobre H2)
cd Frontend && npm test         # 25 tests unitarios (Karma/Jasmine en ChromeHeadless)
```


## Motivación:
La principal motivación del proyecto es poder gestionar mis propios clientes o los clientes de una determinada asesoría energética como a sus empleados, para mejorar la gestión de los equipos comerciales y la organización de los clientes, pudiéndolos clasificar de manera clara y concisa. 

## ARQUITECTURA
![image](https://github.com/alvrichh/Stratton.app-FCT-DAW/assets/81918923/5ef284ac-e66f-4159-acb7-bc72c404f6b5)


| Entidad   | Atributos                                      |
|-----------|------------------------------------------------|
| Empleado   | id, nombre, apellidos, username, email, password (BCrypt), roles (USER/MANAGER/ADMIN), asesoria, clientes  |
| Cliente  | idCliente, nombre, apellidos, dni, email, direccion, telefono, iban, fechaSubidaContrato, fechaNacimiento, asesoria, comercializadora, empleado, suministros |
| Suministro     | cups (PK), estado (ACTIVO/PENDIENTE/INACTIVO), direccion, mensaje, potencia, cliente, comercializadora |
| Asesoria     | idAsesoria, nombre, direccion, descripcion, valoracion, clientes, empleados |
| Comercializadora     | idComercializadora, companiaContratada, plan, logo, suministros, clientes |


## Casos de uso
![image](https://github.com/alvrichh/Stratton.app-FCT-DAW/assets/81918923/751bfc3b-9e41-4d14-8f70-0cd2828b5ae1)

## Requisitos Funcionales:
Nota:(*) los requisitos marcados con ✔ son los que por el momento serán esenciales en el desarrollo del proyecto
- RF-1: El sistema debe permitir registrar a los clientes recopilando sus datos.✔
- RF-2: El sistema debe conocer a qué comercializadora pertenece cada cliente y cuál es el nombre de su oferta (idPlan).
- RF-3: La información de los clientes se basará en sus números de teléfono, que serán únicos para la identificación de la información.✔
- RF-4: Las ubicaciones completas, como comunidad autónoma, provincia y municipio, se determinarán mediante el código postal (ZIP).✔

````
import requests

def obtener_ubicacion_zippopotam(codigo_postal, pais='ES'):
    url = f'http://api.zippopotam.us/{pais}/{codigo_postal}'
    response = requests.get(url)
    return response.json()

codigo_postal = '41018'
ubicacion = obtener_ubicacion_zippopotam(codigo_postal)
print(ubicacion)
````

- RF-5: Cada cliente debe tener uno o más CUPS_luz como puntos de suministro, junto con su UBICACION.
- RF-6: El sistema debe permitir que los empleados estén asociados a una asesoría.✔
- RF-7: El sistema debe permitir que los clientes tengan información bancaria asociada.✔
- RF-8: El sistema debe permitir que los clientes tengan múltiples CUPS_gas como puntos de suministro.
- RF-9: El sistema debe permitir que los clientes estén asociados a un empleado en particular.
- RF-10: El sistema debe permitir la gestión de contratos de clientes, incluyendo la fecha de inicio y finalización del contrato, así como el estado del mismo (activo, inactivo, cancelado, etc.).✔
- RF-11: El sistema debe diferenciar ROLES de usuario para realizar determinadas acciones, ROL EMPLEADO y ROL JEFE.✔
## Reglas de Negocio:
- RN-1: El titular del contrato debe ser mayor de edad.✔
- RN-2: Los números de teléfono deben tener un formato válido.✔
- RN-3: Los puntos de suministro de energía deben estar asociados a una ubicación válida.
- RN-4: Los puntos de suministro de gas deben estar asociados a una ubicación válida.
- RN-5: Los clientes asignados a empleados deben tener un código válido.✔
- RN-6: La asignación de clientes a empleados debe ser coherente.✔
- RN-7: Los números de cuenta bancaria (CCC) deben tener un formato válido.✔
### Casos de Uso detallados para Gestión de Empleados:

**Ver Detalles Empleado:**
- **Actores**: Empleado
- **Flujo principal**:
  1. El empleado inicia sesión en el sistema.
  2. Accede a la opción de ver sus propios detalles.
  3. Visualiza la información personal.

**Ver Detalles de Otro Empleado:**
- **Actores**: Administrador
- **Flujo principal**:
  1. El administrador inicia sesión en el sistema.
  2. Accede a la opción de buscar y seleccionar un empleado.
  3. Visualiza los detalles del empleado seleccionado.

**Crear Empleado:**
- **Actores**: Administrador
- **Flujo principal**:
  1. El administrador inicia sesión en el sistema.
  2. Accede a la opción de agregar un nuevo empleado.
  3. Ingresa los datos del nuevo empleado (nombre, apellidos, email, rol, etc.).
  4. Guarda la información del nuevo empleado en el sistema.

**Actualizar Empleado:**
- **Actores**: Administrador
- **Flujo principal**:
  1. El administrador inicia sesión en el sistema.
  2. Accede a la opción de buscar y seleccionar un empleado para actualizar.
  3. Modifica los datos del empleado (nombre, apellidos, email, rol, etc.).
  4. Guarda los cambios realizados en la información del empleado.

**Eliminar Empleado:**
- **Actores**: Administrador
- **Flujo principal**:
  1. El administrador inicia sesión en el sistema.
  2. Accede a la opción de buscar y seleccionar un empleado para eliminar.
  3. Confirma la eliminación del empleado seleccionado.
  4. El empleado seleccionado es eliminado del sistema junto con todos sus datos asociados.

### Casos de Uso detallados para Gestión de Clientes por Empleados:

**Ver Detalles Cliente:**
- **Actores**: Empleado
- **Flujo principal**:
  1. El empleado inicia sesión en el sistema.
  2. Accede a la opción de ver detalles de clientes.
  3. Selecciona un cliente para ver sus detalles almacenados en el sistema.

**Agregar Cliente:**
- **Actores**: Empleado
- **Flujo principal**:
  1. El empleado inicia sesión en el sistema.
  2. Accede a la opción de agregar un nuevo cliente.
  3. Ingresa los datos del nuevo cliente (nombre, apellidos, email, teléfono, etc.).
  4. Guarda la información del nuevo cliente en el sistema.

**Actualizar Cliente:**
- **Actores**: Empleado
- **Flujo principal**:
  1. El empleado inicia sesión en el sistema.
  2. Accede a la opción de buscar y seleccionar un cliente para actualizar.
  3. Modifica los datos del cliente (nombre, apellidos, email, teléfono, etc.).
  4. Guarda los cambios realizados en la información del cliente.

**Eliminar Cliente:**
- **Actores**: Empleado
- **Flujo principal**:
  1. El empleado inicia sesión en el sistema.
  2. Accede a la opción de buscar y seleccionar un cliente para eliminar.
  3. Confirma la eliminación del cliente seleccionado.
  4. El cliente seleccionado es eliminado del sistema junto con todos sus datos asociados.

## Dependencias Spring Boot:
Las que usa realmente `Backend/pom.xml` (Spring Boot 3.2.3, Java 17):
- Spring Boot Starter Web: API REST con Tomcat embebido.
- Spring Boot Starter Security + JJWT 0.12: autenticación sin estado con tokens JWT y autorización por roles.
- Spring Boot Starter Data JPA + MySQL Connector/J: persistencia con Hibernate sobre MySQL/MariaDB.
- Spring Boot Starter Validation: validación de los datos de entrada (DNI, teléfono, IBAN, CUPS, email...).
- H2 Database (solo en ejecución): perfil `h2` para desarrollar y ejecutar los tests sin MySQL.
- springdoc-openapi: documentación interactiva de la API (Swagger UI).
- Lombok y Spring Boot DevTools: utilidades de desarrollo.

Se retiraron Thymeleaf (la interfaz es Angular) y OAuth2 Client (la autenticación con Google nunca llegó a implementarse; queda como mejora futura).
![image](https://github.com/alvrichh/Stratton.app-FCT-DAW/assets/81918923/d7f7e791-db1d-44a3-9f43-d87c1953a5a3)

# Definición de la API REST

Documentación interactiva (OpenAPI 3) en `http://localhost:8080/swagger-ui.html`. Todas las rutas salvo `/api/v1/auth/**` requieren la cabecera `Authorization: Bearer <token>`.

| Endpoint | Método | Rol | Descripción |
|----------|--------|-----|-------------|
| `/api/v1/auth/login` | POST | público | Inicia sesión. Devuelve `token`, `role`, `roles`, `id`, `username`, `nombre` |
| `/api/v1/auth/register` | POST | público | Registra un empleado con rol USER |
| `/api/v2/empleados` | GET | ADMIN | Lista de empleados |
| `/api/v2/empleados` | POST | ADMIN | Crea un empleado (contraseña cifrada, roles, asesoría) |
| `/api/v2/empleados/{id}` | GET / PUT / DELETE | ADMIN | Detalle (con clientes), actualización y borrado en cascada |
| `/api/v2/empleados/perfil` | GET | autenticado | Perfil del empleado autenticado con sus clientes |
| `/api/v2/empleados/{id}/clientes` | GET / POST | ADMIN | Clientes del empleado / crear cliente para el empleado |
| `/api/v2/empleados/{id}/total-clientes` | GET | ADMIN | Número de clientes |
| `/api/v2/empleados/{id}/suministros-clientes` | GET | ADMIN | Suministros de todos sus clientes |
| `/api/v2/clientes` | GET | autenticado | Clientes visibles (todos para ADMIN/MANAGER, propios para USER) |
| `/api/v2/clientes` | POST | autenticado | Crea un cliente (con CUPS y comercializadora opcionales) |
| `/api/v2/clientes/{id}` | GET / PUT / DELETE | autenticado (propietario o jefe) | Detalle, actualización y borrado |
| `/api/v2/clientes/{empleadoId}/clientes` | GET / POST | autenticado (propio o jefe) | Clientes de un empleado / crear |
| `/api/v2/clientes/{id}/suministros` | GET / POST | autenticado (propietario o jefe) | Suministros del cliente / añadir CUPS |
| `/api/v2/suministros/{cups}` | PUT / DELETE | autenticado (propietario o jefe) | Actualizar o eliminar un suministro |
| `/api/v2/comercializadoras` | GET | autenticado | Catálogo de comercializadoras |
| `/api/v2/asesorias` | GET | autenticado | Catálogo de asesorías |

Los errores devuelven siempre JSON con `status`, `error`, `message`, `path` y, en validaciones, `errors` por campo (400 datos no válidos, 401 sin sesión, 403 sin permiso, 404 no encontrado, 409 duplicado).

## Reglas de negocio implementadas

- RN-1 (mayoría de edad): si se informa la fecha de nacimiento, el titular debe tener 18 años o más.
- RN-2 (teléfono válido): 9 dígitos empezando por 6, 7, 8 o 9; además es único por cliente (RF-3).
- RN-5/RN-6 (asignación coherente): un USER solo puede crear, ver, editar o borrar sus propios clientes; ADMIN y MANAGER pueden operar sobre los de cualquier empleado.
- RN-7 (IBAN válido): formato español `ES` + 22 dígitos.
- CUPS con formato oficial (`ES` + 16 dígitos + 2 letras de control) y único en el sistema; un cliente puede tener varios suministros (RF-5/RF-8).
- Los DNI/NIE se validan por formato y son únicos.

## Estado del proyecto

Octubre 2026: el proyecto se ha revisado y corregido por completo para que compile, arranque y los flujos principales funcionen de extremo a extremo.

- Separación de responsabilidades: Angular (UI) + API REST en Spring Boot. Las plantillas Thymeleaf y el controlador `/api/v1/admin` duplicado se han eliminado.
- Autenticación JWT con un único filtro, roles con prefijo `ROLE_`, `@EnableMethodSecurity`, reglas de autorización reales (antes todos los endpoints eran `permitAll`) y comprobación de propiedad de los clientes en los servicios.
- Validación de datos con Bean Validation (DNI, teléfono, IBAN, CUPS, email, mayoría de edad) y manejo centralizado de excepciones con respuestas JSON uniformes.
- Modelo de datos coherente: `fechaSubidaContrato`, CUPS y comercializadora se persisten de verdad (antes se devolvían valores falsos), los suministros se gestionan desde la ficha del cliente.
- Documentación de la API con springdoc-openapi (Swagger UI).
- CORS configurable por variable de entorno; frontend con interceptor `Authorization`, guards de ruta y `environment` por entorno.
- Pruebas de integración del backend (JUnit 5 + MockMvc + H2) y tests unitarios del frontend.
- Despliegue con Docker Compose (MySQL con healthcheck, backend configurado por variables de entorno, nginx con fallback SPA y proxy `/api`).

Pendiente / ideas futuras: autenticación con Google (OAuth2), gestión de contratos de gas diferenciada de luz, ubicación por código postal (RF-4), avisos de fin de contrato.
