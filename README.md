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
Más APIs para utilizar
APIs de motores de búsqueda o agencias de viajes
- Kiwi (Tequila API) – motor de búsqueda de vuelos muy potente que combina rutas de muchas aerolíneas, incluso low-cost, y permite hacer búsquedas complejas de itinerarios.

- Kayak API – API de búsqueda de vuelos, hoteles y coches (según algunas fuentes está disponible o a través de partners).

- Expedia API – ofrece datos de vuelos y disponibilidad, parte de un ecosistema grande de viajes.

APIs de distribución global (GDS) y proveedores algo más serios
- Amadeus Flight APIs – muy usado en la industria, permite búsqueda de ofertas de vuelos, precios, etc. Tiene sandbox gratuito y cuotas mensuales.

- Sabre APIs – otro GDS mundial que da acceso a datos de vuelos (schedules, tarifas, disponibilidad).

APIs de datos de aviación (no tan comercial como SkyScanner)

 datos de vuelos en tiempo real o rutas aunque no siempre tienen precios:
- Aviationstack / AirLabs Data API – ofrecen datos de vuelos, aerolíneas, aeropuertos y del estado del vuelo (aunque no precios comerciales en todos los casos

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
