#!/bin/bash

echo "Arrancando Docker..."
cd JourneyMate
docker compose up -d
cd ..

echo "Arrancando React (Vite)..."
cd journeymate-frontend
npm run dev
