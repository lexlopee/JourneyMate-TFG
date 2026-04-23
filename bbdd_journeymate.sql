--DROP SCHEMA IF EXISTS journeymate CASCADE;
CREATE SCHEMA journeymate AUTHORIZATION journeymate_bbdd;

-- Creación de la Tabla ROL
CREATE TABLE journeymate.rol(
	id_rol INTEGER GENERATED ALWAYS AS IDENTITY,
	descripcion VARCHAR (50) NOT NULL,
	nombre VARCHAR(30) NOT NULL,
	CONSTRAINT pk_id_rol PRIMARY KEY (id_rol),
	CONSTRAINT uq_nom_rol UNIQUE (nombre)
);
-- Creación de la Tabla USUARIO
CREATE TABLE journeymate.usuario(
	id_usuario INTEGER GENERATED ALWAYS AS IDENTITY,
	telefono VARCHAR (20),
	password_hash VARCHAR(255) ,
	nombre VARCHAR (30) NOT NULL,
	primer_apellido VARCHAR (30) NOT NULL,
	segundo_apellido VARCHAR (30),
	fecha_registro DATE NOT NULL,
	fecha_nacimiento DATE,
	email VARCHAR (100) NOT NULL, 
	id_rol INTEGER NOT NULL,
	CONSTRAINT pk_id_usu PRIMARY KEY (id_usuario),
	CONSTRAINT uq_ema_usua UNIQUE (email),
	CONSTRAINT fk_id_rol FOREIGN KEY (id_rol) REFERENCES journeymate.rol (id_rol) ON DELETE RESTRICT ON UPDATE CASCADE
);
CREATE INDEX idx_usuario_email ON journeymate.usuario(email);

-- Creación de la Tabla TOKEN_JWT
CREATE TABLE journeymate.token_jwt(
	id_token INTEGER GENERATED ALWAYS AS IDENTITY, 
	id_usuario INTEGER NOT NULL,
	fecha_expiacion DATE NOT NULL, 
	fecha_creacion DATE NOT NULL, 
	token TEXT NOT NULL,
	CONSTRAINT pk_id_tok PRIMARY KEY (id_token),
	CONSTRAINT uq_tok UNIQUE (token),
	CONSTRAINT fk_id_usu FOREIGN KEY (id_usuario) REFERENCES journeymate.usuario (id_usuario) ON DELETE CASCADE 
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
	nombre VARCHAR (30),
	CONSTRAINT pk_id_metodo PRIMARY KEY (id_metodo)
);

-- Creación de la Tabla SERVICIO_TURISTICO
CREATE TABLE journeymate.servicio_turistico(
    id_servicio INTEGER GENERATED ALWAYS AS IDENTITY,
    nombre VARCHAR(100) NOT NULL,
    precio_base NUMERIC(10,2),
    CONSTRAINT pk_id_ser PRIMARY KEY(id_servicio),
    CONSTRAINT ck_pre_bas_ser_tur CHECK (precio_base >= 0)
);

-- Creacion de la Tabla DIRECCION
CREATE TABLE journeymate.direccion(
	id_direccion INTEGER GENERATED ALWAYS AS IDENTITY,
	id_servicio INTEGER NOT NULL, 
	descripcion VARCHAR(200),
	latitud NUMERIC (9,6),
	longitud NUMERIC (9,6),
	CONSTRAINT pk_id_dir PRIMARY KEY (id_direccion),
	CONSTRAINT fk_id_ser_dir FOREIGN KEY (id_servicio) REFERENCES journeymate.servicio_turistico(id_servicio)
);

-- Creación de la Tabla RESERVA
CREATE TABLE journeymate.reserva(
	id_reserva INTEGER GENERATED ALWAYS AS IDENTITY,
	id_servicio INTEGER NOT NULL,
	id_estado INTEGER NOT NULL, 
	id_tipo_reserva INTEGER NOT NULL,
	id_usuario INTEGER NOT NULL,
	precio_total NUMERIC(6,2),
	fecha_reserva DATE,
	CONSTRAINT pk_id_res PRIMARY KEY (id_reserva),
	CONSTRAINT fk_id_ser FOREIGN KEY (id_servicio) REFERENCES journeymate.servicio_turistico (id_servicio) ON DELETE RESTRICT,
	CONSTRAINT fk_id_est FOREIGN KEY (id_estado) REFERENCES journeymate.estado (id_estado),
	CONSTRAINT fk_id_tip_res FOREIGN KEY (id_tipo_reserva) REFERENCES journeymate.tipo_reserva (id_tipo_reserva),
	CONSTRAINT fk_id_usu FOREIGN KEY (id_usuario) REFERENCES journeymate.usuario (id_usuario) ON DELETE RESTRICT 
);
CREATE INDEX idx_reserva_usuario ON journeymate.reserva(id_usuario);

-- Creación de la Tabla PAGO
CREATE TABLE journeymate.pago(
	id_pago INTEGER GENERATED ALWAYS AS IDENTITY,
	estado_pago VARCHAR(20),
	fecha_pago DATE, 
	id_reserva INTEGER NOT NULL,
	id_metodo INTEGER NOT NULL,
	CONSTRAINT pk_id_pag PRIMARY KEY (id_pago),
	CONSTRAINT uq_pago_reserva UNIQUE(id_reserva),
	CONSTRAINT fk_id_res FOREIGN KEY (id_reserva) REFERENCES journeymate.reserva (id_reserva),
	CONSTRAINT fk_id_met FOREIGN KEY (id_metodo) REFERENCES journeymate.metodo (id_metodo)
);

-- Creación de la Tabla TREN
CREATE TABLE journeymate.tren(
    id_servicio INTEGER NOT NULL,
    fecha_llegada DATE,
    fecha_salida DATE,
    destino VARCHAR(20),
    origen VARCHAR(20),
    compañia VARCHAR(30),
    CONSTRAINT pk_id_ser_tren PRIMARY KEY (id_servicio),
    CONSTRAINT fk_tren_servicio FOREIGN KEY (id_servicio) REFERENCES journeymate.servicio_turistico(id_servicio) ON DELETE CASCADE
);
-- Creacion de la Tabla VUELO
CREATE TABLE journeymate.vuelo(
	id_servicio INTEGER NOT NULL,
	compañia VARCHAR(30),
	fecha_regreso DATE,
	fecha_salida DATE,
	destino VARCHAR(20),
	origen VARCHAR(20),
	CONSTRAINT pk_id_ser_vue PRIMARY KEY (id_servicio),
	CONSTRAINT fk_id_ser_vue FOREIGN KEY (id_servicio) REFERENCES journeymate.servicio_turistico(id_servicio) ON DELETE CASCADE
);
-- Creacion de la Tabla CRUCERO
CREATE TABLE journeymate.crucero(
	id_servicio INTEGER NOT NULL,
	puerto_llegada VARCHAR(50),
	puerto_salida VARCHAR(50),
	fecha_llegada DATE,
	fecha_salida DATE,
	naviera VARCHAR(30),
	CONSTRAINT pk_id_ser_cruc PRIMARY KEY (id_servicio),
	CONSTRAINT fk_id_ser_cruc FOREIGN KEY (id_servicio) REFERENCES journeymate.servicio_turistico(id_servicio) ON DELETE CASCADE
);

-- Creación de la Tabla ACTIVIDAD
CREATE TABLE journeymate.actividad(
	id_servicio INTEGER NOT NULL,
	descripcion VARCHAR(400),
	CONSTRAINT pk_id_ser_act PRIMARY KEY (id_servicio),
	CONSTRAINT fk_id_ser_act FOREIGN KEY (id_servicio) REFERENCES journeymate.servicio_turistico(id_servicio) ON DELETE CASCADE
);
-- Creación de la Tabla COCHE
CREATE TABLE journeymate.coche(
	id_servicio INTEGER NOT NULL,
	precio NUMERIC(10,2),
	modelo VARCHAR(20),
	hora_llegada TIMESTAMP,
	hora_salida TIMESTAMP,
	distancia NUMERIC(6,2),
	marca VARCHAR(20),
	CONSTRAINT pk_id_ser_coch PRIMARY KEY (id_servicio),
	CONSTRAINT fk_id_ser_coch FOREIGN KEY (id_servicio) REFERENCES journeymate.servicio_turistico(id_servicio) ON DELETE CASCADE
);
-- Creación de la Tabla HOTEL
CREATE TABLE journeymate.hotel(
	id_servicio INTEGER NOT NULL,
	estrellas smallint NOT NULL,
	descripcion VARCHAR(255),
	CONSTRAINT pk_id_ser_hote PRIMARY KEY (id_servicio),
	CONSTRAINT ck_estr_hote CHECK (estrellas BETWEEN 1 and 5),
	CONSTRAINT fk_id_ser_hote FOREIGN KEY (id_servicio) REFERENCES journeymate.servicio_turistico(id_servicio) ON DELETE CASCADE
);

