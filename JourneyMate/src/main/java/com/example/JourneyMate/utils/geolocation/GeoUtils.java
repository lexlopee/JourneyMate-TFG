package com.example.JourneyMate.utils.geolocation;

import lombok.experimental.UtilityClass;

/**
 * Utilidades para cálculos geográficos.
 * Proporciona métodos para operaciones como cálculo de distancia entre coordenadas.
 */
@UtilityClass
public class GeoUtils {

    /**
     * Radio medio de la Tierra en kilómetros.
     */
    private static final double EARTH_RADIUS = 6371.0;

    /**
     * Calcula la distancia entre dos puntos geográficos usando la fórmula de Haversine.
     *
     * @param lat1 latitud del primer punto
     * @param lon1 longitud del primer punto
     * @param lat2 latitud del segundo punto
     * @param lon2 longitud del segundo punto
     * @return distancia en kilómetros entre ambos puntos
     */
    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }
}