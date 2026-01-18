# Aplicaciones y Plugins

# Cosas importantes
Si hay Paginas Web en las cuales tenemos que iniciar sesion, lo suyo seria poner una cuenta conjunta o crear una cuenta para usarla solo para ello.

# Ejecuccion del proyecto 
mvn spring-boot:run


# Creacion de la Base de Datos
Mirar como crear esto

# Android Studio
Iniciamos el Android Studio y nos dirigimos al apartado de Plugins y le tenemos que poner esto en el buscador. 

![alt text](image.png) 


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


