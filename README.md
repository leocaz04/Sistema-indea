# Sistema de Gestión para INDEA - Sprint 3 (Incremento 1)

Equipo 9 - Programación Orientada a Objetos, Universidad Veracruzana.

## Qué incluye este incremento

- Inicio de sesión con usuario y contraseña (HU08): contraseñas con PBKDF2, bloqueo tras 3 intentos fallidos y permisos por rol.
- Panel principal con resumen (equipos en reparación y clientes registrados).
- Clientes (HU09): registrar, consultar, buscar, editar y eliminar (solo administrador).
- Reparaciones (HU02, HU04): registrar equipo con folio único y recibo, asignar técnico, actualizar estado, diagnóstico, costo y pago, con historial de cambios.
- Consulta por folio de solo lectura (HU06), disponible desde la pantalla de inicio de sesión sin entrar al sistema.

Flujo completo para la demostración: iniciar sesión, registrar cliente, registrar reparación (se genera el folio), actualizar el estado hasta Entregado y consultar el folio.

## Cómo ejecutarlo

1. Abre MySQL Workbench, abre el archivo sql/01_crear_base_indea.sql y ejecútalo completo (icono del rayo). Crea la base indea_db con sus 11 tablas.
2. Descarga MySQL Connector/J (opción Platform Independent) desde https://dev.mysql.com/downloads/connector/j/ y copia el archivo .jar a la carpeta lib.
3. En Eclipse: File > Import > General > Existing Projects into Workspace y elige esta carpeta.
4. Clic derecho en el proyecto > Build Path > Configure Build Path > Libraries > Classpath > Add JARs... y elige el .jar de la carpeta lib.
5. Abre config/db.properties y cambia db.password por la contraseña de tu MySQL (y db.user si no usas root).
6. Ejecuta com.indea.Main (clic derecho > Run As > Java Application).

La primera vez que se abre el programa se crean automáticamente los usuarios de prueba.

## Usuarios de prueba

| Rol | Usuario | Contraseña |
|---|---|---|
| Administrador | admin | admin123 |
| Técnico | tecnico1 | tecnico123 |
| Vendedor | vendedor1 | vendedor123 |
| Marketing | marketing1 | marketing123 |

## Estructura

- com.indea.modelo: Persona, Cliente, Empleado, Usuario, Reparacion, CambioEstado y enumeraciones.
- com.indea.dao: interfaces y su implementación JDBC.
- com.indea.servicio: reglas de negocio, validaciones y permisos por rol.
- com.indea.controlador: conecta la vista con los servicios.
- com.indea.vista: ventanas Swing.
- com.indea.util: ConexionBD, SeguridadUtil, Sesion y DatosIniciales.
- com.indea.excepcion: excepciones propias.
