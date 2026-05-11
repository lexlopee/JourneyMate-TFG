package com.example.JourneyMate.service.external;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Servicio encargado del envío de correos electrónicos transaccionales.
 *
 * Actualmente se utiliza para enviar facturas de reservas confirmadas
 * dentro de JourneyMate.
 */
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Envía un correo electrónico con la factura de una reserva confirmada.
     *
     * El correo incluye información detallada del servicio contratado,
     * el importe total y los datos básicos de la reserva.
     *
     * @param to correo electrónico del destinatario
     * @param nombreUsuario nombre del usuario que realizó la reserva
     * @param idReserva identificador de la reserva
     * @param total importe total pagado
     * @param tipoReserva tipo de servicio reservado (HOTEL, VUELO, COCHE, etc.)
     * @param servicioNombre nombre del servicio contratado
     */
    public void enviarFactura(String to,
                              String nombreUsuario,
                              Integer idReserva,
                              Double total,
                              String tipoReserva,
                              String servicioNombre) {

        String tipoTexto = switch (tipoReserva != null ? tipoReserva.toUpperCase() : "") {
            case "HOTEL" -> "Hotel";
            case "VUELO" -> "Vuelo";
            case "COCHE" -> "Alquiler de coche";
            case "CRUCERO" -> "Crucero";
            case "TREN" -> "Tren";
            case "ACTIVIDAD" -> "Actividad";
            default -> tipoReserva != null ? tipoReserva : "Servicio turístico";
        };

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("journeymate.info@gmail.com");
        message.setTo(to);
        message.setSubject("Factura de tu viaje en JourneyMate - Reserva #" + idReserva);

        message.setText(
                "¡Hola " + nombreUsuario + "!\n\n" +
                        "Gracias por confiar en JourneyMate. Tu pago se ha procesado correctamente.\n\n" +
                        "Detalles de la factura:\n" +
                        "- Tipo Reserva:  " + tipoTexto + "\n" +
                        "- ID Reserva:    #" + idReserva + "\n" +
                        "- Importe Total: " + String.format("%.2f", total) + " €\n\n" +
                        "Puedes consultar tu reserva en cualquier momento desde:\n" +
                        "http://localhost:5173/mis-reservas\n\n" +
                        "¡Buen viaje!\n" +
                        "El equipo de JourneyMate 🌍"
        );

        mailSender.send(message);
    }
}