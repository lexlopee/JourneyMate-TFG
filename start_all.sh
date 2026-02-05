#!/bin/bash

echo "Arrancando Docker (backend + base de datos)..."
cd JourneyMate
docker compose up -d
cd ..

echo "Arrancando React (Vite)..."
cd journeymate-frontend

# Ejecutar Vite y capturar su salida para abrir el navegador automáticamente
npm run dev | while read line; do
    echo "$line"

    # Buscar la línea que contiene la URL local
    if echo "$line" | grep -q "Local:"; then
        URL=$(echo "$line" | grep -o 'http://localhost:[0-9]\+')
        echo "Abriendo navegador en: $URL"
        xdg-open "$URL"
    fi
done &

cd ..

echo "Arrancando Flutter en emulador Android..."
cd journeymate_mobile

# Detectar el primer emulador disponible
EMULATOR_ID=$(emulator -list-avds | head -n 1)

if [ -z "$EMULATOR_ID" ]; then
  echo "❌ No se encontraron emuladores instalados."
else
  # Comprobar si ya hay un dispositivo Android conectado
  if ! flutter devices | grep -q "android"; then
    echo "No hay emulador encendido. Arrancando $EMULATOR_ID..."
    emulator -avd "$EMULATOR_ID" &
    sleep 10
  fi
fi

# Esperar a que Android esté listo
echo "Esperando a que Android termine de arrancar..."
until adb shell pm list packages >/dev/null 2>&1; do
  echo "Android aún no está listo... esperando 2 segundos"
  sleep 2
done

echo "Android está listo. Arrancando Flutter..."
flutter run &

cd ..

echo "Todo arrancado correctamente."
