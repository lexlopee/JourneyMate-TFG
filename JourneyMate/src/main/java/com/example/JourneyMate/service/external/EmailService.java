package com.example.JourneyMate.service.external;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarFactura(String to, String nombreUsuario, Integer idReserva,
                              Double total, String tipoReserva, String servicioNombre) {

        // Traducir el tipo de reserva a texto legible
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