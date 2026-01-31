/*1.*/ 
	CREATE USER journeymate_bbdd WITH PASSWORD 'journeymate-bbdd';

/*2.*/
	CREATE DATABASE journeymate_bbdd
    WITH OWNER = journeymate_bbdd
    ENCODING = 'UTF8'
    CONNECTION LIMIT = -1;
/*3.
 * Conectar con la Base de datos con estos datos-->  
 *
 * Base de datos: journeymate_bbdd
 * Usuario: journeymate_bbdd
 * Contraseña: journeymate-bbdd
 */
 
/*4.*/ 
   CREATE SCHEMA journeymate AUTHORIZATION journeymate_bbdd;
