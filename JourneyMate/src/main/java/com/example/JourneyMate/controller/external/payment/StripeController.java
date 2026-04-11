package com.example.JourneyMate.controller.external.payment;

import com.example.JourneyMate.dao.booking.ReservaRepository;
import com.example.JourneyMate.dao.payment.MetodoRepository;
import com.example.JourneyMate.dto.pago.PagoRequestDTO;
import com.example.JourneyMate.entity.booking.ReservaEntity;
import com.example.JourneyMate.entity.payment.MetodoEntity;
import com.example.JourneyMate.entity.payment.PagoEntity;
import com.example.JourneyMate.service.external.EmailService;
import com.example.JourneyMate.service.external.payment.StripeService;
import com.example.JourneyMate.service.payment.PagoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/stripe")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StripeController {

    private final StripeService stripeService;
    private final PagoService pagoService;
    private final ReservaRepository reservaRepository;
    private final MetodoRepository metodoRepository;
    private final EmailService emailService;

    /**
     * El frontend llama aquí con { idReserva } en el body.
     * Devuelve la URL de Stripe Checkout para redirigir al usuario.
     */
    @PostMapping("/create-checkout")
    public ResponseEntity<?> createCheckout(@RequestBody PagoRequestDTO request) {
        ReservaEntity reserva = reservaRepository.findById(request.getIdReserva())
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada: " + request.getIdReserva()));
        try {
            String url = stripeService.createCheckoutSession(reserva);
            // ✅ Devolvemos { url } como JSON para que el frontend redirija
            return ResponseEntity.ok(Map.of("url", url));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error con Stripe: " + e.getMessage()));
        }
    }

    /**
     * Stripe redirige aquí tras el pago exitoso.
     * Guardamos el pago en BBDD, enviamos email y redirigimos al frontend.
     */
    @GetMapping("/success")
    public ResponseEntity<Void> success(@RequestParam("reservaId") Integer reservaId) {
        try {
            ReservaEntity reserva = reservaRepository.findById(reservaId)
                    .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

            MetodoEntity metodo = metodoRepository.findByNombre("STRIPE")
                    .orElseThrow(() -> new RuntimeException("Método STRIPE no configurado en BD"));

            PagoEntity pago = new PagoEntity();
            pago.setReserva(reserva);
            pago.setMetodo(metodo);
            pago.setEstado_pago("COMPLETADO");
            pago.setFecha_pago(LocalDate.now());
            pagoService.save(pago);

            try {
                emailService.enviarFactura(
                        reserva.getUsuario().getEmail(),
                        reserva.getUsuario().getNombre(),
                        reserva.getIdReserva(),
                        reserva.getPrecioTotal().doubleValue()
                );
            } catch (Exception e) {
                System.err.println("Error al enviar email: " + e.getMessage());
            }

            // ✅ Redirigir al frontend con los parámetros para mostrar pantalla de éxito
            HttpHeaders headers = new HttpHeaders();
            headers.add("Location",
                    "http://localhost:5173/pago-exitoso?metodo=stripe&reservaId=" + reservaId);
            return new ResponseEntity<>(headers, HttpStatus.FOUND);

        } catch (Exception e) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Location", "http://localhost:5173/pago-cancelado");
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        }
    }

    @GetMapping("/cancel")
    public ResponseEntity<Void> cancel() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "http://localhost:5173/pago-cancelado");
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
}