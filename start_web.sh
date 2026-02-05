#!/bin/bash

echo "Arrancando Docker..."
cd JourneyMate
docker compose up -d
cd ..

echo "Arrancando React (Vite)..."
cd journeymate-frontend

# Ejecutar Vite y capturar su salida
npm run dev | while read line; do
    echo "$line"

    # Buscar la línea que contiene la URL local
    if echo "$line" | grep -q "Local:"; then
        # Extraer la URL
        URL=$(echo "$line" | grep -o 'http://localhost:[0-9]\+')
        echo "Abriendo navegador en: $URL"
        xdg-open "$URL"
    fi
done
