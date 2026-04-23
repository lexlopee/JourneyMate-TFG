INSERT INTO journeymate.tipo_reserva (nombre) VALUES
('HOTEL'),
('CRUCERO'),
('VUELO'),
('TREN'),
('COCHE'),
('ACTIVIDAD');
INSERT INTO journeymate.estado (nombre) values
('PENDIENTE'),
('CONFIRMADA'),
('CANCELADA'),
('COMPLETADA');

INSERT INTO journeymate.rol (nombre, descripcion)
VALUES ('USER', 'Rol por defecto para usuarios');

insert into journeymate.metodo (nombre)
values ('PAYPAL');
insert into journeymate.metodo (nombre)
values ('STRIPE');
