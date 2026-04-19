package com.example.JourneyMate.service.external;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // ✅ NUEVO: recibe tipoReserva como quinto parámetro
    public void enviarFactura(String to, String nombreUsuario, Integer idReserva,
                              Double total, String tipoReserva, String nombreReserva) {

        // Traducir el tipo de reserva a texto legible con emoji
        String tipoTexto = switch (tipoReserva != null ? tipoReserva.toUpperCase().trim() : "") {
            case "HOTEL"            -> "Alojamiento / Hotel";
            case "VUELO"            -> "Vuelo";
            case "VTC", "COCHE"     -> "Alquiler de coche";
            case "CRUCERO"          -> "Crucero";
            case "TREN"             -> "Tren";
            case "ACTIVIDAD"        -> "Actividad turística";
            default                 -> tipoReserva != null && !tipoReserva.isBlank()
                    ? tipoReserva : "Servicio turístico";
        };

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("journeymate.info@gmail.com");
        message.setTo(to);
        message.setSubject("Confirmación de tu reserva en JourneyMate - #" + idReserva);
        message.setText(
                "¡Hola " + nombreUsuario + "!\n\n" +
                        "Gracias por confiar en JourneyMate. Tu pago se ha procesado correctamente.\n\n" +
                        "━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                        "  DETALLES DE TU RESERVA\n" +
                        "━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                        "  Tipo de reserva:  " + tipoTexto + "\n" +  "->" + nombreReserva +  "\n" +
                        "  Importe total:    " + String.format("%.2f", total) + " €\n" +
                        "━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
                        "¡Buen viaje! 🌍\n" +
                        "El equipo de JourneyMate"
        );

        mailSender.send(message);
    }
}