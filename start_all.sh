#!/bin/bash

echo "Arrancando Docker (backend + base de datos)..."
cd JourneyMate
docker compose up -d
cd ..

echo "Arrancando React (Vite)..."
cd journeymate-frontend
npm run dev &
cd ..

echo "Arrancando Flutter en emulador Android..."
cd journeymate_mobile
flutter run -d android &
cd ..

echo "Arrancado correctamente"
