--DROP SCHEMA IF EXISTS journeymate CASCADE;
CREATE SCHEMA journeymate AUTHORIZATION journeymate_bbdd;


-- Creación de la Tabla CATEGORIA
CREATE TABLE journeymate.categoria(
    id_categoria INTEGER GENERATED ALWAYS AS IDENTITY,
    nombre VARCHAR (50) NOT NULL,
    CONSTRAINT pk_id_cat PRIMARY KEY (id_categoria)
);


-- Creación de la Tabla ROL
CREATE TABLE journeymate.rol(
	id_rol INTEGER GENERATED ALWAYS AS IDENTITY,
	descripcion VARCHAR (50) NOT NULL,
	nombre VARCHAR(30) NOT NULL,
	CONSTRAINT pk_id_rol PRIMARY KEY (id_rol)
);


-- Creación de la Tabla USUARIO
CREATE TABLE journeymate.usuario(
	id_usuario INTEGER GENERATED ALWAYS AS IDENTITY,
	telefono VARCHAR (13),
	contraseña VARCHAR (30),
	nombre VARCHAR (30),
	primer_apellido VARCHAR (30),
	segundo_apellido VARCHAR (30),
	fecha_registro DATE,
	fecha_nacimiento DATE,
	email VARCHAR (30), 
	id_rol INTEGER NOT NULL,
	CONSTRAINT pk_id_usu PRIMARY KEY (id_usuario),
	CONSTRAINT fk_id_rol FOREIGN KEY (id_rol) REFERENCES journeymate.rol (id_rol)
);


-- Creación de la RUTA
CREATE TABLE journeymate.ruta(
	id_ruta INTEGER GENERATED ALWAYS AS IDENTITY,
	nombre VARCHAR(100) NOT NULL,
	fecha_creacion DATE NOT NULL,
	id_usuario INTEGER NOT NULL,
	CONSTRAINT pk_id_rut PRIMARY KEY (id_ruta),
	CONSTRAINT fk_id_usu FOREIGN KEY (id_usuario) REFERENCES journeymate.usuario (id_usuario)
);


-- Creación de la Tabla PUNTO_INTERES
CREATE TABLE journeymate.punto_interes(
	id_punto INTEGER GENERATED ALWAYS AS IDENTITY,
	ciudad VARCHAR(50) NOT NULL,
	nombre VARCHAR(50),
	latitud NUMERIC(9,6),
	descripcion VARCHAR (50),
	id_categoria INTEGER NOT NULL,
	CONSTRAINT pk_id_pun PRIMARY KEY (id_punto),
	CONSTRAINT fk_id_cate FOREIGN KEY (id_categoria) REFERENCES journeymate.categoria (id_categoria)
);


-- Creación de la Tabla RUTA_PUNTO_INTERES
CREATE TABLE journeymate.ruta_punto_interes (
    id_ruta_punto_interes INTEGER GENERATED ALWAYS AS IDENTITY,
    id_ruta INTEGER NOT NULL,
    id_punto_interes INTEGER NOT NULL,
    orden INTEGER,
    CONSTRAINT pk_id_rut_pun_int PRIMARY KEY (id_ruta_punto_interes),	
    CONSTRAINT fk_id_ruta FOREIGN KEY (id_ruta) REFERENCES journeymate.ruta (id_ruta),
    CONSTRAINT fk_id_punt_inte FOREIGN KEY (id_punto_interes) REFERENCES journeymate.punto_interes(id_punto)
);

-- Creación de la Tabla TOKEN_JWT
CREATE TABLE journeymate.token_jwt(
	id_token INTEGER GENERATED ALWAYS AS IDENTITY, 
	id_usuario INTEGER,
	fecha_expiacion DATE NOT NULL, 
	fecha_creacion DATE NOT NULL, 
	token VARCHAR(100),
	CONSTRAINT pk_id_tok PRIMARY KEY (id_token),
	CONSTRAINT fk_id_usu FOREIGN KEY (id_usuario) REFERENCES journeymate.usuario (id_usuario)
);


-- Creacion de la Tabla TIPO_ELEMENTO
CREATE TABLE journeymate.tipo_elemento(
	id_elemento INTEGER GENERATED ALWAYS AS IDENTITY,
	nombre VARCHAR (30)NOT NULL,
	CONSTRAINT pk_id_elem PRIMARY KEY (id_elemento)
);


-- Creación de la Tabla RECOMENDADCION
CREATE TABLE journeymate.recomendacion(
	id_recomendacion INTEGER GENERATED ALWAYS AS IDENTITY,
	id_elemento INTEGER,
	score NUMERIC (5), 
	id_usuario INTEGER,
	CONSTRAINT pk_id_reco PRIMARY KEY (id_recomendacion),
	CONSTRAINT fk_id_elem FOREIGN KEY (id_elemento) REFERENCES journeymate.tipo_elemento (id_elemento),
	CONSTRAINT fk_id_usu FOREIGN KEY (id_usuario) REFERENCES journeymate.usuario (id_usuario)
);


-- Creación de la Tabla HISTORIAL_BUSQUEDA
CREATE TABLE journeymate.historial_busqueda(
	id_historial INTEGER GENERATED ALWAYS AS IDENTITY,
	fecha DATE,
	termino VARCHAR (30),
	id_usuario INTEGER, 
	CONSTRAINT pk_id_his PRIMARY KEY (id_historial),
	CONSTRAINT fk_id_usu FOREIGN KEY (id_usuario) REFERENCES journeymate.usuario (id_usuario)
);


-- Creación de la Tabla TIPO_PREFERENCIA
CREATE TABLE journeymate.tipo_preferencia(
	id_tipo_preferencia INTEGER GENERATED ALWAYS AS IDENTITY,
	nombre VARCHAR (30) NOT NULL,
	CONSTRAINT pk_id_tip_pre PRIMARY KEY (id_tipo_preferencia)	
);



-- Creacion de la Tabla PREFERENCIAS USUARIO
CREATE TABLE journeymate.preferencias_usuario(
	id_preferencia INTEGER GENERATED ALWAYS AS IDENTITY, 
	id_tipo_preferencia INTEGER, 
	id_usuario INTEGER,
	valor NUMERIC(3),
	CONSTRAINT pk_id_pre PRIMARY KEY (id_preferencia),
	CONSTRAINT fk_id_tip_pre FOREIGN KEY (id_tipo_preferencia) REFERENCES journeymate.tipo_preferencia (id_tipo_preferencia),
	CONSTRAINT fk_id_usu FOREIGN KEY (id_usuario) REFERENCES journeymate.usuario (id_usuario)
);

-- Creación de la Tabla ESTADO
CREATE TABLE journeymate.estado(
	id_estado INTEGER GENERATED ALWAYS AS IDENTITY,
	nombre VARCHAR (30),
	CONSTRAINT pk_id_est PRIMARY KEY (id_estado)
);

-- Creación de la Tabla TIPO_RESERVA
CREATE TABLE journeymate.tipo_reserva(
	id_tipo_reserva INTEGER GENERATED ALWAYS AS IDENTITY,
	nombre VARCHAR (30),
	CONSTRAINT pk_id_tip_res PRIMARY KEY(id_tipo_reserva)
);

-- Creación de la Tabla METODO
CREATE TABLE journeymate.metodo(
	id_metodo INTEGER GENERATED ALWAYS AS IDENTITY,
	nombre VARCHAR (29),
	CONSTRAINT pk_id_metodo PRIMARY KEY (id_metodo)
);


-- Creación de la Tabla SERVICIO_TURISTICO
CREATE TABLE journeymate.servicio_turistico(
    id_servicio INTEGER GENERATED ALWAYS AS IDENTITY,
    nombre VARCHAR(20),
    precio_base NUMERIC(6,2),
    CONSTRAINT pk_id_ser PRIMARY KEY(id_servicio)
);


-- Creacion de la Tabla DIRECCION
CREATE TABLE journeymate.direccion(
	id_direccion INTEGER GENERATED ALWAYS AS IDENTITY,
	id_servicio INTEGER, 
	calle VARCHAR(100),
	numero VARCHAR (10),
	ciudad VARCHAR (50),
	provincia VARCHAR (50),
	pais VARCHAR (50),
	codigo_postal VARCHAR (10),
	latitud NUMERIC (9,6),
	longitud NUMERIC (9,6),
	CONSTRAINT pk_id_dir PRIMARY KEY (id_direccion),
	CONSTRAINT fk_id_ser_dir FOREIGN KEY (id_servicio) REFERENCES journeymate.servicio_turistico(id_servicio)
);


-- Creación de la Tabla RESERVA
CREATE TABLE journeymate.reserva(
	id_reserva INTEGER GENERATED ALWAYS AS IDENTITY,
	id_servicio INTEGER,
	id_estado INTEGER, 
	id_tipo_reserva INTEGER,
	id_usuario INTEGER,
	precio_total NUMERIC(6,2),
	telefono NUMERIC(15),
	fecha_reserva DATE,
	CONSTRAINT pk_id_res PRIMARY KEY (id_reserva),
	CONSTRAINT fk_id_ser FOREIGN KEY (id_servicio) REFERENCES journeymate.servicio_turistico (id_servicio),
	CONSTRAINT fk_id_est FOREIGN KEY (id_estado) REFERENCES journeymate.estado (id_estado),
	CONSTRAINT fk_id_tip_res FOREIGN KEY (id_tipo_reserva) REFERENCES journeymate.tipo_reserva (id_tipo_reserva),
	CONSTRAINT fk_id_usu FOREIGN KEY (id_usuario) REFERENCES journeymate.usuario (id_usuario)
);


-- Creación de la Tabla PAGO
CREATE TABLE journeymate.pago(
	id_pago INTEGER GENERATED ALWAYS AS IDENTITY,
	estado_pago VARCHAR (20),
	fecha_pago DATE, 
	id_reserva INTEGER,
	id_metodo INTEGER,
	CONSTRAINT pk_id_pag PRIMARY KEY (id_pago),
	CONSTRAINT fk_id_res FOREIGN KEY (id_reserva) REFERENCES journeymate.reserva (id_reserva),
	CONSTRAINT fk_id_met FOREIGN KEY (id_metodo) REFERENCES journeymate.metodo (id_metodo)
);


--Creación de la Tabla TREN
CREATE TABLE journeymate.tren(
	id_servicio INTEGER,
	fecha_llegada DATE,
	fecha_salida DATE, 
	destino VARCHAR(20),
	origen VARCHAR(20),
	compañia VARCHAR(30),
	CONSTRAINT pk_id_ser_tren PRIMARY KEY (id_servicio),
	CONSTRAINT fk_id_servicio_tren FOREIGN KEY (id_servicio) REFERENCES journeymate.servicio_turistico(id_servicio)
);


-- Creacion de la Tabla VUELO
CREATE TABLE journeymate.vuelo(
	id_servicio INTEGER,
	compañia VARCHAR(30),
	fecha_llegada DATE,
	fecha_salida DATE,
	destino VARCHAR(20),
	origen VARCHAR(20),
	CONSTRAINT pk_id_ser_vue PRIMARY KEY (id_servicio),
	CONSTRAINT fk_id_ser_vue FOREIGN KEY (id_servicio) REFERENCES journeymate.servicio_turistico(id_servicio)
);


-- Creacion de la Tabla CRUCERO
CREATE TABLE journeymate.crucero(
	id_servicio INTEGER,
	puerto_llegada VARCHAR(50),
	puerto_salida VARCHAR(50),
	fecha_llegada DATE,
	fecha_salida DATE,
	naviera VARCHAR(30),
	CONSTRAINT pk_id_ser_cruc PRIMARY KEY (id_servicio),
	CONSTRAINT fk_id_ser_cruc FOREIGN KEY (id_servicio) REFERENCES journeymate.servicio_turistico(id_servicio)
);


-- Creación de la Tabla APARTAMENTO
CREATE TABLE journeymate.apartamento(
	id_servicio INTEGER,
	descripcion VARCHAR(50),
	CONSTRAINT pk_id_ser_apar PRIMARY KEY (id_servicio),
	CONSTRAINT fk_id_ser_apar FOREIGN KEY (id_servicio) REFERENCES journeymate.servicio_turistico(id_servicio)
);


-- Creación de la Tabla ACTIVIDAD
CREATE TABLE journeymate.actividad(
	id_servicio INTEGER,
	descripcion VARCHAR(50),
	CONSTRAINT pk_id_ser_act PRIMARY KEY (id_servicio),
	CONSTRAINT fk_id_ser_act FOREIGN KEY (id_servicio) REFERENCES journeymate.servicio_turistico(id_servicio)
);


-- Creación de la Tabla VTC
CREATE TABLE journeymate.vtc(
	id_servicio INTEGER,
	precio NUMERIC(5,2),
	modelo VARCHAR(20),
	hora_llegada DATE,
	hora_salida DATE,
	distancia VARCHAR(6),
	marca VARCHAR(20),
	CONSTRAINT pk_id_ser_vtc PRIMARY KEY (id_servicio),
	CONSTRAINT fk_id_ser_vtc FOREIGN KEY (id_servicio) REFERENCES journeymate.servicio_turistico(id_servicio)
);


-- Creación de la Tabla HOTEL
CREATE TABLE journeymate.hotel(
	id_servicio INTEGER,
	estrellas NUMERIC(5),
	descripcion VARCHAR(50),
	CONSTRAINT pk_id_ser_hote PRIMARY KEY (id_servicio),
	CONSTRAINT fk_id_ser_hote FOREIGN KEY (id_servicio) REFERENCES journeymate.servicio_turistico(id_servicio)
);


-- Creación de la Tabla HABITACIÓN
CREATE TABLE journeymate.habitacion(
	id_habitacion INTEGER GENERATED ALWAYS AS IDENTITY,
	id_hotel INTEGER,
	tipo VARCHAR(20),
	precio_noche NUMERIC(8,2),
	capacidad NUMERIC(4),
	CONSTRAINT pk_id_hab PRIMARY KEY (id_habitacion),
	CONSTRAINT fk_id_hot FOREIGN KEY (id_hotel) REFERENCES journeymate.hotel (id_servicio)
);

