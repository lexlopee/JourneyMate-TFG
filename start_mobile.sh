#!/bin/bash

echo "Arrancando Docker..."
cd JourneyMate
docker compose up -d
cd ..

echo "Arrancando Flutter en emulador Android..."
cd journeymate_mobile
flutter run -d android
