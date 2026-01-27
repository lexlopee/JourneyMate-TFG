# Aplicaciones y Plugins

# Cosas importantes
Si hay Paginas Web en las cuales tenemos que iniciar sesion, lo suyo seria poner una cuenta conjunta o crear una cuenta para usarla solo para ello.

# Ejecuccion del proyecto 
mvn spring-boot:run


# Creacion de la Base de Datos
Mirar como crear esto

<img width="1029" height="775" alt="image" src="https://github.com/user-attachments/assets/11ae753a-7506-4bf6-b644-eff11835d935" />



Contraseña--> journeymate-bbdd

# Android Studio
Iniciamos el Android Studio y nos dirigimos al apartado de Plugins y le tenemos que poner esto en el buscador. 

<img width="780" height="645" alt="image" src="https://github.com/user-attachments/assets/170fc577-ae28-444f-8074-d592fce63a84" />


# Instalacion de Postamn
1. En el navegador ir a la pagina oficial de Postman
   [https://www.postman.com/downloads/ ](https://www.postman.com/downloads/)
2. Pinchar en "Dowload for Windows"
3. Se descargaŕa un archivo:
   Postman-win64-Setup.exe
4. Doble click y en el archivo descargado 


# Enlace para crear nuestro proyecto de backend

https://start.spring.io/ 

# Instalación de React 
1. Enlace para instalar Node.js (importante para poder usar 'React').
https://nodejs.org 

Pasos:
- Darle a seguir todo a "Next/Continua".
- Reiniciar el PC (en algunos casos no hace falta, pero para asegurar lo hacemos).

2. Comprobaciones de instalaciones (terminal)
node -v 
npm -v

3. Crear un proyecto React 
npx create-react-app mi-frontend 

para arrancarlo:  
- cd mi-frontend
- npm start 

# APIs Google Maps y API viajes

- Crear una cuenta en Google Cloud --> https://console.cloud.google.com 

Mas usadas --> 
- Activar la API que queramos
 Las más usadas: 
 - Maps JavaScript API (para mostrar mapas en React)
 - Geocoding API (convertir direcciones en coordenadas)
 - Places API  (buscar sitios, restaurantes, hoteles…)
 - Directions API (rutas, distancias, tiempos)

APIs de Vuelos
- Amadeus AP (Vuelos, hoteles, precios, aeropuerto)
- Skyscanner API (Búsqueda de vuelos y precios)
- AviationStack (Información de vuelos en tiempo rea)

APIs de hoteles
- Booking.com API (Hoteles, disponibilidad, precios)
- Hotels.com API (Hoteles y reviews)
- Airbnb API (no oficial)  (Listados y precios)

APIs de transporte 
- Google Directions API (Rutas en coche, bus, bici, andando)
- TransportAPI (Transporte público (UK))
- OpenTripPlanner (Rutas multimodales)

APIs de turismo 
- TripAdvisor API  (Reviews, lugares, actividades)
- OpenTripMap (Lugares turísticos, POIs, mapas)
- WikiVoyage API (Información turística abierta)

# Crear una cuenta de PayPal Developer
https://developer.paypal.com 

1. Crear una API Key/ Client ID

2. Usar su SDK o API REST desde nuestro backend

# Stripe (muy usado) igual que PayPal
1. Creas cuenta en https://stripe.com

2. Obtenemos la Secret Key 

3. LLaamas a su API desde Spring Boot

# División del BACKEND por integrantes (según el anteproyecto JourneyMate)

Integrante 1 – Backend base y Base de Datos
Rol: Gestión de datos y lógica principal
Responsabilidades:
Diseño e implementación del modelo relacional:
Usuario
Reserva
Hotel
Actividad
Punto de interés
Creación de entidades JPA y relaciones.
Configuración de PostgreSQL.
Repositorios (JpaRepository).
Servicios CRUD principales.
Validaciones de negocio básicas.
Relacionado con el anteproyecto:
Objetivo específico 1 (Base de datos).
Objetivo específico 2 (Backend Spring Boot).
Módulos: Bases de Datos y Acceso a Datos.

Integrante 2 – Seguridad y Usuarios
Rol: Autenticación y control de acceso
Responsabilidades:
Registro y login de usuarios.
Implementación de Spring Security + JWT.
Gestión de roles (usuario / administrador).
Encriptación de contraseñas (BCrypt).
Protección de endpoints REST.
Controladores de usuario.
Validación de datos de entrada.
Relacionado con el anteproyecto:
Objetivo específico 6 (Autenticación JWT).
Arquitectura basada en APIs.
Módulo: Servicios y Procesos.

Integrante 3 – Integraciones y funcionalidades avanzadas
Rol: APIs externas y lógica turística
Responsabilidades:
Integración con Google Maps API (mapas y geolocalización).
Integración con Skyscanner / APIs de viajes.
Implementación del comparador de precios.
Gestión de rutas turísticas personalizadas.
Gestión de pagos (PayPal – sandbox).
Base del módulo opcional de IA recomendadora.
Manejo global de errores.
Relacionado con el anteproyecto:
Objetivos específicos 5, 7 y 9.
Conceptos clave: geolocalización, comparador, IA.
Módulo: Servicios y Procesos.

# BackEnd dvision por carpetas
INTEGRANTE 1 — Backend base + Base de Datos
Rol: Lógica principal, CRUD, entidades, repositorios, servicios base
Carpetas que usa:
   entity/
   dao/
   service/
   controller/
   config/app/
   dto/

Carpetas específicas dentro de cada dominio:
Entidades (entity/)
   - entity/user/ → solo Usuario (sin seguridad)
   - entity/route/
   - entity/interest/
   - entity/booking/
   - entity/payment/
   - entity/service/
   - entity/service_type/
-----
Repositorios (dao/)

Todos los repositorios de CRUD:
  - usuario
  - ruta
  - punto_interes
  - reserva
  - pago
  - servicio_turistico
  - vuelo, tren, hotel, etc.
-------
Servicios (service/)
   - Servicios CRUD de todos los dominios anteriores
-------
Controladores (controller/)
Endpoints CRUD de: 
   - usuario (solo CRUD, no login)
   - rutas
   - puntos de interés
   - reservas
   - pagos
   - servicios turísticos
-------
Configuración (config/app/)
   - Datasource
   - PostgreSQL
   - Beans generales
-------
DTOs (dto/)
   - Request y response para CRUD



=================






INTEGRANTE 2 — Seguridad y Usuarios
Rol: Autenticación, autorización, JWT, roles, protección de endpoints
Carpetas que usa:

- config/security/
- entity/user/
- dao/user/
- service/user/
- controller/user/
- utils/jwt/
- exception/
--------
Carpetas específicas:
config/security/
   - Configuración de Spring Security
   - Filtros JWT
   - PasswordEncoder
   - AuthenticationManager
---------
entity/user/
   - Usuario
   - Rol
   - TokenJWT
---------
dao/user/  
   - UsuarioRepository
   - RolRepository
   - TokenRepository
---------
service/user/
  - Login
  - Registro
  - Gestión de roles
  - Renovación de tokens 
----------
controller/user/
   - /auth/login
   - /auth/register
   - /auth/refresh
   - /auth/logout
----------
utils/jwt/
   - Generación de tokens
   - Validación
   - Extracción de claims
----------
exception/
   - Manejo de errores de autenticación
   - Excepciones personalizadas



=============




INTEGRANTE 3 — Integraciones externas + Funcionalidades avanzadas
Rol: APIs externas, comparadores, rutas inteligentes, IA, pagos
Carpetas que usa:
   - external/
   - service/external/
   - controller/external/
   - utils/geolocation/
   - exception/handler/
-------   
Carpetas específicas:
external/
   - external/maps/ → Google Maps API
   - external/flights/ → Skyscanner
   - external/payments/ → PayPal
   - external/ai/ → IA recomendadora
-------
service/external/
   - Servicios que consumen APIs externas
   - Comparador de precios
   - Generación de rutas personalizadas
--------
controller/external/
   Endpoints como:
   - /maps/nearby
   - /flights/search
   - /payments/pay
   - /ai/recommend
--------
utils/geolocation/
   - Cálculo de distancias
   - Conversión de coordenadas
   - Validación de lat/long
--------
exception/handler/
   - Manejo global de errores
   - Respuestas uniformes para APIs externas
# Errores Proyectos

/home/alex/.jdks/ms-21.0.9/bin/java -XX:TieredStopAtLevel=1 -Dspring.output.ansi.enabled=always -javaagent:/snap/intellij-idea-community/709/lib/idea_rt.jar=46049 -Dfile.encoding=UTF-8 -Dsun.stdout.encoding=UTF-8 -Dsun.stderr.encoding=UTF-8 -classpath /home/alex/Escritorio/JourneyMate-TFG/JourneyMate/target/classes:/home/alex/.m2/repository/org/springframework/boot/spring-boot-starter-web/3.2.5/spring-boot-starter-web-3.2.5.jar:/home/alex/.m2/repository/org/springframework/boot/spring-boot-starter/3.2.5/spring-boot-starter-3.2.5.jar:/home/alex/.m2/repository/org/springframework/boot/spring-boot-starter-logging/3.2.5/spring-boot-starter-logging-3.2.5.jar:/home/alex/.m2/repository/ch/qos/logback/logback-classic/1.4.14/logback-classic-1.4.14.jar:/home/alex/.m2/repository/ch/qos/logback/logback-core/1.4.14/logback-core-1.4.14.jar:/home/alex/.m2/repository/org/apache/logging/log4j/log4j-to-slf4j/2.21.1/log4j-to-slf4j-2.21.1.jar:/home/alex/.m2/repository/org/apache/logging/log4j/log4j-api/2.21.1/log4j-api-2.21.1.jar:/home/alex/.m2/repository/org/slf4j/jul-to-slf4j/2.0.13/jul-to-slf4j-2.0.13.jar:/home/alex/.m2/repository/jakarta/annotation/jakarta.annotation-api/2.1.1/jakarta.annotation-api-2.1.1.jar:/home/alex/.m2/repository/org/yaml/snakeyaml/2.2/snakeyaml-2.2.jar:/home/alex/.m2/repository/org/springframework/boot/spring-boot-starter-json/3.2.5/spring-boot-starter-json-3.2.5.jar:/home/alex/.m2/repository/com/fasterxml/jackson/core/jackson-databind/2.15.4/jackson-databind-2.15.4.jar:/home/alex/.m2/repository/com/fasterxml/jackson/core/jackson-annotations/2.15.4/jackson-annotations-2.15.4.jar:/home/alex/.m2/repository/com/fasterxml/jackson/core/jackson-core/2.15.4/jackson-core-2.15.4.jar:/home/alex/.m2/repository/com/fasterxml/jackson/datatype/jackson-datatype-jdk8/2.15.4/jackson-datatype-jdk8-2.15.4.jar:/home/alex/.m2/repository/com/fasterxml/jackson/datatype/jackson-datatype-jsr310/2.15.4/jackson-datatype-jsr310-2.15.4.jar:/home/alex/.m2/repository/com/fasterxml/jackson/module/jackson-module-parameter-names/2.15.4/jackson-module-parameter-names-2.15.4.jar:/home/alex/.m2/repository/org/springframework/boot/spring-boot-starter-tomcat/3.2.5/spring-boot-starter-tomcat-3.2.5.jar:/home/alex/.m2/repository/org/apache/tomcat/embed/tomcat-embed-core/10.1.20/tomcat-embed-core-10.1.20.jar:/home/alex/.m2/repository/org/apache/tomcat/embed/tomcat-embed-websocket/10.1.20/tomcat-embed-websocket-10.1.20.jar:/home/alex/.m2/repository/org/springframework/spring-web/6.1.6/spring-web-6.1.6.jar:/home/alex/.m2/repository/org/springframework/spring-beans/6.1.6/spring-beans-6.1.6.jar:/home/alex/.m2/repository/io/micrometer/micrometer-observation/1.12.5/micrometer-observation-1.12.5.jar:/home/alex/.m2/repository/io/micrometer/micrometer-commons/1.12.5/micrometer-commons-1.12.5.jar:/home/alex/.m2/repository/org/springframework/spring-webmvc/6.1.6/spring-webmvc-6.1.6.jar:/home/alex/.m2/repository/org/springframework/spring-context/6.1.6/spring-context-6.1.6.jar:/home/alex/.m2/repository/org/springframework/spring-expression/6.1.6/spring-expression-6.1.6.jar:/home/alex/.m2/repository/org/springframework/boot/spring-boot-starter-data-jpa/3.2.5/spring-boot-starter-data-jpa-3.2.5.jar:/home/alex/.m2/repository/org/springframework/boot/spring-boot-starter-aop/3.2.5/spring-boot-starter-aop-3.2.5.jar:/home/alex/.m2/repository/org/aspectj/aspectjweaver/1.9.22/aspectjweaver-1.9.22.jar:/home/alex/.m2/repository/org/springframework/boot/spring-boot-starter-jdbc/3.2.5/spring-boot-starter-jdbc-3.2.5.jar:/home/alex/.m2/repository/com/zaxxer/HikariCP/5.0.1/HikariCP-5.0.1.jar:/home/alex/.m2/repository/org/springframework/spring-jdbc/6.1.6/spring-jdbc-6.1.6.jar:/home/alex/.m2/repository/org/hibernate/orm/hibernate-core/6.4.4.Final/hibernate-core-6.4.4.Final.jar:/home/alex/.m2/repository/jakarta/persistence/jakarta.persistence-api/3.1.0/jakarta.persistence-api-3.1.0.jar:/home/alex/.m2/repository/jakarta/transaction/jakarta.transaction-api/2.0.1/jakarta.transaction-api-2.0.1.jar:/home/alex/.m2/repository/org/jboss/logging/jboss-logging/3.5.3.Final/jboss-logging-3.5.3.Final.jar:/home/alex/.m2/repository/org/hibernate/common/hibernate-commons-annotations/6.0.6.Final/hibernate-commons-annotations-6.0.6.Final.jar:/home/alex/.m2/repository/io/smallrye/jandex/3.1.2/jandex-3.1.2.jar:/home/alex/.m2/repository/com/fasterxml/classmate/1.6.0/classmate-1.6.0.jar:/home/alex/.m2/repository/net/bytebuddy/byte-buddy/1.14.13/byte-buddy-1.14.13.jar:/home/alex/.m2/repository/org/glassfish/jaxb/jaxb-runtime/4.0.5/jaxb-runtime-4.0.5.jar:/home/alex/.m2/repository/org/glassfish/jaxb/jaxb-core/4.0.5/jaxb-core-4.0.5.jar:/home/alex/.m2/repository/org/eclipse/angus/angus-activation/2.0.2/angus-activation-2.0.2.jar:/home/alex/.m2/repository/org/glassfish/jaxb/txw2/4.0.5/txw2-4.0.5.jar:/home/alex/.m2/repository/com/sun/istack/istack-commons-runtime/4.1.2/istack-commons-runtime-4.1.2.jar:/home/alex/.m2/repository/jakarta/inject/jakarta.inject-api/2.0.1/jakarta.inject-api-2.0.1.jar:/home/alex/.m2/repository/org/antlr/antlr4-runtime/4.13.0/antlr4-runtime-4.13.0.jar:/home/alex/.m2/repository/org/springframework/data/spring-data-jpa/3.2.5/spring-data-jpa-3.2.5.jar:/home/alex/.m2/repository/org/springframework/data/spring-data-commons/3.2.5/spring-data-commons-3.2.5.jar:/home/alex/.m2/repository/org/springframework/spring-orm/6.1.6/spring-orm-6.1.6.jar:/home/alex/.m2/repository/org/springframework/spring-tx/6.1.6/spring-tx-6.1.6.jar:/home/alex/.m2/repository/org/slf4j/slf4j-api/2.0.13/slf4j-api-2.0.13.jar:/home/alex/.m2/repository/org/springframework/spring-aspects/6.1.6/spring-aspects-6.1.6.jar:/home/alex/.m2/repository/org/springframework/boot/spring-boot-starter-security/3.2.5/spring-boot-starter-security-3.2.5.jar:/home/alex/.m2/repository/org/springframework/spring-aop/6.1.6/spring-aop-6.1.6.jar:/home/alex/.m2/repository/org/springframework/security/spring-security-config/6.2.4/spring-security-config-6.2.4.jar:/home/alex/.m2/repository/org/springframework/security/spring-security-core/6.2.4/spring-security-core-6.2.4.jar:/home/alex/.m2/repository/org/springframework/security/spring-security-crypto/6.2.4/spring-security-crypto-6.2.4.jar:/home/alex/.m2/repository/org/springframework/security/spring-security-web/6.2.4/spring-security-web-6.2.4.jar:/home/alex/.m2/repository/org/springframework/boot/spring-boot-starter-validation/3.2.5/spring-boot-starter-validation-3.2.5.jar:/home/alex/.m2/repository/org/apache/tomcat/embed/tomcat-embed-el/10.1.20/tomcat-embed-el-10.1.20.jar:/home/alex/.m2/repository/org/hibernate/validator/hibernate-validator/8.0.1.Final/hibernate-validator-8.0.1.Final.jar:/home/alex/.m2/repository/jakarta/validation/jakarta.validation-api/3.0.2/jakarta.validation-api-3.0.2.jar:/home/alex/.m2/repository/org/postgresql/postgresql/42.6.2/postgresql-42.6.2.jar:/home/alex/.m2/repository/org/checkerframework/checker-qual/3.31.0/checker-qual-3.31.0.jar:/home/alex/.m2/repository/org/projectlombok/lombok/1.18.32/lombok-1.18.32.jar:/home/alex/.m2/repository/org/springframework/boot/spring-boot-devtools/3.2.5/spring-boot-devtools-3.2.5.jar:/home/alex/.m2/repository/org/springframework/boot/spring-boot/3.2.5/spring-boot-3.2.5.jar:/home/alex/.m2/repository/org/springframework/boot/spring-boot-autoconfigure/3.2.5/spring-boot-autoconfigure-3.2.5.jar:/home/alex/.m2/repository/jakarta/xml/bind/jakarta.xml.bind-api/4.0.2/jakarta.xml.bind-api-4.0.2.jar:/home/alex/.m2/repository/jakarta/activation/jakarta.activation-api/2.1.3/jakarta.activation-api-2.1.3.jar:/home/alex/.m2/repository/org/springframework/spring-core/6.1.6/spring-core-6.1.6.jar:/home/alex/.m2/repository/org/springframework/spring-jcl/6.1.6/spring-jcl-6.1.6.jar com.example.JourneyMate.JourneyMateApplication

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.2.5)

2026-01-27T10:49:58.540+01:00  INFO 22331 --- [JourneyMate] [  restartedMain] c.e.JourneyMate.JourneyMateApplication   : Starting JourneyMateApplication using Java 21.0.9 with PID 22331 (/home/alex/Escritorio/JourneyMate-TFG/JourneyMate/target/classes started by alex in /home/alex/Escritorio/JourneyMate-TFG/JourneyMate)
2026-01-27T10:49:58.542+01:00  INFO 22331 --- [JourneyMate] [  restartedMain] c.e.JourneyMate.JourneyMateApplication   : No active profile set, falling back to 1 default profile: "default"
2026-01-27T10:49:58.585+01:00  INFO 22331 --- [JourneyMate] [  restartedMain] .e.DevToolsPropertyDefaultsPostProcessor : Devtools property defaults active! Set 'spring.devtools.add-properties' to 'false' to disable
2026-01-27T10:49:58.586+01:00  INFO 22331 --- [JourneyMate] [  restartedMain] .e.DevToolsPropertyDefaultsPostProcessor : For additional web related logging consider setting the 'logging.level.web' property to 'DEBUG'
2026-01-27T10:49:59.054+01:00  INFO 22331 --- [JourneyMate] [  restartedMain] .s.d.r.c.RepositoryConfigurationDelegate : Bootstrapping Spring Data JPA repositories in DEFAULT mode.
2026-01-27T10:49:59.128+01:00  INFO 22331 --- [JourneyMate] [  restartedMain] .s.d.r.c.RepositoryConfigurationDelegate : Finished Spring Data repository scanning in 68 ms. Found 27 JPA repository interfaces.
2026-01-27T10:49:59.722+01:00  INFO 22331 --- [JourneyMate] [  restartedMain] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port 8080 (http)
2026-01-27T10:49:59.731+01:00  INFO 22331 --- [JourneyMate] [  restartedMain] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2026-01-27T10:49:59.732+01:00  INFO 22331 --- [JourneyMate] [  restartedMain] o.apache.catalina.core.StandardEngine    : Starting Servlet engine: [Apache Tomcat/10.1.20]
2026-01-27T10:49:59.760+01:00  INFO 22331 --- [JourneyMate] [  restartedMain] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
2026-01-27T10:49:59.760+01:00  INFO 22331 --- [JourneyMate] [  restartedMain] w.s.c.ServletWebServerApplicationContext : Root WebApplicationContext: initialization completed in 1174 ms
2026-01-27T10:49:59.859+01:00  INFO 22331 --- [JourneyMate] [  restartedMain] o.hibernate.jpa.internal.util.LogHelper  : HHH000204: Processing PersistenceUnitInfo [name: default]
2026-01-27T10:49:59.898+01:00  INFO 22331 --- [JourneyMate] [  restartedMain] org.hibernate.Version                    : HHH000412: Hibernate ORM core version 6.4.4.Final
2026-01-27T10:49:59.918+01:00  INFO 22331 --- [JourneyMate] [  restartedMain] o.h.c.internal.RegionFactoryInitiator    : HHH000026: Second-level cache disabled
2026-01-27T10:50:00.086+01:00  INFO 22331 --- [JourneyMate] [  restartedMain] o.s.o.j.p.SpringPersistenceUnitInfo      : No LoadTimeWeaver setup: ignoring JPA class transformer
2026-01-27T10:50:00.103+01:00  INFO 22331 --- [JourneyMate] [  restartedMain] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2026-01-27T10:50:01.180+01:00 ERROR 22331 --- [JourneyMate] [  restartedMain] com.zaxxer.hikari.pool.HikariPool        : HikariPool-1 - Exception during pool initialization.

org.postgresql.util.PSQLException: FATAL: database "journeymate" does not exist
	at org.postgresql.core.v3.QueryExecutorImpl.receiveErrorResponse(QueryExecutorImpl.java:2713) ~[postgresql-42.6.2.jar:42.6.2]
	at org.postgresql.core.v3.QueryExecutorImpl.readStartupMessages(QueryExecutorImpl.java:2825) ~[postgresql-42.6.2.jar:42.6.2]
	at org.postgresql.core.v3.QueryExecutorImpl.<init>(QueryExecutorImpl.java:175) ~[postgresql-42.6.2.jar:42.6.2]
	at org.postgresql.core.v3.ConnectionFactoryImpl.openConnectionImpl(ConnectionFactoryImpl.java:313) ~[postgresql-42.6.2.jar:42.6.2]
	at org.postgresql.core.ConnectionFactory.openConnection(ConnectionFactory.java:54) ~[postgresql-42.6.2.jar:42.6.2]
	at org.postgresql.jdbc.PgConnection.<init>(PgConnection.java:263) ~[postgresql-42.6.2.jar:42.6.2]
	at org.postgresql.Driver.makeConnection(Driver.java:443) ~[postgresql-42.6.2.jar:42.6.2]
	at org.postgresql.Driver.connect(Driver.java:297) ~[postgresql-42.6.2.jar:42.6.2]
	at com.zaxxer.hikari.util.DriverDataSource.getConnection(DriverDataSource.java:138) ~[HikariCP-5.0.1.jar:na]
	at com.zaxxer.hikari.pool.PoolBase.newConnection(PoolBase.java:359) ~[HikariCP-5.0.1.jar:na]
	at com.zaxxer.hikari.pool.PoolBase.newPoolEntry(PoolBase.java:201) ~[HikariCP-5.0.1.jar:na]
	at com.zaxxer.hikari.pool.HikariPool.createPoolEntry(HikariPool.java:470) ~[HikariCP-5.0.1.jar:na]
	at com.zaxxer.hikari.pool.HikariPool.checkFailFast(HikariPool.java:561) ~[HikariCP-5.0.1.jar:na]
	at com.zaxxer.hikari.pool.HikariPool.<init>(HikariPool.java:100) ~[HikariCP-5.0.1.jar:na]
	at com.zaxxer.hikari.HikariDataSource.getConnection(HikariDataSource.java:112) ~[HikariCP-5.0.1.jar:na]
	at org.hibernate.engine.jdbc.connections.internal.DatasourceConnectionProviderImpl.getConnection(DatasourceConnectionProviderImpl.java:122) ~[hibernate-core-6.4.4.Final.jar:6.4.4.Final]
	at org.hibernate.engine.jdbc.env.internal.JdbcEnvironmentInitiator$ConnectionProviderJdbcConnectionAccess.obtainConnection(JdbcEnvironmentInitiator.java:428) ~[hibernate-core-6.4.4.Final.jar:6.4.4.Final]
	at org.hibernate.resource.transaction.backend.jdbc.internal.JdbcIsolationDelegate.delegateWork(JdbcIsolationDelegate.java:61) ~[hibernate-core-6.4.4.Final.jar:6.4.4.Final]
	at org.hibernate.engine.jdbc.env.internal.JdbcEnvironmentInitiator.getJdbcEnvironmentUsingJdbcMetadata(JdbcEnvironmentInitiator.java:276) ~[hibernate-core-6.4.4.Final.jar:6.4.4.Final]
	at org.hibernate.engine.jdbc.env.internal.JdbcEnvironmentInitiator.initiateService(JdbcEnvironmentInitiator.java:107) ~[hibernate-core-6.4.4.Final.jar:6.4.4.Final]
	at org.hibernate.engine.jdbc.env.internal.JdbcEnvironmentInitiator.initiateService(JdbcEnvironmentInitiator.java:68) ~[hibernate-core-6.4.4.Final.jar:6.4.4.Final]
	at org.hibernate.boot.registry.internal.StandardServiceRegistryImpl.initiateService(StandardServiceRegistryImpl.java:130) ~[hibernate-core-6.4.4.Final.jar:6.4.4.Final]
	at org.hibernate.service.internal.AbstractServiceRegistryImpl.createService(AbstractServiceRegistryImpl.java:263) ~[hibernate-core-6.4.4.Final.jar:6.4.4.Final]
	at org.hibernate.service.internal.AbstractServiceRegistryImpl.initializeService(AbstractServiceRegistryImpl.java:238) ~[hibernate-core-6.4.4.Final.jar:6.4.4.Final]
	at org.hibernate.service.internal.AbstractServiceRegistryImpl.getService(AbstractServiceRegistryImpl.java:215) ~[hibernate-core-6.4.4.Final.jar:6.4.4.Final]
	at org.hibernate.boot.model.relational.Database.<init>(Database.java:45) ~[hibernate-core-6.4.4.Final.jar:6.4.4.Final]
	at org.hibernate.boot.internal.InFlightMetadataCollectorImpl.getDatabase(InFlightMetadataCollectorImpl.java:223) ~[hibernate-core-6.4.4.Final.jar:6.4.4.Final]
	at org.hibernate.boot.internal.InFlightMetadataCollectorImpl.<init>(InFlightMetadataCollectorImpl.java:191) ~[hibernate-core-6.4.4.Final.jar:6.4.4.Final]
	at org.hibernate.boot.model.process.spi.MetadataBuildingProcess.complete(MetadataBuildingProcess.java:170) ~[hibernate-core-6.4.4.Final.jar:6.4.4.Final]
	at org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl.metadata(EntityManagerFactoryBuilderImpl.java:1432) ~[hibernate-core-6.4.4.Final.jar:6.4.4.Final]
	at org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl.build(EntityManagerFactoryBuilderImpl.java:1503) ~[hibernate-core-6.4.4.Final.jar:6.4.4.Final]
	at org.springframework.orm.jpa.vendor.SpringHibernateJpaPersistenceProvider.createContainerEntityManagerFactory(SpringHibernateJpaPersistenceProvider.java:75) ~[spring-orm-6.1.6.jar:6.1.6]
	at org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean.createNativeEntityManagerFactory(LocalContainerEntityManagerFactoryBean.java:390) ~[spring-orm-6.1.6.jar:6.1.6]
	at org.springframework.orm.jpa.AbstractEntityManagerFactoryBean.buildNativeEntityManagerFactory(AbstractEntityManagerFactoryBean.java:409) ~[spring-orm-6.1.6.jar:6.1.6]
	at org.springframework.orm.jpa.AbstractEntityManagerFactoryBean.afterPropertiesSet(AbstractEntityManagerFactoryBean.java:396) ~[spring-orm-6.1.6.jar:6.1.6]
	at org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean.afterPropertiesSet(LocalContainerEntityManagerFactoryBean.java:366) ~[spring-orm-6.1.6.jar:6.1.6]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.invokeInitMethods(AbstractAutowireCapableBeanFactory.java:1833) ~[spring-beans-6.1.6.jar:6.1.6]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.initializeBean(AbstractAutowireCapableBeanFactory.java:1782) ~[spring-beans-6.1.6.jar:6.1.6]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:600) ~[spring-beans-6.1.6.jar:6.1.6]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:522) ~[spring-beans-6.1.6.jar:6.1.6]
	at org.springframework.beans.factory.support.AbstractBeanFactory.lambda$doGetBean$0(AbstractBeanFactory.java:326) ~[spring-beans-6.1.6.jar:6.1.6]
	at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:234) ~[spring-beans-6.1.6.jar:6.1.6]
	at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:324) ~[spring-beans-6.1.6.jar:6.1.6]
	at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:200) ~[spring-beans-6.1.6.jar:6.1.6]
	at org.springframework.context.support.AbstractApplicationContext.getBean(AbstractApplicationContext.java:1234) ~[spring-context-6.1.6.jar:6.1.6]
	at org.springframework.context.support.AbstractApplicationContext.finishBeanFactoryInitialization(AbstractApplicationContext.java:952) ~[spring-context-6.1.6.jar:6.1.6]
	at org.springframework.context.support.AbstractApplicationContext.refresh(AbstractApplicationContext.java:624) ~[spring-context-6.1.6.jar:6.1.6]
	at org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext.refresh(ServletWebServerApplicationContext.java:146) ~[spring-boot-3.2.5.jar:3.2.5]
	at org.springframework.boot.SpringApplication.refresh(SpringApplication.java:754) ~[spring-boot-3.2.5.jar:3.2.5]
	at org.springframework.boot.SpringApplication.refreshContext(SpringApplication.java:456) ~[spring-boot-3.2.5.jar:3.2.5]
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:334) ~[spring-boot-3.2.5.jar:3.2.5]
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:1354) ~[spring-boot-3.2.5.jar:3.2.5]
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:1343) ~[spring-boot-3.2.5.jar:3.2.5]
	at com.example.JourneyMate.JourneyMateApplication.main(JourneyMateApplication.java:11) ~[classes/:na]
	at java.base/jdk.internal.reflect.DirectMethodHandleAccessor.invoke(DirectMethodHandleAccessor.java:103) ~[na:na]
	at java.base/java.lang.reflect.Method.invoke(Method.java:580) ~[na:na]
	at org.springframework.boot.devtools.restart.RestartLauncher.run(RestartLauncher.java:50) ~[spring-boot-devtools-3.2.5.jar:3.2.5]

2026-01-27T10:50:01.188+01:00  WARN 22331 --- [JourneyMate] [  restartedMain] o.h.e.j.e.i.JdbcEnvironmentInitiator     : HHH000342: Could not obtain connection to query metadata

java.lang.NullPointerException: Cannot invoke "org.hibernate.engine.jdbc.spi.SqlExceptionHelper.convert(java.sql.SQLException, String)" because the return value of "org.hibernate.resource.transaction.backend.jdbc.internal.JdbcIsolationDelegate.sqlExceptionHelper()" is null
	at org.hibernate.resource.transaction.backend.jdbc.internal.JdbcIsolationDelegate.delegateWork(JdbcIsolationDelegate.java:116) ~[hibernate-core-6.4.4.Final.jar:6.4.4.Final]
	at org.hibernate.engine.jdbc.env.internal.JdbcEnvironmentInitiator.getJdbcEnvironmentUsingJdbcMetadata(JdbcEnvironmentInitiator.java:276) ~[hibernate-core-6.4.4.Final.jar:6.4.4.Final]
	at org.hibernate.engine.jdbc.env.internal.JdbcEnvironmentInitiator.initiateService(JdbcEnvironmentInitiator.java:107) ~[hibernate-core-6.4.4.Final.jar:6.4.4.Final]
	at org.hibernate.engine.jdbc.env.internal.JdbcEnvironmentInitiator.initiateService(JdbcEnvironmentInitiator.java:68) ~[hibernate-core-6.4.4.Final.jar:6.4.4.Final]
	at org.hibernate.boot.registry.internal.StandardServiceRegistryImpl.initiateService(StandardServiceRegistryImpl.java:130) ~[hibernate-core-6.4.4.Final.jar:6.4.4.Final]
	at org.hibernate.service.internal.AbstractServiceRegistryImpl.createService(AbstractServiceRegistryImpl.java:263) ~[hibernate-core-6.4.4.Final.jar:6.4.4.Final]
	at org.hibernate.service.internal.AbstractServiceRegistryImpl.initializeService(AbstractServiceRegistryImpl.java:238) ~[hibernate-core-6.4.4.Final.jar:6.4.4.Final]
	at org.hibernate.service.internal.AbstractServiceRegistryImpl.getService(AbstractServiceRegistryImpl.java:215) ~[hibernate-core-6.4.4.Final.jar:6.4.4.Final]
	at org.hibernate.boot.model.relational.Database.<init>(Database.java:45) ~[hibernate-core-6.4.4.Final.jar:6.4.4.Final]
	at org.hibernate.boot.internal.InFlightMetadataCollectorImpl.getDatabase(InFlightMetadataCollectorImpl.java:223) ~[hibernate-core-6.4.4.Final.jar:6.4.4.Final]
	at org.hibernate.boot.internal.InFlightMetadataCollectorImpl.<init>(InFlightMetadataCollectorImpl.java:191) ~[hibernate-core-6.4.4.Final.jar:6.4.4.Final]
	at org.hibernate.boot.model.process.spi.MetadataBuildingProcess.complete(MetadataBuildingProcess.java:170) ~[hibernate-core-6.4.4.Final.jar:6.4.4.Final]
	at org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl.metadata(EntityManagerFactoryBuilderImpl.java:1432) ~[hibernate-core-6.4.4.Final.jar:6.4.4.Final]
	at org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl.build(EntityManagerFactoryBuilderImpl.java:1503) ~[hibernate-core-6.4.4.Final.jar:6.4.4.Final]
	at org.springframework.orm.jpa.vendor.SpringHibernateJpaPersistenceProvider.createContainerEntityManagerFactory(SpringHibernateJpaPersistenceProvider.java:75) ~[spring-orm-6.1.6.jar:6.1.6]
	at org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean.createNativeEntityManagerFactory(LocalContainerEntityManagerFactoryBean.java:390) ~[spring-orm-6.1.6.jar:6.1.6]
	at org.springframework.orm.jpa.AbstractEntityManagerFactoryBean.buildNativeEntityManagerFactory(AbstractEntityManagerFactoryBean.java:409) ~[spring-orm-6.1.6.jar:6.1.6]
	at org.springframework.orm.jpa.AbstractEntityManagerFactoryBean.afterPropertiesSet(AbstractEntityManagerFactoryBean.java:396) ~[spring-orm-6.1.6.jar:6.1.6]
	at org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean.afterPropertiesSet(LocalContainerEntityManagerFactoryBean.java:366) ~[spring-orm-6.1.6.jar:6.1.6]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.invokeInitMethods(AbstractAutowireCapableBeanFactory.java:1833) ~[spring-beans-6.1.6.jar:6.1.6]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.initializeBean(AbstractAutowireCapableBeanFactory.java:1782) ~[spring-beans-6.1.6.jar:6.1.6]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:600) ~[spring-beans-6.1.6.jar:6.1.6]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:522) ~[spring-beans-6.1.6.jar:6.1.6]
	at org.springframework.beans.factory.support.AbstractBeanFactory.lambda$doGetBean$0(AbstractBeanFactory.java:326) ~[spring-beans-6.1.6.jar:6.1.6]
	at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:234) ~[spring-beans-6.1.6.jar:6.1.6]
	at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:324) ~[spring-beans-6.1.6.jar:6.1.6]
	at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:200) ~[spring-beans-6.1.6.jar:6.1.6]
	at org.springframework.context.support.AbstractApplicationContext.getBean(AbstractApplicationContext.java:1234) ~[spring-context-6.1.6.jar:6.1.6]
	at org.springframework.context.support.AbstractApplicationContext.finishBeanFactoryInitialization(AbstractApplicationContext.java:952) ~[spring-context-6.1.6.jar:6.1.6]
	at org.springframework.context.support.AbstractApplicationContext.refresh(AbstractApplicationContext.java:624) ~[spring-context-6.1.6.jar:6.1.6]
	at org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext.refresh(ServletWebServerApplicationContext.java:146) ~[spring-boot-3.2.5.jar:3.2.5]
	at org.springframework.boot.SpringApplication.refresh(SpringApplication.java:754) ~[spring-boot-3.2.5.jar:3.2.5]
	at org.springframework.boot.SpringApplication.refreshContext(SpringApplication.java:456) ~[spring-boot-3.2.5.jar:3.2.5]
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:334) ~[spring-boot-3.2.5.jar:3.2.5]
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:1354) ~[spring-boot-3.2.5.jar:3.2.5]
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:1343) ~[spring-boot-3.2.5.jar:3.2.5]
	at com.example.JourneyMate.JourneyMateApplication.main(JourneyMateApplication.java:11) ~[classes/:na]
	at java.base/jdk.internal.reflect.DirectMethodHandleAccessor.invoke(DirectMethodHandleAccessor.java:103) ~[na:na]
	at java.base/java.lang.reflect.Method.invoke(Method.java:580) ~[na:na]
	at org.springframework.boot.devtools.restart.RestartLauncher.run(RestartLauncher.java:50) ~[spring-boot-devtools-3.2.5.jar:3.2.5]

2026-01-27T10:50:01.218+01:00  WARN 22331 --- [JourneyMate] [  restartedMain] org.hibernate.orm.deprecation            : HHH90000025: PostgreSQLDialect does not need to be specified explicitly using 'hibernate.dialect' (remove the property setting and it will be selected by default)
2026-01-27T10:50:01.505+01:00 ERROR 22331 --- [JourneyMate] [  restartedMain] j.LocalContainerEntityManagerFactoryBean : Failed to initialize JPA EntityManagerFactory: Entity 'com.example.JourneyMate.entity.service_type.CruceroEntity' has no identifier (every '@Entity' class must declare or inherit at least one '@Id' or '@EmbeddedId' property)
2026-01-27T10:50:01.506+01:00  WARN 22331 --- [JourneyMate] [  restartedMain] ConfigServletWebServerApplicationContext : Exception encountered during context initialization - cancelling refresh attempt: org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'entityManagerFactory' defined in class path resource [org/springframework/boot/autoconfigure/orm/jpa/HibernateJpaConfiguration.class]: Entity 'com.example.JourneyMate.entity.service_type.CruceroEntity' has no identifier (every '@Entity' class must declare or inherit at least one '@Id' or '@EmbeddedId' property)
2026-01-27T10:50:01.512+01:00  INFO 22331 --- [JourneyMate] [  restartedMain] o.apache.catalina.core.StandardService   : Stopping service [Tomcat]
2026-01-27T10:50:01.532+01:00  INFO 22331 --- [JourneyMate] [  restartedMain] .s.b.a.l.ConditionEvaluationReportLogger : 

Error starting ApplicationContext. To display the condition evaluation report re-run your application with 'debug' enabled.
2026-01-27T10:50:01.555+01:00 ERROR 22331 --- [JourneyMate] [  restartedMain] o.s.boot.SpringApplication               : Application run failed

org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'entityManagerFactory' defined in class path resource [org/springframework/boot/autoconfigure/orm/jpa/HibernateJpaConfiguration.class]: Entity 'com.example.JourneyMate.entity.service_type.CruceroEntity' has no identifier (every '@Entity' class must declare or inherit at least one '@Id' or '@EmbeddedId' property)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.initializeBean(AbstractAutowireCapableBeanFactory.java:1786) ~[spring-beans-6.1.6.jar:6.1.6]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:600) ~[spring-beans-6.1.6.jar:6.1.6]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:522) ~[spring-beans-6.1.6.jar:6.1.6]
	at org.springframework.beans.factory.support.AbstractBeanFactory.lambda$doGetBean$0(AbstractBeanFactory.java:326) ~[spring-beans-6.1.6.jar:6.1.6]
	at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:234) ~[spring-beans-6.1.6.jar:6.1.6]
	at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:324) ~[spring-beans-6.1.6.jar:6.1.6]
	at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:200) ~[spring-beans-6.1.6.jar:6.1.6]
	at org.springframework.context.support.AbstractApplicationContext.getBean(AbstractApplicationContext.java:1234) ~[spring-context-6.1.6.jar:6.1.6]
	at org.springframework.context.support.AbstractApplicationContext.finishBeanFactoryInitialization(AbstractApplicationContext.java:952) ~[spring-context-6.1.6.jar:6.1.6]
	at org.springframework.context.support.AbstractApplicationContext.refresh(AbstractApplicationContext.java:624) ~[spring-context-6.1.6.jar:6.1.6]
	at org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext.refresh(ServletWebServerApplicationContext.java:146) ~[spring-boot-3.2.5.jar:3.2.5]
	at org.springframework.boot.SpringApplication.refresh(SpringApplication.java:754) ~[spring-boot-3.2.5.jar:3.2.5]
	at org.springframework.boot.SpringApplication.refreshContext(SpringApplication.java:456) ~[spring-boot-3.2.5.jar:3.2.5]
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:334) ~[spring-boot-3.2.5.jar:3.2.5]
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:1354) ~[spring-boot-3.2.5.jar:3.2.5]
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:1343) ~[spring-boot-3.2.5.jar:3.2.5]
	at com.example.JourneyMate.JourneyMateApplication.main(JourneyMateApplication.java:11) ~[classes/:na]
	at java.base/jdk.internal.reflect.DirectMethodHandleAccessor.invoke(DirectMethodHandleAccessor.java:103) ~[na:na]
	at java.base/java.lang.reflect.Method.invoke(Method.java:580) ~[na:na]
	at org.springframework.boot.devtools.restart.RestartLauncher.run(RestartLauncher.java:50) ~[spring-boot-devtools-3.2.5.jar:3.2.5]
Caused by: org.hibernate.AnnotationException: Entity 'com.example.JourneyMate.entity.service_type.CruceroEntity' has no identifier (every '@Entity' class must declare or inherit at least one '@Id' or '@EmbeddedId' property)
	at org.hibernate.boot.model.internal.InheritanceState.determineDefaultAccessType(InheritanceState.java:279) ~[hibernate-core-6.4.4.Final.jar:6.4.4.Final]
	at org.hibernate.boot.model.internal.InheritanceState.getElementsToProcess(InheritanceState.java:215) ~[hibernate-core-6.4.4.Final.jar:6.4.4.Final]
	at org.hibernate.boot.model.internal.InheritanceState.postProcess(InheritanceState.java:160) ~[hibernate-core-6.4.4.Final.jar:6.4.4.Final]
	at org.hibernate.boot.model.internal.EntityBinder.handleIdentifier(EntityBinder.java:348) ~[hibernate-core-6.4.4.Final.jar:6.4.4.Final]
	at org.hibernate.boot.model.internal.EntityBinder.bindEntityClass(EntityBinder.java:237) ~[hibernate-core-6.4.4.Final.jar:6.4.4.Final]
	at org.hibernate.boot.model.internal.AnnotationBinder.bindClass(AnnotationBinder.java:423) ~[hibernate-core-6.4.4.Final.jar:6.4.4.Final]
	at org.hibernate.boot.model.source.internal.annotations.AnnotationMetadataSourceProcessorImpl.processEntityHierarchies(AnnotationMetadataSourceProcessorImpl.java:256) ~[hibernate-core-6.4.4.Final.jar:6.4.4.Final]
	at org.hibernate.boot.model.process.spi.MetadataBuildingProcess$1.processEntityHierarchies(MetadataBuildingProcess.java:279) ~[hibernate-core-6.4.4.Final.jar:6.4.4.Final]
	at org.hibernate.boot.model.process.spi.MetadataBuildingProcess.complete(MetadataBuildingProcess.java:322) ~[hibernate-core-6.4.4.Final.jar:6.4.4.Final]
	at org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl.metadata(EntityManagerFactoryBuilderImpl.java:1432) ~[hibernate-core-6.4.4.Final.jar:6.4.4.Final]
	at org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl.build(EntityManagerFactoryBuilderImpl.java:1503) ~[hibernate-core-6.4.4.Final.jar:6.4.4.Final]
	at org.springframework.orm.jpa.vendor.SpringHibernateJpaPersistenceProvider.createContainerEntityManagerFactory(SpringHibernateJpaPersistenceProvider.java:75) ~[spring-orm-6.1.6.jar:6.1.6]
	at org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean.createNativeEntityManagerFactory(LocalContainerEntityManagerFactoryBean.java:390) ~[spring-orm-6.1.6.jar:6.1.6]
	at org.springframework.orm.jpa.AbstractEntityManagerFactoryBean.buildNativeEntityManagerFactory(AbstractEntityManagerFactoryBean.java:409) ~[spring-orm-6.1.6.jar:6.1.6]
	at org.springframework.orm.jpa.AbstractEntityManagerFactoryBean.afterPropertiesSet(AbstractEntityManagerFactoryBean.java:396) ~[spring-orm-6.1.6.jar:6.1.6]
	at org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean.afterPropertiesSet(LocalContainerEntityManagerFactoryBean.java:366) ~[spring-orm-6.1.6.jar:6.1.6]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.invokeInitMethods(AbstractAutowireCapableBeanFactory.java:1833) ~[spring-beans-6.1.6.jar:6.1.6]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.initializeBean(AbstractAutowireCapableBeanFactory.java:1782) ~[spring-beans-6.1.6.jar:6.1.6]
	... 19 common frames omitted


Process finished with exit code 0
