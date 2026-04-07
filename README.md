# JourneyMate – Plataforma Turística Inteligente
JourneyMate es una plataforma turística multiplataforma (web y móvil) desarrollada como Trabajo Fin de Grado.
Permite a los usuarios buscar, comparar y reservar servicios turísticos desde una única aplicación, integrando mapas interactivos, rutas personalizadas, comparador de precios y autenticación segura.

## Descripción del proyecto
JourneyMate centraliza todo el proceso de planificación de viajes: búsqueda de vuelos, hoteles, actividades, rutas turísticas y puntos de interés.
El sistema está compuesto por una aplicación web (React), una aplicación móvil (Flutter) y un backend REST desarrollado con Spring Boot, conectado a una base de datos PostgreSQL y a múltiples APIs externas.

El objetivo es ofrecer una experiencia moderna, rápida y personalizada, permitiendo al usuario gestionar sus viajes de forma sencilla y eficiente.

## Características principales
- Registro y autenticación con JWT
- Gestión de usuarios
- Gestión de reservas
- Comparador de precios (vuelos, hoteles, actividades…)
- Mapas interactivos (Google Maps API)
- Rutas turísticas personalizadas
- Aplicación web (React + Vite)
- Aplicación móvil (Flutter)
- Backend REST con Spring Boot
- Integración con APIs externas
- Pagos con PayPal y Stripe
- Envío de correos (Spring Mail)
- Sistema de arranque automático mediante scripts .bat

## Arquitectura del sistema
A continuación se muestra la arquitectura general del sistema:
<img width="1222" height="809" alt="Captura de pantalla 2026-04-07 234752" src="https://github.com/user-attachments/assets/575e274b-7a10-4ec0-818a-59368c56d884" />

### Capas del backend
- Controller → Endpoints REST
- Service → Lógica de negocio
- Repository (DAO) → Acceso a la base de datos
- Entity → Modelos JPA
- DTO → Transferencia de datos
- Config → Seguridad, CORS, pagos, dotenv
- External → Integración con APIs externas
- Exception → Manejo global de errores

## Modelo de datos
Este es el modelo de datos utilizado por JourneyMate:
<img width="2571" height="2314" alt="Diagrama_Estructura_TFG drawio" src="https://github.com/user-attachments/assets/fe67c080-e17b-47cf-b9e2-ae23e49c2625" />

### Entidades principales
- Usuario
- Reserva
- Pago
- Ruta
- Actividad
- Punto de interés
- Recomendación
- Historial de búsqueda
- Preferencias
### Relaciones destacadas
- Un usuario puede realizar muchas reservas
- Un usuario puede recibir recomendaciones
- Una ruta contiene varios puntos de interés
- Un pago corresponde a una reserva
- Un usuario tiene preferencias que afectan a las recomendaciones

## Tecnologías destacadas
### Backend              
- Java 21
- Spring Boot 3.2.5
- Spring Web
- Spring Data JPA
- Spring Security
- Bean Validation
- PostgreSQL
- Lombok
- Swagger / OpenAPI
- Actuator
- Java Dotenv
- Spring Mail
### Autenticación
- JJWT (API, Impl, Jackson)
### Pagos
- PayPal SDK
- Stripe Java SDK
### Frontend Web
- React
- Vite
- TypeScript
### Aplicación móvil
- Flutter
- Dart
### Base de datos
- PostgreSQL
- Docker
### APIs externas
- Google Maps
- RapidAPI (vuelos, hoteles, actividades…)

## Estructura del repositorio

<img width="583" height="122" alt="image" src="https://github.com/user-attachments/assets/0f6a5f74-a66e-4cdf-ab59-80666c47ad30" />


## Instalación y ejecución (manual)
JourneyMate incluye un sistema de arranque automático mediante scripts .bat.
Esta sección muestra la ejecución manual para desarrolladores o entornos sin launcher.

### Backend + Base de datos
- cd JourneyMate
- docker compose up -d
### Frontend Web
- cd journeymate-frontend
- npm install
- npm run dev
### Aplicación móvil
- cd journeymate_mobile
- flutter run -d android
## Sistema de arranque automático (Launcher)
JourneyMate incluye un launcher desarrollado en Visual Studio Community que permite iniciar cualquier parte del proyecto sin necesidad de ejecutar comandos manuales.
### Ubicación de los scripts
- JourneyMate-TFG\JourneyMateLauncher\JourneyMateLauncher\scripts\windows
### Opciones del menú
- Arrancar Backend
- Arrancar Web
- Arrancar Móvil
- Arrancar Todo
- Parar Servicios
- Salir
### Ventajas
- Automatiza la ejecución del proyecto completo
- Evita errores de configuración
- Facilita la demostración del TFG
- Permite arrancar cada módulo por separado
- Comprueba que Docker, PostgreSQL y Vite están listos antes de continuar
<img width="266" height="548" alt="image" src="https://github.com/user-attachments/assets/12796253-407a-4a79-a57f-b73a0bc12774" />

## API REST (resumen)
| Método | Endpoint | Descripción |
| --- | --- | --- |
| POST | /auth/login | Login |
| POST | /auth/register | Registro |
| GET | /routes | Listar rutas |
| POST | /booking | Crear reserva |
| GET | /interest | Puntos de interés |

## Equipo de desarrollo
Integrante 1 (Alejandro López) → Backend base + BD + CRUD

Integrante 2 (Daniel Fernández) → Seguridad + JWT + Usuarios

Integrante 3 (Marcos Hernández) → APIs externas + IA + Pagos

## Estado del proyecto
Proyecto desarrollado como Trabajo Fin de Grado.
Actualmente en fase de mejora y ampliación.
## Licencia
Este proyecto se distribuye únicamente con fines académicos como parte del Trabajo Fin de Grado.
