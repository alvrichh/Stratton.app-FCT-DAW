# Stratton.app-FCT-DAW
Proyecto final de grado superior de desarrollo de aplicaciones web, partiremos del proyecto final de base de datos del año anterior, ampliándolo con las tecnologías que hemos aprendido este año, utilizando para la seguridad security springboot, y para el frontend angular / react.
## Requisitos Funcionales:
- RF-1: El sistema debe permitir registrar a los clientes recopilando sus datos.
- RF-2: El sistema debe conocer a qué comercializadora pertenece cada cliente y cuál es el nombre de su oferta (idPlan).
- RF-3: La información de los clientes se basará en sus números de teléfono, que serán únicos para la identificación de la información.
- RF-4: Las ubicaciones completas, como comunidad autónoma, provincia y municipio, se determinarán mediante el código postal (ZIP).
- RF-5: Cada cliente debe tener uno o más CUPS_luz como puntos de suministro, junto con su UBICACION.
- RF-6: El sistema debe permitir que los empleados estén asociados a una asesoría.
- RF-7: El sistema debe permitir que los clientes tengan información bancaria asociada.
- RF-8: El sistema debe permitir que los clientes tengan múltiples CUPS_gas como puntos de suministro.
- RF-9: El sistema debe permitir que los empleados estén asociados a un cliente en particular.
- RF-10: El sistema debe permitir la gestión de contratos de clientes, incluyendo la fecha de inicio y finalización del contrato, así como el estado del mismo (activo, inactivo, cancelado, etc.).
## Reglas de Negocio:
- RN-1: El titular del contrato debe ser mayor de edad.
- RN-2: Los números de teléfono deben tener un formato válido.
- RN-3: Los puntos de suministro de energía deben estar asociados a una ubicación válida.
- RN-4: Los puntos de suministro de gas deben estar asociados a una ubicación válida.
- RN-5: Los empleados asignados a clientes deben tener un código válido.
- RN-6: La asignación de empleados a clientes debe ser coherente.   
- RN-7: Los números de cuenta bancaria (CCC) deben tener un formato válido.

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

