-- Crear el usuario si no existe (opcional)
-- CREATE USER journeymate_bbdd WITH PASSWORD 'tu_password';

-- Crear el schema
DROP SCHEMA IF EXISTS journeymate CASCADE;
CREATE SCHEMA journeymate AUTHORIZATION journeymate_bbdd;
