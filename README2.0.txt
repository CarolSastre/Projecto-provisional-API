JCC API - Sistema de Gestión Académica y Control de Asistencia

Este proyecto nace como una solución integral para digitalizar y gestionar los procesos del día a día en un entorno educativo o institucional. 
El objetivo principal era construir un backend robusto, escalable y, sobre todo, seguro, que pudiera manejar desde el 
fichaje diario de los usuarios hasta la gestión de calificaciones y eventos.

Contexto del Proyecto
Seguridad real: Implementar autenticación robusta mediante tokens JWT utilizando cifrado asimétrico (claves RSA públicas y privadas).

Bases de datos NoSQL: Salir de la zona de confort de las bases de datos relacionales tradicionales y aprovechar la flexibilidad
 documental de MongoDB para registros variables como las asistencias o los avisos.

Arquitectura Limpia: Separar responsabilidades usando el patrón Modelo-Vista-Controlador (MVC) adaptado a APIs, con capas bien definidas
 de Repositorios, DAOs, Servicios y Controladores.

Stack Tecnológico
He utilizado tecnologías modernas y estándares de la industria para asegurar que el proyecto sea mantenible y eficiente:

Lenguaje: Java 17+

Framework Principal: Spring Boot 3.x

Base de Datos: MongoDB (Spring Data MongoDB)

Seguridad: Spring Security + JSON Web Tokens (JWT) con encriptación RSA.

Gestor de Dependencias: Maven

Estructura de Datos: JSON

Control de Versiones: Git

📂 Arquitectura y Estructura del Código
He organizado el proyecto (com.intermodular.jcc) siguiendo una arquitectura en capas estrictas para evitar el acoplamiento y facilitar 
el testing y la depuración.

Plaintext
src/main/java/com/intermodular/jcc/
├── config/       # Configuraciones globales (CORS, MongoDB, Spring Security, JWT)
├── controller/   # Endpoints de la API REST. Manejan las peticiones HTTP y devuelven DTOs/JSON
├── dao/          # Data Access Objects. Lógica intermedia de acceso a datos (Patrón DAO)
├── dto/          # Data Transfer Objects. Objetos para enviar/recibir datos limpios (ej. LoginResponse)
├── entities/     # Modelos de dominio mapeados a colecciones de MongoDB (@Document)
├── repository/   # Interfaces que extienden MongoRepository para las consultas a la BBDD
├── service/      # Lógica de negocio pesada y servicios de autenticación (UserDetailsServiceImpl)
└── JccApplication.java # Clase principal de arranque

Decisiones de Diseño
¿Por qué usar DAOs además de Repositories? Aunque Spring Data provee los Repository, decidí crear una capa DAO intermedia. 
Esto me ha permitido encapsular lógicas de consulta más complejas (por ejemplo, buscar registros de acceso entre dos fechas
concretas o filtrar faltas por departamento) sin ensuciar los controladores ni depender exclusivamente de los nombres de métodos
de Spring Data.

Gestión de Archivos: He implementado un sistema local de subida de archivos (almacenados en la carpeta /uploads/).
Esto se utiliza, por ejemplo, para que cada usuario tenga su imagen de perfil (ej. 11111111A_1770303078663_images.jpg).

Seguridad y Autenticación (El mayor reto)
La seguridad no es un simple usuario y contraseña. Me he peleado bastante con la configuración de Spring Security
para dejarlo exactamente como requiere un entorno de producción:

JWT con Claves Asimétricas: En lugar de usar una clave secreta simple (simétrica), generé un par de claves RSA (app.key y app.pub).
El servidor firma los tokens con la clave privada y valida las peticiones con la pública. Esto hace que el token sea mucho más difícil
de falsificar.

Stateless Session: La API no guarda sesiones en memoria (SessionCreationPolicy.STATELESS). Cada petición HTTP a un endpoint protegido
debe incluir el token en la cabecera Authorization: Bearer <token>.

Filtros de Seguridad: En WebSecurityConfig.java y SecurityConfig.java, configuré el árbol de rutas.
Endpoints como el login son públicos, pero gestionar calificaciones, fichar o ver faltas requiere estar autenticado y,
en muchos casos, tener el Rol adecuado (ej. ADMIN, PROFESOR, ALUMNO).

Modelo de Datos (Entities)
La base de datos está modelada en MongoDB con las siguientes colecciones principales:

Usuario: Entidad central. Almacena credenciales, datos personales, roles y referencias a su departamento.

Rol y Departamento: Clasificación jerárquica y de permisos de los usuarios.

RegistroAcceso: El core del control horario. Guarda el timestamp exacto de entrada y salida del usuario.

Falta y Horario: Control de ausencias y definición de turnos.

Calificacion y Evento: Módulo puramente académico para gestión de notas y calendario escolar/institucional.

Aviso: Sistema de notificaciones o tablón de anuncios interno.

Módulos y Endpoints Principales
La API expone un gran abanico de endpoints. Aquí un resumen de las funcionalidades de los controladores:

Usuarios y Autenticación (UsuarioController)
Registro de nuevos usuarios (encriptando la contraseña con BCrypt).

Login y generación de JWT de acceso.

Actualización de perfiles y subida de imágenes a /uploads/.

Control de Presencia (AccesoController & AsistenciaController)
Endpoint para registrar el "fichaje" de entrada y salida. Calcula automáticamente la hora del sistema para evitar manipulaciones del cliente.

Consultas de historial de accesos por usuario y mes.

Gestión Académica (CalificacionesController & EventoController)
CRUD completo de calificaciones (asignar nota a un alumno en una materia concreta).

Creación de eventos en el calendario institucional (exámenes, festivos, reuniones).

Configuración y Notificaciones (AvisoController & HorarioController)
Publicación de avisos generales o dirigidos a departamentos específicos.

Asignación de horarios base para comprobar posteriormente si un usuario ha llegado tarde (generando una entidad Falta).

Instalación y Despliegue Local
Si quieres clonar este repositorio y probar la API en tu máquina, sigue estos pasos:

Prerrequisitos
Tener Java 17 o superior instalado.

Tener Maven (aunque el proyecto incluye el wrapper mvnw).

Un servidor de MongoDB ejecutándose en local (puerto por defecto 27017) o un clúster en MongoDB Atlas.

Pasos
Clonar el repositorio:

Bash
git clone <url-del-repositorio>
cd Projecto-provisional-API
Configurar Propiedades:
Abre el archivo src/main/resources/application.properties y asegúrate de que la URI de MongoDB apunte a tu base de datos:

Properties
spring.data.mongodb.uri=mongodb://localhost:27017/jcc_db
# Ajusta el puerto del servidor si es necesario
server.port=8080
Compilar y Ejecutar:
Puedes levantar el proyecto usando el wrapper de Maven integrado:

Bash
# En Windows
mvnw.cmd spring-boot:run

# En Linux/Mac
./mvnw spring-boot:run
Probar la API:
La aplicación arrancará en http://localhost:8080. Puedes usar Postman o Insomnia para atacar los endpoints. Te recomiendo empezar creando un usuario y haciendo un POST a la ruta de Login para obtener tu token Bearer.
