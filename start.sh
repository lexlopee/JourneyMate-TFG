#!/bin/bash

while true; do
    clear
    OPCION=$(gum choose \
        "Arrancar Web" \
        "Arrancar Móvil" \
        "Arrancar Todo" \
        "Parar Servicios" \
        "Dar Permisos" \
        "Salir")

    case "$OPCION" in
        "Arrancar Web")
            ./start_web.sh
            gum confirm "Volver al menú" && continue
            ;;

        "Arrancar Móvil")
            ./start_mobile.sh
            gum confirm "Volver al menú" && continue
            ;;

        "Arrancar Todo")
            ./start_all.sh
            gum confirm "Volver al menú" && continue
            ;;

        "Parar Servicios")
            echo "Parando Docker..."
            cd JourneyMate
            docker compose down
            cd ..

            echo "Matando Vite..."
            pkill -f "vite"

            echo "Matando Flutter..."
            pkill -f "flutter"

            echo "Matando Emulador..."
            pkill -f "qemu-system"

            gum confirm "Servicios detenidos. Volver al menú" && continue
            ;;

        "Dar Permisos")
            chmod +x *.sh
            gum confirm "Permisos aplicados. Volver al menú" && continue
            ;;

        "Salir")
            gum style --foreground 212 "Saliendo del launcher. ¡Hasta luego!"
            exit 0
            ;;
    esac
done
