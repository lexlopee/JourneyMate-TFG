package com.example.JourneyMate.service.external;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarFactura(String to, String nombreUsuario, Integer idReserva, Double total) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("journeymate.info@gmail.com");
        message.setTo(to);
        message.setSubject("Factura de tu viaje en JourneyMate - Reserva #" + idReserva);
        message.setText("¡Hola " + nombreUsuario + "!\n\n" +
                "Gracias por confiar en JourneyMate. Tu pago se ha procesado correctamente.\n\n" +
                "Detalles de la factura:\n" +
                "- ID Reserva: " + idReserva + "\n" +
                "- Importe Total: " + total + "€\n\n" +
                "¡Buen viaje!\nEl equipo de JourneyMate.");

        mailSender.send(message);
    }
}