#!/bin/bash

echo "Arrancando Docker..."
cd JourneyMate
docker compose up -d
cd ..

echo "Buscando emuladores disponibles..."

# Obtener el primer emulador real usando emulator -list-avds
EMULATOR_ID=$(emulator -list-avds | head -n 1)

if [ -z "$EMULATOR_ID" ]; then
  echo "❌ No se encontraron emuladores instalados."
  exit 1
fi

echo "Emulador encontrado: $EMULATOR_ID"

echo "Comprobando si hay un emulador Android encendido..."
if ! flutter devices | grep -q "android"; then
  echo "No hay emulador encendido. Arrancando $EMULATOR_ID..."
  emulator -avd "$EMULATOR_ID" &
  sleep 10
fi

echo "Esperando a que Android termine de arrancar..."
until adb shell pm list packages >/dev/null 2>&1; do
  echo "Android aún no está listo... esperando 2 segundos"
  sleep 2
done

echo "Android está listo. Arrancando Flutter..."
cd journeymate_mobile
flutter run
