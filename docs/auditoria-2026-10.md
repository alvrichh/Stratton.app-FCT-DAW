# Auditoría técnica de StrattonApp (octubre 2026)

Auditoría del repositorio en el commit `53c46de` (estado previo a las correcciones), realizada con un flujo de agentes: 7 auditores independientes (uno por dimensión), verificación adversarial por lotes de cada hallazgo y síntesis final. Resultado: **181 hallazgos confirmados** y 2 descartados. Todos los hallazgos se refieren al código original; la columna *Estado* indica qué ha pasado con cada uno en la rama corregida.

| Severidad | Hallazgos |
|---|---|
| Bloqueante | 4 |
| Alta | 55 |
| Media | 49 |
| Baja | 73 |

| Dimensión | Hallazgos |
|---|---|
| Backend: persistencia y datos | 42 |
| Frontend Angular | 31 |
| Backend: compilación y ejecución | 29 |
| Construcción y despliegue | 22 |
| Backend: seguridad y autenticación | 22 |
| Contrato API frontend ↔ backend | 20 |
| Funcionalidad frente al README | 15 |

## Resumen ejecutivo

Estado real del repo en 53c46de: la app "parece" funcionar sólo porque todo /api/v2/** es permitAll, @PreAuthorize es inerte (sin @EnableMethodSecurity), el JWT nunca valida (Integer.equals(Long)) y el frontend nunca envía Authorization. Tres causas raíz explican ~150 de los 181 hallazgos: (1) los controladores enlazan entidades JPA como @RequestBody y devuelven getters falsos (CUPS="CUPS", comercializadora="No especificado", fecha=hoy), con lo que el núcleo del dominio (puntos de suministro) ni se persiste ni se muestra; (2) seguridad decorativa en ambos extremos (sin cors() en la cadena, sin interceptor/guards, passwords sin cifrar, roles descartados por el desajuste role/roles); (3) configuración no parametrizada (datasource localhost, ddl-auto=create + seeder destructivo, Dockerfiles con nombre incorrecto, nginx.conf no copiado, URLs hardcodeadas). Sobre JDK 21 ni siquiera compila (Lombok 1.18.22 fijado).

Orden del roadmap: A (compila/construye limpio) → B (arranca contra MySQL en Docker y contra H2 en local, seed idempotente, compose end-to-end same-origin vía nginx /api) → C (auth real: JWT Long + interceptor + guards + method security, DTOs de entrada con password cifrada y roles, clientes con CUPS/comercializadora/fecha reales persistidos como Suministro, cascadas, errores HTTP coherentes, listas sin TypeError, Bootstrap/SweetAlert consistentes) → D (RF/RN del README: CRUD de suministros luz/gas con fechas y estado, catálogos de comercializadoras/asesorías, validadores DNI/IBAN/teléfono/edad, CP→ubicación, contenido falso fuera, tests y CI). Los pasos C1, C2, C3 y B5 deben entrar en el MISMO cambio: cerrar el backend sin interceptor en el frontend deja la UI en 401 para todos.

Nota: el código corregido (esta rama) fue sometido además a dos revisiones adversariales independientes (backend y frontend) cuyas conclusiones se incorporaron en los últimos commits; ver el historial de git.

## Causas raíz

1. **Entidades JPA como contrato HTTP y getters falsos.** Los controladores recibían y devolvían las entidades. `Cliente.getCups()` devolvía la cadena "CUPS", `getCompaniaContratada()` null y la fecha de contrato siempre "hoy"; los campos del formulario se descartaban al deserializar. El núcleo del dominio (puntos de suministro) ni se guardaba ni se mostraba.
2. **Seguridad decorativa.** Todo `/api/v2/**` era `permitAll`, `@PreAuthorize` no tenía efecto sin `@EnableMethodSecurity`, el JWT nunca validaba (`Integer.equals(Long)`), CORS no estaba en la cadena de seguridad (los preflight recibían 401), convivían dos mecanismos JWT con claves distintas, las contraseñas se guardaban en claro y el frontend nunca enviaba el token.
3. **Configuración no parametrizada.** Datasource fijo a localhost, `ddl-auto=create` más un seeder que vaciaba las tablas en cada arranque, Dockerfiles con nombre incorrecto, `nginx.conf` nunca copiado y URLs de la API fijas en el código.
4. **Toolchain.** Lombok 1.18.22 fijado no compila con JDK 21; `@angular/material-moment-adapter@18` impedía `npm ci` con Angular 17.

## Hallazgos confirmados

### Backend: compilación y ejecución (29)

| Id | Severidad | Hallazgo | Fichero original | Estado |
|---|---|---|---|---|
| B-01 | Bloqueante | Lombok 1.18.22 pinned in dependencyManagement cannot run on the JDK 21 toolchain; Lombok is required to compile | `Backend/pom.xml:127` | Corregido |
| B-02 | Alta | isTokenValid compares Integer claim with Long id -&gt; always false, so JWT authentication can never succeed | `Backend/src/main/java/com/StrattonApp/Backend/service/impl/JwtServiceImpl.java:88` | Corregido |
| H-02 | Alta | PUT /api/v2/empleados/{id} orphan-deletes all of the employee&#x27;s clients (FK failure on suministros) or corrupts them | `Backend/src/main/java/com/StrattonApp/Backend/controller/EmpleadoController.java:126` | Corregido |
| H-03 | Alta | Employees created/updated through the API get a raw (unencoded or null) password and lose their roles, so they can never log in | `Backend/src/main/java/com/StrattonApp/Backend/controller/EmpleadoController.java:115` | Corregido |
| H-04 | Alta | Method security is not enabled and /api/v2/** is permitAll: every CRUD endpoint is open to anonymous callers; @PreAuthorize expressions are wrong anyway | `Backend/src/main/java/com/StrattonApp/Backend/config/SecurityConfig.java:71` | Corregido |
| H-05 | Alta | CUPS, comercializadora and fechaSubidaContrato are fabricated: Cliente has fake getters and ClienteDTO.getCompaniaContratada() always returns &#x27;No especificado&#x27; | `Backend/src/main/java/com/StrattonApp/Backend/entities/Cliente.java:259` | Corregido |
| H-06 | Alta | Deleting a client or an employee violates the suministros FK -&gt; 500 for all seeded data | `Backend/src/main/java/com/StrattonApp/Backend/service/impl/ClienteServiceImpl.java:112` | Corregido |
| H-07 | Alta | Datasource hard-coded to localhost; Docker env vars (BBDD_HOST, BBDD_NAME, DLL_AUTO...) are never read | `Backend/src/main/resources/application.properties:10` | Corregido |
| H-08 | Alta | ddl-auto=create plus unconditional limpiarTablas() wipe the whole database on every start | `Backend/src/main/java/com/StrattonApp/Backend/config/InicializarDatos.java:70` | Corregido |
| M-04 | Alta | Controllers bind JPA entities (Empleado, Cliente) directly as @RequestBody, producing a brittle contract that the frontend does not match | `Backend/src/main/java/com/StrattonApp/Backend/controller/ClienteController.java:66` | Corregido |
| B-04 | Media | POST /api/v2/empleados/{id}/clientes is a stub: returns 201 with empty body and never saves the client | `Backend/src/main/java/com/StrattonApp/Backend/controller/EmpleadoController.java:238` | Corregido |
| H-01 | Media | AdminController serializes the Empleado entity: getIdEmpleado() throws and the object graph recurses -&gt; 500, plus password hash exposure | `Backend/src/main/java/com/StrattonApp/Backend/controller/AdminController.java:45` | Corregido |
| M-01 | Media | JwtAuthenticationFilter lets jjwt/UserDetails exceptions escape -&gt; 500 instead of 401 for expired or tampered tokens | `Backend/src/main/java/com/StrattonApp/Backend/config/JwtAuthenticationFilter.java:60` | Corregido |
| M-03 | Media | Authentication errors are mapped to 404/500: bad credentials -&gt; GlobalException (@ResponseStatus NOT_FOUND), duplicate email -&gt; IllegalArgumentException 500 | `Backend/src/main/java/com/StrattonApp/Backend/service/impl/AuthenticationServiceImpl.java:101` | Corregido |
| M-06 | Media | actualizarCliente overwrites comercializadora/dni/email/iban with whatever the payload has (null from the UI) | `Backend/src/main/java/com/StrattonApp/Backend/service/impl/ClienteServiceImpl.java:96` | Corregido |
| M-07 | Media | CORS origin configuration is inconsistent and &#x27;http://localhost:80&#x27; can never match a browser Origin | `Backend/src/main/java/com/StrattonApp/Backend/controller/AuthenticationController.java:20` | Corregido |
| M-08 | Media | Mixed javax/jakarta Bean Validation stack: constraints on entities are never enforced | `Backend/pom.xml:84` | Corregido |
| M-13 | Media | Role resolution uses iterator().next() on a HashSet: crashes for role-less users and is non-deterministic for multi-role users | `Backend/src/main/java/com/StrattonApp/Backend/service/impl/AuthenticationServiceImpl.java:93` | Corregido |
| L-01 | Baja | Thymeleaf templates reference MVC controllers, model attributes and properties that do not exist (dead UI + unused starters) | `Backend/src/main/resources/templates/empleados.html:26` | Corregido |
| L-02 | Baja | @Transactional on an entity method has no effect | `Backend/src/main/java/com/StrattonApp/Backend/entities/Empleado.java:69` | Corregido |
| L-03 | Baja | Seeder runs MySQL-only ALTER TABLE AUTO_INCREMENT statements (no-op for the String-keyed suministros) and picks the wrong comercializadora | `Backend/src/main/java/com/StrattonApp/Backend/config/InicializarDatos.java:73` | Corregido |
| L-04 | Baja | The only test is a @SpringBootTest that needs a live MySQL, so `mvn package` fails without -DskipTests | `Backend/src/test/java/com/StrattonApp/Backend/BackendApplicationTests.java:6` | Corregido |
| L-05 | Baja | Stale/ineffective configuration keys in application.properties | `Backend/src/main/resources/application.properties:22` | Corregido |
| M-02 | Baja | Second, conflicting JWT stack (RequestFilter + TokenUtils) with a different hard-coded secret is auto-registered as a servlet filter | `Backend/src/main/java/com/StrattonApp/Backend/config/RequestFilter.java:44` | Corregido |
| M-05 | Baja | ClienteRepository.findByCups declares List&lt;ClienteDTO&gt; for an entity-select JPQL -&gt; fails at call time | `Backend/src/main/java/com/StrattonApp/Backend/repository/ClienteRepository.java:19` | Corregido |
| M-09 | Baja | jjwt version pinned twice (0.11.5 in dependencyManagement vs 0.12.5 in dependencies) and code relies on deprecated 0.11-era API | `Backend/pom.xml:132` | Corregido |
| M-10 | Baja | Cliente.telefono is an Integer: phone numbers with leading zeros/prefixes are impossible and large values overflow | `Backend/src/main/java/com/StrattonApp/Backend/entities/Cliente.java:45` | Corregido |
| M-11 | Baja | GET /api/v2/empleados/perfil is permitAll and dereferences a null Authentication -&gt; 500 for anonymous callers | `Backend/src/main/java/com/StrattonApp/Backend/controller/EmpleadoController.java:172` | Corregido |
| M-12 | Baja | Remaining entity responses can recurse infinitely in Jackson (Cliente/Suministro/Comercializadora graph has no @JsonIgnore) | `Backend/src/main/java/com/StrattonApp/Backend/DTO/SuministroDTO.java:16` | Corregido |

### Backend: seguridad y autenticación (22)

| Id | Severidad | Hallazgo | Fichero original | Estado |
|---|---|---|---|---|
| SEC-01 | Alta | isTokenValid compara Integer con Long y siempre devuelve false: ningún JWT autentica nunca | `Backend/src/main/java/com/StrattonApp/Backend/service/impl/JwtServiceImpl.java:88` | Corregido |
| SEC-02 | Alta | CORS no integrado en Spring Security: los preflight OPTIONS reciben 401 y el navegador bloquea toda la API | `Backend/src/main/java/com/StrattonApp/Backend/config/SecurityConfig.java:123` | Corregido |
| SEC-03 | Alta | Autorización inexistente: todo /api/v2/** es permitAll y los @PreAuthorize se ignoran por falta de @EnableMethodSecurity | `Backend/src/main/java/com/StrattonApp/Backend/config/SecurityConfig.java:71` | Corregido |
| SEC-04 | Alta | POST/PUT /api/v2/empleados guardan la entidad tal cual: password sin cifrar y roles controlados por el cliente (escalada de privilegios) | `Backend/src/main/java/com/StrattonApp/Backend/controller/EmpleadoController.java:115` | Corregido |
| SEC-05 | Alta | DELETE /api/v2/empleados/** y /api/v2/clientes/** permitidos sin token (borrado destructivo anónimo) | `Backend/src/main/java/com/StrattonApp/Backend/config/SecurityConfig.java:74` | Corregido |
| SEC-09 | Alta | InicializarDatos borra todas las tablas y resiembra admin/admin y usuario/usuario en CADA arranque, sin @Profile | `Backend/src/main/java/com/StrattonApp/Backend/config/InicializarDatos.java:70` | Corregido |
| SEC-10 | Alta | Datos personales de todos los clientes (DNI, IBAN, teléfono, email, dirección) expuestos sin autenticación | `Backend/src/main/java/com/StrattonApp/Backend/controller/ClienteController.java:30` | Corregido |
| SEC-12 | Alta | Registro público (/api/v1/auth/register) crea empleados sin control; signup ignora nombre/apellidos/rol y carece de validaciones | `Backend/src/main/java/com/StrattonApp/Backend/service/impl/AuthenticationServiceImpl.java:55` | Corregido: el registro valida nombre/apellidos/email; REGISTRO_ABIERTO=false lo desactiva |
| SEC-06 | Media | hasRole(&#x27;ADMIN&#x27;) vs authority &#x27;ADMIN&#x27; (sin prefijo ROLE_) y SpEL hasAuthority(Role.ADMIN.toString()) irresoluble | `Backend/src/main/java/com/StrattonApp/Backend/controller/EmpleadoController.java:60` | Corregido |
| SEC-07 | Media | Dos mecanismos JWT con secretos distintos; RequestFilter auto-registrado loguea ERROR por petición y permite suplantación con la clave hardcodeada | `Backend/src/main/java/com/StrattonApp/Backend/config/RequestFilter.java:44` | Corregido |
| SEC-08 | Media | Secretos JWT versionados en el repositorio (application.properties y constante hardcodeada), presentes en el historial git | `Backend/src/main/resources/application.properties:4` | Corregido |
| SEC-11 | Media | AdminController devuelve la entidad Empleado (hash de password, authorities) y además responde 500 siempre; POST/PUT/DELETE /api/v1/admin/** accesibles a cualquier usuario autenticado | `Backend/src/main/java/com/StrattonApp/Backend/controller/AdminController.java:44` | Corregido |
| SEC-13 | Media | signin captura Exception y responde 404 (GlobalException @ResponseStatus NOT_FOUND) ante credenciales incorrectas y ante cualquier error interno | `Backend/src/main/java/com/StrattonApp/Backend/service/impl/AuthenticationServiceImpl.java:99` | Corregido |
| SEC-15 | Media | El frontend nunca envía el JWT (sin interceptor) y toda la &#x27;autorización&#x27; es client-side a partir de un claim sin verificar | `Frontend/src/app/app.config.ts:13` | Corregido |
| SEC-16 | Media | GET /api/v2/empleados/perfil es permitAll y dereferencia Authentication → NPE 500 para anónimos | `Backend/src/main/java/com/StrattonApp/Backend/controller/EmpleadoController.java:172` | Corregido |
| SEC-17 | Media | Mass assignment: entidades JPA (Cliente, Empleado) como @RequestBody permiten fijar id, empleado, asesoria, suministros y roles desde el cliente | `Backend/src/main/java/com/StrattonApp/Backend/controller/ClienteController.java:66` | Corregido |
| SEC-18 | Media | La asignación de roles desde el frontend se descarta (clave JSON &#x27;role&#x27; vs setter setRoles) y el &#x27;rol principal&#x27; se elige de un HashSet: todos acaban como USER | `Backend/src/main/java/com/StrattonApp/Backend/entities/Empleado.java:175` | Corregido |
| SEC-19 | Media | MySQL root sin contraseña expuesto en el puerto 3306 del host; credenciales de BD y URL hardcodeadas e incoherentes con Docker | `Docker-compose.yml:8` | Corregido: contraseña obligatoria en compose; puerto 3306 configurable (DB_PORT) |
| SEC-14 | Baja | JwtAuthenticationFilter no captura excepciones de jjwt: tokens expirados/malformados/firma inválida producen 500; además está doblemente registrado | `Backend/src/main/java/com/StrattonApp/Backend/config/JwtAuthenticationFilter.java:60` | Corregido |
| SEC-20 | Baja | Logs con SQL y parámetros (hashes, DNI, IBAN) activados; DevTools en el pom | `Backend/src/main/resources/application.properties:18` | Corregido |
| SEC-21 | Baja | Código y dependencias de seguridad muertos o duplicados que aumentan la confusión y la superficie de ataque | `Backend/src/main/java/com/StrattonApp/Backend/utils/JwtUtils.java:15` | Corregido |
| SEC-22 | Baja | Ciclo de vida del token débil: 10 h sin refresh ni revocación, guardado en localStorage, sin iss/aud | `Backend/src/main/java/com/StrattonApp/Backend/service/impl/JwtServiceImpl.java:108` | Parcial: expiración configurable (JWT_EXPIRATION_MS); sin refresh ni revocación |

### Backend: persistencia y datos (42)

| Id | Severidad | Hallazgo | Fichero original | Estado |
|---|---|---|---|---|
| PERS-01 | Alta | El seeder borra todas las tablas en cada arranque y ddl-auto=create recrea el esquema: ningún dato sobrevive a un reinicio | `Backend/src/main/java/com/StrattonApp/Backend/config/InicializarDatos.java:70` | Corregido |
| PERS-02 | Alta | PUT /api/v2/empleados/{id} vacía la colección `clientes` (orphanRemoval), anula password, roles y asesoría del empleado | `Backend/src/main/java/com/StrattonApp/Backend/controller/EmpleadoController.java:120` | Corregido |
| PERS-03 | Alta | Borrar un Cliente (o un Empleado, por cascade) falla con violación de FK porque Cliente.suministros no tiene cascade/orphanRemoval | `Backend/src/main/java/com/StrattonApp/Backend/entities/Cliente.java:57` | Corregido |
| PERS-05 | Alta | cups, companiaContratada y fechaSubidaContrato de Cliente son getters falsos: el formulario los envía y el backend los descarta | `Backend/src/main/java/com/StrattonApp/Backend/entities/Cliente.java:259` | Corregido |
| PERS-09 | Alta | Empleados creados por API se guardan con la contraseña en claro y el POST permite sobrescribir cualquier empleado por id | `Backend/src/main/java/com/StrattonApp/Backend/controller/EmpleadoController.java:72` | Corregido |
| PERS-10 | Alta | Datasource fijado a localhost: las variables BBDD_HOST/BBDD_NAME/DATABASE_USERNAME/DATABASE_PASSWORD/DLL_AUTO de Docker se ignoran y el backend en compose no arranca | `Backend/src/main/resources/application.properties:9` | Corregido |
| PERS-11 | Alta | No existen endpoints de Suministro ni Comercializadora y ClienteDTO no expone suministros: el frontend nunca puede ver ni gestionar CUPS/estado | `Backend/src/main/java/com/StrattonApp/Backend/controller/EmpleadoController.java:203` | Corregido |
| PERS-12 | Alta | lombok 1.18.22 fijado en dependencyManagement no soporta JDK 21: la compilación falla en este entorno (afecta a Comercializadora, ClienteDTO, AuthenticationServiceImpl, AuthenticationController) | `Backend/pom.xml:124` | Corregido |
| PERS-04 | Media | ClienteDTO.getCompaniaContratada() ignora el campo y devuelve siempre &quot;No especificado&quot; | `Backend/src/main/java/com/StrattonApp/Backend/DTO/ClienteDTO.java:121` | Corregido |
| PERS-06 | Media | Empleado.getIdEmpleado() lanza UnsupportedOperationException: serializar la entidad Empleado devuelve 500 y además expone el hash de la contraseña | `Backend/src/main/java/com/StrattonApp/Backend/entities/Empleado.java:170` | Corregido |
| PERS-07 | Media | Relaciones bidireccionales sin @JsonIgnore: recursión infinita al serializar Empleado/Cliente/Suministro/Comercializadora | `Backend/src/main/java/com/StrattonApp/Backend/entities/Empleado.java:65` | Corregido |
| PERS-08 | Media | Bean Validation está desactivado silenciosamente: pila javax (hibernate-validator 6.2) con API jakarta 3.0 → ninguna @NotBlank/@Email/@Size se evalúa | `Backend/pom.xml:81` | Corregido |
| PERS-13 | Media | PUT /api/v2/clientes/{id} pone comercializadora=NULL en cada edición (el frontend envía companiaContratada string, no el objeto) | `Backend/src/main/java/com/StrattonApp/Backend/service/impl/ClienteServiceImpl.java:96` | Corregido |
| PERS-14 | Media | Cliente.telefono es Integer: las semillas generan números de 10 dígitos inválidos, no caben prefijos/ceros y no hay unique (RF-3, RN-2) | `Backend/src/main/java/com/StrattonApp/Backend/entities/Cliente.java:43` | Corregido |
| PERS-15 | Media | Sin restricciones de unicidad ni validación de formato en clientes.dni / email / iban (el DDL original sí las tenía) | `Backend/src/main/java/com/StrattonApp/Backend/entities/Cliente.java:31` | Corregido |
| PERS-18 | Media | Empleado.clientes con cascade=ALL + orphanRemoval: cualquier manipulación del set borra clientes y el borrado de empleado arrastra la cartera (bloqueado hoy por FK) | `Backend/src/main/java/com/StrattonApp/Backend/entities/Empleado.java:65` | Corregido |
| PERS-19 | Media | POST /api/v2/empleados/{id}/clientes responde 201 sin persistir nada (stub devuelve null) y la variante con DTO se copia a sí misma | `Backend/src/main/java/com/StrattonApp/Backend/controller/EmpleadoController.java:229` | Corregido |
| PERS-20 | Media | Los endpoints de creación aceptan la entidad con id: `save()` hace merge y sobrescribe clientes existentes; asesoria/comercializadora se toman del JSON crudo | `Backend/src/main/java/com/StrattonApp/Backend/service/impl/ClienteServiceImpl.java:70` | Corregido |
| PERS-21 | Media | Registrar un cliente sin IBAN (campo opcional en el formulario) provoca 500 por NOT NULL en vez de 400 | `Backend/src/main/java/com/StrattonApp/Backend/entities/Cliente.java:47` | Corregido |
| PERS-25 | Media | Credenciales de demo hardcodeadas (admin/admin, usuario/usuario, root sin contraseña) que se recrean en cualquier entorno | `Backend/src/main/java/com/StrattonApp/Backend/config/InicializarDatos.java:105` | Se mantienen como datos de ejemplo; SEED_DATA=false los desactiva |
| PERS-16 | Baja | Suministro.cups length=20 rechaza CUPS reales de 22 caracteres y las semillas usan &#x27;CUPS-1-0&#x27; sin formato | `Backend/src/main/java/com/StrattonApp/Backend/entities/Suministro.java:10` | Corregido |
| PERS-17 | Baja | Comercializadora modelada dos veces (Cliente.comercializadora y Suministro.comercializadora) con semillas contradictorias; `plan` cuelga de la compañía y no del contrato | `Backend/src/main/java/com/StrattonApp/Backend/config/InicializarDatos.java:209` | Decisión: Cliente.comercializadora se mantiene como &quot;principal&quot;, sincronizada con el primer suministro |
| PERS-22 | Baja | ClienteRepository.findByCups declara List&lt;ClienteDTO&gt; pero el JPQL selecciona la entidad Cliente: falla al invocarse | `Backend/src/main/java/com/StrattonApp/Backend/repository/ClienteRepository.java:18` | Corregido |
| PERS-23 | Baja | Si se elimina limpiarTablas(), el seeder referencia asesoria1/asesoria2 sin persistir y rompe el arranque (TransientPropertyValueException) | `Backend/src/main/java/com/StrattonApp/Backend/config/InicializarDatos.java:112` | Corregido |
| PERS-24 | Baja | Semillas incoherentes con RN-6: todos los clientes en asesoría 1 aunque &#x27;usuario&#x27; pertenece a asesoría 2 | `Backend/src/main/java/com/StrattonApp/Backend/config/InicializarDatos.java:163` | Corregido |
| PERS-26 | Baja | spring.profiles.active=demo sin application-demo.properties y sin que nada dependa del perfil | `Backend/src/main/resources/application.properties:2` | Corregido |
| PERS-27 | Baja | Comercializadora usa @Data en una entidad JPA: equals/hashCode/toString incluyen la colección lazy `suministros` | `Backend/src/main/java/com/StrattonApp/Backend/entities/Comercializadora.java:120` | Corregido |
| PERS-28 | Baja | Suministro con id String asignado se guarda vía merge: SELECT extra por fila y upsert silencioso de CUPS duplicados | `Backend/src/main/java/com/StrattonApp/Backend/entities/Suministro.java:10` | Corregido |
| PERS-29 | Baja | README: las tablas de entidades se contradicen entre sí y con el código (dos versiones distintas en el mismo fichero) | `README.md:178` | Corregido |
| PERS-30 | Baja | docs/ddl es el esquema del proyecto de BBDD del curso anterior: incompatible con el esquema que genera Hibernate y no cargable en la app | `docs/ddl/strattonapp3.0.sql:5` | Documentado: docs/ddl es el esquema histórico del proyecto de BBDD |
| PERS-31 | Baja | Driver mysql-connector-java 8.0.23 (artefacto relocalizado, con CVEs) en lugar del com.mysql:mysql-connector-j gestionado por Boot; compose usa mysql:latest | `Backend/pom.xml:25` | Corregido |
| PERS-32 | Baja | Propiedades de Hibernate obsoletas: MySQL8Dialect deprecado, logger BasicBinder de Hibernate 5 sin efecto, show-sql siempre activo | `Backend/src/main/resources/application.properties:19` | Corregido |
| PERS-33 | Baja | ALTER TABLE ... AUTO_INCREMENT = 1 en el seeder: inútil con ddl-auto=create, sin efecto en `suministros` (PK VARCHAR) y sintaxis sólo MySQL | `Backend/src/main/java/com/StrattonApp/Backend/config/InicializarDatos.java:71` | Corregido |
| PERS-34 | Baja | Dependencias y código muertos: javafaker (snakeyaml 1.x), Thymeleaf + plantillas sin controlador, generarCUPS, getters de repositorio en el controlador | `Backend/pom.xml:115` | Corregido |
| PERS-35 | Baja | jjwt declarado 0.12.5 en dependencies y 0.11.5 en dependencyManagement: la gestión es inerte y engañosa | `Backend/pom.xml:129` | Corregido |
| PERS-36 | Baja | BackendApplicationTests arranca el contexto completo contra MySQL en localhost y ejecuta el seeder destructivo | `Backend/src/test/java/com/StrattonApp/Backend/BackendApplicationTests.java:6` | Corregido |
| PERS-37 | Baja | Nombres de columna inconsistentes (@Column &quot;Nombre&quot;/&quot;Usuario&quot;/&quot;correo&quot;/&quot;RolesUsuario&quot;, @JoinColumn &quot;empleado&quot;) que la estrategia de nombrado de Boot reescribe en snake_case | `Backend/src/main/java/com/StrattonApp/Backend/entities/Empleado.java:34` | Corregido |
| PERS-38 | Baja | Contradicciones @NotBlank vs nullable=true en Asesoria.direccion, Empleado.nombre/apellidos y Cliente.direccion; Empleado.password sin restricción de columna | `Backend/src/main/java/com/StrattonApp/Backend/entities/Asesoria.java:22` | Corregido |
| PERS-39 | Baja | signin y getMainRole asumen que el Set de roles no está vacío: NoSuchElementException enmascarada como &#x27;Contraseña no válida&#x27; (404) | `Backend/src/main/java/com/StrattonApp/Backend/service/impl/AuthenticationServiceImpl.java:93` | Corregido |
| PERS-40 | Baja | Ausencia total de @Transactional en servicios y seeder; @Transactional de jakarta.transaction en un getter de entidad; dependencia implícita de open-in-view | `Backend/src/main/java/com/StrattonApp/Backend/entities/Empleado.java:69` | Corregido |
| PERS-41 | Baja | Seeder: generarTelefono/generarIBAN producen valores inválidos (RN-2/RN-7) y la lógica de &#x27;Suministro&#x27; ignora Estado distinto de ACTIVO | `Backend/src/main/java/com/StrattonApp/Backend/config/InicializarDatos.java:257` | Corregido |
| PERS-42 | Baja | Volumen de compose ./Backend/src/main/resources:/app/resources no tiene efecto: Spring Boot lee la configuración embebida en el jar | `Docker-compose.yml:31` | Corregido |

### Contrato API frontend ↔ backend (20)

| Id | Severidad | Hallazgo | Fichero original | Estado |
|---|---|---|---|---|
| API-01 | Bloqueante | CORS preflight always 401: security chain has no cors() and OPTIONS is not permitted | `Backend/src/main/java/com/StrattonApp/Backend/config/SecurityConfig.java:62` | Corregido |
| API-02 | Alta | Allowed origins do not match the Docker deployment (http://localhost) nor each other | `Backend/src/main/java/com/StrattonApp/Backend/config/SecurityConfig.java:127` | Corregido |
| API-03 | Alta | JwtServiceImpl.isTokenValid is always false (Integer.equals(Long)) so no request is ever authenticated | `Backend/src/main/java/com/StrattonApp/Backend/service/impl/JwtServiceImpl.java:88` | Corregido |
| API-04 | Alta | API is wide open: all matchers permitAll, @PreAuthorize inert (no @EnableMethodSecurity), invalid SpEL and missing ROLE_ prefix | `Backend/src/main/java/com/StrattonApp/Backend/config/SecurityConfig.java:71` | Corregido |
| API-05 | Alta | Frontend never sends the JWT: no HTTP interceptor, no route guards | `Frontend/src/app/app.config.ts:13` | Corregido |
| API-06 | Alta | Client CUPS, comercializadora, fecha and logo are silently dropped (getter-only properties on the JPA entity) | `Backend/src/main/java/com/StrattonApp/Backend/entities/Cliente.java:269` | Corregido |
| API-07 | Alta | ClienteDTO.getCompaniaContratada() always returns &quot;No especificado&quot; | `Backend/src/main/java/com/StrattonApp/Backend/DTO/ClienteDTO.java:121` | Corregido |
| API-08 | Alta | PUT /api/v2/clientes/{id} wipes the client&#x27;s comercializadora on every edit | `Backend/src/main/java/com/StrattonApp/Backend/service/impl/ClienteServiceImpl.java:96` | Corregido |
| API-09 | Alta | POST /api/v2/empleados stores the password in plaintext and ignores the roles array (JSON property is &#x27;roles&#x27;, not &#x27;role&#x27;) | `Backend/src/main/java/com/StrattonApp/Backend/controller/EmpleadoController.java:76` | Corregido |
| API-10 | Alta | PUT /api/v2/empleados/{id} from the edit form nulls password/roles/asesoria and corrupts the client set | `Backend/src/main/java/com/StrattonApp/Backend/controller/EmpleadoController.java:115` | Corregido |
| API-11 | Alta | DELETE client/employee returns 500: suministros FK is not cascaded | `Backend/src/main/java/com/StrattonApp/Backend/entities/Cliente.java:57` | Corregido |
| API-12 | Media | /api/v1/auth/register drops nombre/apellidos/email and fails with 500 from the second registration on | `Frontend/src/app/auth.service.ts:19` | Corregido |
| API-13 | Media | Wrong HTTP status contract: bad login -&gt; 404, validation -&gt; 500, expired JWT -&gt; 500 | `Backend/src/main/java/com/StrattonApp/Backend/exceptions/GlobalException.java:9` | Corregido |
| API-15 | Media | EmpleadoDTO shape differs from the frontend Empleado model (role string vs string[], no asesoria/foto, &#x27;apellido&#x27;) | `Backend/src/main/java/com/StrattonApp/Backend/DTO/EmpleadoDTO.java:21` | Corregido |
| API-18 | Media | Role claim is a single string; frontend role check is a substring test and MANAGER is unhandled | `Frontend/src/app/auth.service.ts:58` | Corregido |
| API-14 | Baja | ClienteService.registrarCliente posts a Cliente to /api/v2/empleados; suministros endpoint field names differ | `Frontend/src/app/pages/clientes/cliente.service.ts:22` | Corregido |
| API-16 | Baja | Dead second JWT implementation with a hardcoded secret is auto-registered as a servlet filter | `Backend/src/main/java/com/StrattonApp/Backend/utils/TokenUtils.java:32` | Corregido |
| API-17 | Baja | Endpoints that serialize JPA entities or save nothing: AdminController and EmpleadoController.agregarClienteAEmpleado | `Backend/src/main/java/com/StrattonApp/Backend/controller/AdminController.java:44` | Corregido |
| API-19 | Baja | List components spread undefined before the HTTP response arrives | `Frontend/src/app/pages/clientes/lista-clientes/lista-clientes.component.ts:62` | Corregido |
| API-20 | Baja | telefono modelled as Integer: overflow/format loss and no-op validators | `Backend/src/main/java/com/StrattonApp/Backend/entities/Cliente.java:45` | Corregido |

### Frontend Angular (31)

| Id | Severidad | Hallazgo | Fichero original | Estado |
|---|---|---|---|---|
| F01 | Alta | Sin interceptor HTTP: ninguna llamada al backend envía el JWT | `Frontend/src/app/app.config.ts:13` | Corregido |
| F03 | Alta | URL de API hardcodeada a localhost:8080 sin environments; CORS del backend sólo permite :4200 | `Frontend/src/app/auth.service.ts:9` | Corregido |
| F04 | Alta | DockerFIle: `npm install sweetalert2 --force` instala la v11, incompatible con el código (API v7) | `Frontend/DockerFIle:12` | Corregido |
| F05 | Alta | Dockerfile mal nombrado para compose y nginx.conf nunca copiado (404 en deep links) | `Frontend/DockerFIle:21` | Corregido |
| F06 | Alta | Actualizar empleado reenvía el EmpleadoDTO completo: el backend borra password y roles y toca la colección de clientes | `Frontend/src/app/pages/empleados/actualizar-empleado/actualizar-empleado.component.ts:43` | Corregido |
| F07 | Alta | Registrar empleado envía `role` como array y password en claro: el empleado creado no tiene roles y no puede hacer login | `Frontend/src/app/pages/empleados/registrar-empleado/registrar-empleado.component.html:92` | Corregido |
| F08 | Alta | El formulario de cliente envía CUPS/comercializadora que el backend descarta: el núcleo del dominio no persiste | `Frontend/src/app/pages/clientes/registrar-cliente/registrar-cliente.component.html:88` | Corregido |
| F02 | Media | Rutas sin guards: cualquiera puede abrir /empleados, /registrar-empleado, etc. | `Frontend/src/app/app.routes.ts:16` | Corregido |
| F09 | Media | Bootstrap 4.0.0 CSS desde CDN + Bootstrap 5 JS empaquetado + CSS 5 ausente: la UI queda entre dos versiones | `Frontend/src/index.html:10` | Corregido |
| F14 | Media | Listas: spread de `this.clientes`/`this.empleados` antes de que responda el HTTP → TypeError en ngOnInit | `Frontend/src/app/pages/clientes/lista-clientes/lista-clientes.component.ts:62` | Corregido |
| F15 | Media | `clienteForm.controls[&#x27;nombre&#x27;].invalid` sin `?.`: TypeError en el primer ciclo de detección de cambios | `Frontend/src/app/pages/clientes/registrar-cliente/registrar-cliente.component.html:16` | Corregido |
| F19 | Media | Registro sólo envía username+password: nombre, apellidos y email se descartan y los mensajes son de login | `Frontend/src/app/pages/register/register.component.ts:28` | Corregido |
| F26 | Media | Dependencias rotas/innecesarias: material-moment-adapter 18 con Angular 17 (ERESOLVE), material/cdk/moment/gsap/ngx-toastr sin uso | `Frontend/package.json:20` | Corregido |
| F10 | Baja | script.js empaquetado como script global llama a gsap/MorphSVGPlugin inexistentes: ReferenceError en cada carga | `Frontend/angular.json:35` | Corregido |
| F11 | Baja | assets/js/main.js usa `import` en un script clásico (SyntaxError) y, si funcionara, dereferencia elementos inexistentes | `Frontend/src/assets/js/main.js:4` | Corregido |
| F12 | Baja | CDN gsap mal escrito (unpkg.co) y tres scripts externos sin uso ni SRI | `Frontend/src/index.html:102` | Corregido |
| F13 | Baja | Footer duplicado: uno hardcodeado en index.html y otro en &lt;app-footer&gt; | `Frontend/src/index.html:81` | Corregido |
| F16 | Baja | registrar-cliente/actualizar-cliente terminan con `&lt;form&gt;` en vez de `&lt;/form&gt;`; 4 contenedores sin cerrar | `Frontend/src/app/pages/clientes/registrar-cliente/registrar-cliente.component.html:177` | Corregido |
| F17 | Baja | registrar-empleado: form-groups anidados por cierres mal colocados y typo `controls[&#x27;roles&#x27;]` | `Frontend/src/app/pages/empleados/registrar-empleado/registrar-empleado.component.html:35` | Corregido |
| F20 | Baja | Login: navegación por `response.role` sólo contempla ADMIN/USER y getUserRoles devuelve un string tipado como array | `Frontend/src/app/pages/login/login.component.ts:41` | Corregido |
| F21 | Baja | PerfilEmpleadoComponent inalcanzable (sin ruta) y usa `empleado.apellido` en vez de `apellidos` | `Frontend/src/app/pages/empleados/perfil-empleado/perfil-empleado.component.html:4` | Corregido |
| F22 | Baja | Columna &#x27;Asesor&#x27; hardcodeada a &#x27;alvrich&#x27; y ordenación/filtro de clientes incoherentes | `Frontend/src/app/pages/clientes/lista-clientes/lista-clientes.component.html:52` | Corregido |
| F23 | Baja | lista-empleados filtra destruyendo la lista fuente y refetcha en cada borrado del texto | `Frontend/src/app/pages/empleados/lista-empleados/lista-empleados.component.ts:86` | Corregido |
| F24 | Baja | Modelo Empleado desalineado con el DTO: `foto` es un tipo literal, no un valor; role/asesoria/clientes con tipos incorrectos | `Frontend/src/app/pages/empleados/empleado.ts:13` | Corregido |
| F25 | Baja | Inicio: llamada http a api.zippopotam.us (mixed content) y `this.google.maps` undefined → TypeError | `Frontend/src/app/pages/inicio/inicio.component.ts:62` | Corregido |
| F27 | Baja | ToastrService registrado como provider sin provideToastr(); provideAnimationsAsync duplicado; CargarScriptsService redundante | `Frontend/src/app/app.config.ts:13` | Corregido |
| F28 | Baja | angular.json test: referencia node_modules/jquery (no instalado) y main.js roto → `ng test` no arranca | `Frontend/angular.json:97` | Corregido |
| F29 | Baja | routerLinkActive sin importar RouterLinkActive (no-op) y logo con href=&quot;#&quot; que redirige a /login | `Frontend/src/app/layout/header/header.component.ts:11` | Corregido |
| F30 | Baja | Ficheros y código muerto: contrast.scss, search.html, search.pipe.ts, cargar-scripts.service.ts, imports sobrantes | `Frontend/src/app/layout/contrast.scss:1` | Corregido |
| F31 | Baja | Sin manejo de errores en borrar/actualizar y SweetAlert intrusivo en detalles de empleado | `Frontend/src/app/pages/clientes/lista-clientes/lista-clientes.component.ts:105` | Corregido |
| F32 | Baja | tsconfig: opciones strict colocadas en angularCompilerOptions (ignoradas) y strict=false global | `Frontend/tsconfig.json:31` | Corregido: opciones movidas; strict sigue en false (migración progresiva pendiente) |

### Construcción y despliegue (22)

| Id | Severidad | Hallazgo | Fichero original | Estado |
|---|---|---|---|---|
| BD-01 | Bloqueante | Compose references &#x27;Dockerfile&#x27; but files are &#x27;DockerFile&#x27; / &#x27;DockerFIle&#x27; (case mismatch) | `Docker-compose.yml:18` | Corregido |
| BD-03 | Bloqueante | Backend datasource URL hardcoded to localhost; compose env vars (BBDD_HOST etc.) are never read | `Backend/src/main/resources/application.properties:10` | Corregido |
| BD-06 | Alta | Frontend hardcodes http://localhost:8080, nginx does not proxy /api, and backend CORS never allows the port-80 origin | `Frontend/src/app/auth.service.ts:9` | Corregido |
| BD-07 | Alta | Committed secrets: jwt.secret, second hardcoded HMAC key in TokenUtils, MySQL root with empty password published on host port 3306 | `Backend/src/main/resources/application.properties:4` | Corregido |
| BD-08 | Alta | Lombok pinned to 1.18.22 breaks compilation on JDK 21 (sandbox toolchain) | `Backend/pom.xml:127` | Corregido |
| BD-12 | Alta | ddl-auto=create plus seeder deleteAll() wipes the database on every backend start; db_data volume is pointless | `Backend/src/main/resources/application.properties:17` | Corregido |
| BD-02 | Media | Compose file &#x27;Docker-compose.yml&#x27; is not auto-discovered by docker compose | `Docker-compose.yml:1` | Corregido |
| BD-04 | Media | No MySQL healthcheck and short-form depends_on: backend starts before the DB accepts connections | `Docker-compose.yml:33` | Corregido |
| BD-05 | Media | Frontend image never copies nginx.conf: no SPA fallback, deep links/refresh return 404 | `Frontend/DockerFIle:24` | Corregido |
| BD-14 | Media | pom.xml dependency hygiene: EOL MySQL driver coordinates, duplicated jjwt versions, javax+jakarta validation stacks, unused starters, milestone repos | `Backend/pom.xml:26` | Corregido |
| BD-17 | Media | index.html: stale styles.css link, Bootstrap 4 CDN CSS vs Bootstrap 5 JS, GSAP from &#x27;unpkg.co&#x27; typo, main.js loaded as classic script with ES import | `Frontend/src/index.html:9` | Corregido |
| BD-18 | Media | README has no run/deploy instructions; PDF deployment manual is wrong; no CI workflow, .env, or root .gitignore | `README.md:268` | Corregido: README con despliegue, .env.example, .gitignore y CI en GitHub Actions |
| BD-09 | Baja | npm install --force masks an unused @angular/material-moment-adapter@18 peer conflict; redundant second npm install | `Frontend/DockerFIle:11` | Corregido |
| BD-10 | Baja | &#x27;npm run build --prod&#x27; passes --prod to npm, not to ng build | `Frontend/DockerFIle:18` | Corregido |
| BD-11 | Baja | Deprecated/unmaintained base images and no dependency-layer caching in backend Dockerfile | `Backend/DockerFile:2` | Corregido |
| BD-13 | Baja | mysql:latest unpinned, deprecated MySQL8Dialect, obsolete compose version, dead resources volume | `Docker-compose.yml:4` | Corregido |
| BD-15 | Baja | Backend/mvnw is committed without the executable bit | `Backend/mvnw:1` | Corregido |
| BD-16 | Baja | ng test references node_modules/jquery (not installed) and a legacy script with an ES import | `Frontend/angular.json:95` | Corregido |
| BD-19 | Baja | @SpringBootTest contextLoads needs a live MySQL: mvn package/verify fails without -DskipTests | `Backend/src/test/java/com/StrattonApp/Backend/BackendApplicationTests.java:7` | Corregido |
| BD-20 | Baja | Production-unfriendly logging/profile settings and a no-op TRACE logger | `Backend/src/main/resources/application.properties:18` | Corregido |
| BD-21 | Baja | Frontend budgets/assets: footer CSS trips the 2 kB warning, initial bundle over 500 kB warning, duplicated assets entry, 12 MB video shipped in every image | `Frontend/angular.json:47` | Corregido |
| BD-22 | Baja | javadoc.css misplaced at repo root; generated javadoc committed under Backend/doc | `javadoc.css:1` | Corregido: javadoc.css movido a Backend/; el javadoc generado se mantiene como entregable |

### Funcionalidad frente al README (15)

| Id | Severidad | Hallazgo | Fichero original | Estado |
|---|---|---|---|---|
| UXF-01 | Alta | Flujo 3: crear empleado guarda la contraseña en claro y pierde los roles; el nuevo empleado no puede iniciar sesión | `Backend/src/main/java/com/StrattonApp/Backend/controller/EmpleadoController.java:76` | Corregido |
| UXF-02 | Alta | Flujo 4: al registrar un cliente se descartan CUPS, comercializadora y fecha; no se crea ningún Suministro | `Backend/src/main/java/com/StrattonApp/Backend/service/impl/ClienteServiceImpl.java:139` | Corregido |
| UXF-03 | Alta | Flujo 2: la lista de clientes muestra datos inventados (asesor &#x27;alvrich&#x27;, comercializadora &#x27;No especificado&#x27;, fecha = hoy) | `Frontend/src/app/pages/clientes/lista-clientes/lista-clientes.component.html:52` | Corregido |
| UXF-04 | Alta | Flujo 6: borrar un cliente (o un empleado) con suministros devuelve 500 por FK y la UI no avisa | `Backend/src/main/java/com/StrattonApp/Backend/entities/Cliente.java:57` | Corregido |
| UXF-05 | Alta | Flujo 5: editar cliente pone la comercializadora a NULL y no permite editar CUPS ni fecha | `Backend/src/main/java/com/StrattonApp/Backend/service/impl/ClienteServiceImpl.java:96` | Corregido |
| UXF-06 | Alta | Actualizar empleado es destructivo: borra contraseña, roles, asesoría y desvincula/borra sus clientes | `Backend/src/main/java/com/StrattonApp/Backend/controller/EmpleadoController.java:115` | Corregido |
| UXF-07 | Alta | RF-11: los roles no se aplican en ningún sitio; la API es pública y el JWT nunca se envía ni se valida | `Backend/src/main/java/com/StrattonApp/Backend/config/SecurityConfig.java:70` | Corregido |
| UXF-08 | Alta | Flujos 1-10 en Docker/producción: CORS solo acepta http://localhost:4200 y las URLs del API están hardcodeadas | `Backend/src/main/java/com/StrattonApp/Backend/config/SecurityConfig.java:127` | Corregido |
| UXF-13 | Alta | README vs código: RF-2, RF-4, RF-5/RF-8, RF-10, RN-1, RN-2, RN-7, CRUD de suministros/comercializadoras/asesorías y rol MANAGER no tienen implementación | `Backend/src/main/java/com/StrattonApp/Backend/entities/Suministro.java:6` | Parcial: RF-4 (código postal) y plan por suministro siguen pendientes |
| UXF-09 | Media | Flujo 7: los tres contadores de suministros muestran el número de clientes; foto y asesoría vacíos; swal intrusivo | `Frontend/src/app/pages/empleados/detalles-empleado/detalles-empleado.component.html:31` | Corregido |
| UXF-10 | Media | Flujo 8: el auto-registro descarta nombre/apellidos/email, es público y los botones Google/GitHub no hacen nada | `Frontend/src/app/auth.service.ts:19` | Corregido: registro completo; botones Google/GitHub eliminados |
| UXF-12 | Media | Flujo 10: la página de inicio es un escaparate con datos falsos y una llamada a zippopotam que revienta | `Frontend/src/app/pages/inicio/inicio.component.ts:83` | Corregido |
| UXF-11 | Baja | Flujos 1 y 2: TypeError en ngOnInit por copiar arrays antes de recibir la respuesta HTTP | `Frontend/src/app/pages/empleados/lista-empleados/lista-empleados.component.ts:34` | Corregido |
| UXF-14 | Baja | Contenido placeholder/personal y recursos externos rotos en index.html, footer y modelos | `Frontend/src/index.html:81` | Corregido |
| UXF-15 | Baja | Código muerto o engañoso en el backend: plantillas Thymeleaf, AdminController, endpoints que devuelven null y filtros con secreto distinto | `Backend/src/main/java/com/StrattonApp/Backend/controller/AdminController.java:44` | Corregido |

## Hallazgos descartados en la verificación

| Id | Dimensión | Hallazgo | Motivo |
|---|---|---|---|
| B-03 | Backend: compilación y ejecución | CORS is never applied in the Spring Security chain; preflight OPTIONS requests get 401, blocking the Angular app entirely | The central claim (CORS never applied in the security chain, OPTIONS preflight -&gt; 401, login blocked) is wrong for Spring Boot 3.2.3 + Spring MVC. Spring MVC registers the mvcHandlerMappingIntrospector bean (WebMvcConfigurationSupport.mvcHandlerMappingIntrospector(), verified via javap on spring-web |
| F18 | Frontend Angular | detalles-cliente lee `cliente.nombre` antes de que llegue el GET (cliente undefined) | no verdict |

## Decisiones tomadas en la corrección

| Tema | Decisión |
|---|---|
| Plantillas Thymeleaf, `AdminController`, `TokenUtils`, `RequestFilter`, `JwtUtils`, `Banco` | Eliminados: código muerto o duplicado sin ningún controlador que lo usara. |
| Rol MANAGER | Se mantiene con semántica de jefe de equipo: ve y gestiona los clientes de todos los empleados, pero no administra empleados. Usuario de ejemplo `jefe/jefe`. |
| Auto-registro público | Se mantiene (es lo que describe el README) y se puede desactivar con `REGISTRO_ABIERTO=false`. |
| Convención de autoridades | Prefijo `ROLE_` + `hasRole(...)` en toda la configuración; el claim `role` del token sigue siendo `ADMIN`/`MANAGER`/`USER` para el frontend. |
| Borrado de un empleado con cartera | Cascada completa (clientes y suministros), literal del caso de uso del README. Un administrador no puede borrarse a sí mismo ni quitarse el rol ADMIN. |
| Campos obligatorios del cliente | Obligatorios: nombre, apellidos, DNI, email y teléfono (único). Opcionales: dirección, IBAN, fechas, CUPS y comercializadora. |
| Comercializadora del cliente | `Cliente.comercializadora` se mantiene como comercializadora principal (la del primer suministro); cada suministro tiene la suya. |
| SweetAlert2 y Bootstrap | Migrados a SweetAlert2 11 (helper `Alertas`) y Bootstrap 5 empaquetado desde `node_modules`. |
| Ciclo de vida del token | 10 horas por defecto (`JWT_EXPIRATION_MS`), sin refresh token: limitación documentada. |
| Esquema de base de datos | `ddl-auto=update` por defecto (configurable con `DDL_AUTO`); sin migraciones Flyway en esta iteración. |
| Java | Target 17 (imágenes Docker con Temurin 17); compila también con JDK 21. |
| Página de inicio | Se conserva como portada (vídeo, comparativa de comercializadoras y contacto) con accesos directos para el empleado autenticado; se retiran las reseñas inventadas y el mapa roto. |
| Puertos en Docker | Por defecto se publican 80 (frontend con proxy `/api`), 8080 y 3306; todos configurables en `.env`. |
| Clientes de otros empleados | El ADMIN puede elegir el asesor al crear o editar un cliente; USER crea siempre para sí mismo. |

## Pendiente para futuras iteraciones

- RF-4: ubicación (comunidad, provincia, municipio) a partir del código postal.
- RF-2/RF-10 ampliados: plan/tarifa, tipo LUZ/GAS y fechas de inicio/fin por suministro, con avisos de vencimiento.
- Autenticación con Google (OAuth2) prevista en el README.
- Refresh token y revocación; `strict` de TypeScript activado progresivamente; migraciones con Flyway para producción.
