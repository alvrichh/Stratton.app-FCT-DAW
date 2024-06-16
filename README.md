# Stratton.app-FCT-DAW
Proyecto final de grado superior de desarrollo de aplicaciones web, partiremos del proyecto final de base de datos del año anterior, ampliándolo con las tecnologías que hemos aprendido este año, utilizando para la seguridad security springboot, y para el frontend angular 17

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


## Motivación:
La principal motivación del proyecto es poder gestionar mis propios clientes o los clientes de una determinada asesoría energética como a sus empleados, para mejorar la gestión de los equipos comerciales y la organización de los clientes, pudiéndolos clasificar de manera clara y concisa. 

## ARQUITECTURA
![image](https://github.com/alvrichh/Stratton.app-FCT-DAW/assets/81918923/5ef284ac-e66f-4159-acb7-bc72c404f6b5)

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

## Dependencias Springboot:
- Spring Boot Starter Web:
Esta dependencia proporciona las funcionalidades básicas para crear aplicaciones web con Spring Boot.
Incluye el servidor web incorporado (por defecto, Tomcat) y otras utilidades web.
Agrega automáticamente otras dependencias relacionadas con la web.
- Spring Boot Starter Security:
Esta dependencia agrega todas las bibliotecas necesarias para habilitar la seguridad en nuestra aplicación.
Incluye Spring Security, que nos permite configurar la autenticación y la autorización.
También proporciona características como protección CSRF, manejo de sesiones, etc.
- Spring Boot Starter Data JPA (opcional, pero recomendado):
Si planeas trabajar con una base de datos, esta dependencia simplifica la configuración de JPA (Java Persistence API).
Te permite interactuar fácilmente con bases de datos relacionales.
- Spring Boot Starter Thymeleaf (opcional, si planeas usar Thymeleaf como motor de plantillas):
Thymeleaf es un motor de plantillas que se integra bien con Spring Boot.
Facilita la creación de vistas HTML dinámicas.
- Spring Boot Starter OAuth2 Client (para autenticación con Google):
Esta dependencia nos permite integrar la autenticación con proveedores OAuth2, como Google.
Simplifica la configuración para conectarse con servicios de autenticación externos.
- Spring Boot DevTools (opcional, pero útil para desarrollo):
Proporciona herramientas para reiniciar automáticamente la aplicación durante el desarrollo.
Facilita la recarga en caliente de cambios en el código.
![image](https://github.com/alvrichh/Stratton.app-FCT-DAW/assets/81918923/d7f7e791-db1d-44a3-9f43-d87c1953a5a3)
## @TODO
- Separación de Responsabilidades:
    * Angular se encargará de la interfaz de usuario (UI) y la interacción con el usuario.
    * Spring Boot se centrará en la lógica de negocio, la autenticación, la autorización y la comunicación con la base de datos.
    * Mantén una clara separación entre estas dos partes para facilitar el mantenimiento y la escalabilidad.
- API RESTful:
    * Diseña una API RESTful en Spring Boot para que Angular pueda comunicarse con el backend.
    * Define rutas (endpoints) para las operaciones CRUD (crear, leer, actualizar, eliminar) en tus entidades (clientes, empleados, contratos, etc.).
- Autenticación y Autorización:
    * Utiliza Spring Security para gestionar la autenticación y la autorización.
    * Configura la autenticación basada en tokens (JWT) o mediante OAuth2 (si planeas integrar Google).
    * Asegúrate de proteger las rutas relevantes (por ejemplo, las que requieren inicio de sesión).
-  Validación de Datos:
    * Implementa validaciones en el backend para garantizar que los datos ingresados por el usuario sean válidos.
    * Usa anotaciones de validación de Spring (como @NotNull, @Size, etc.) en tus modelos.
- Manejo de Excepciones:
    * Crea un manejo centralizado de excepciones para proporcionar respuestas claras y consistentes en caso de errores.
    * Define excepciones personalizadas para situaciones específicas (por ejemplo, cliente no encontrado, acceso denegado, etc.).
-  Documentación de la API:
    * Genera documentación para tu API (por ejemplo, utilizando Swagger o Springfox).
    * Esto ayudará a los desarrolladores de frontend a comprender cómo interactuar con el backend.
- CORS (Cross-Origin Resource Sharing):
    * Configura CORS para permitir que Angular se comunique con el backend desde un dominio diferente.
    * Define las políticas de acceso en tu aplicación Spring Boot.
- Pruebas Unitarias y de Integración:
    * Escribe pruebas unitarias y de integración para verificar el funcionamiento correcto de tus controladores, servicios y repositorios.
    * Utiliza herramientas como JUnit y Mockito.
- Seguridad en Producción:
    * En producción, asegúrate de configurar correctamente las propiedades de seguridad (por ejemplo, cambiar las claves secretas, habilitar HTTPS, etc.).
- Despliegue:
    * Decide dónde y cómo desplegarás tu aplicación (por ejemplo, en un servidor local, en la nube, etc.).
    * Considera opciones como Docker y Kubernetes para facilitar el despliegue y la escalabilidad.

