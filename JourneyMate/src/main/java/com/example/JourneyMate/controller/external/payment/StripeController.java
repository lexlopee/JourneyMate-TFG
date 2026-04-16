package com.example.JourneyMate.controller.external.payment;

import com.example.JourneyMate.dao.booking.EstadoRepository;
import com.example.JourneyMate.dao.booking.ReservaRepository;
import com.example.JourneyMate.dao.payment.MetodoRepository;
import com.example.JourneyMate.dao.payment.PagoRepository;
import com.example.JourneyMate.dto.pago.PagoRequestDTO;
import com.example.JourneyMate.entity.booking.EstadoEntity;
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
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/stripe")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StripeController {

    private final StripeService stripeService;
    private final PagoService pagoService;
    private final PagoRepository pagoRepository;
    private final ReservaRepository reservaRepository;
    private final MetodoRepository metodoRepository;
    private final EstadoRepository estadoRepository;
    private final EmailService emailService;

    @PostMapping("/create-checkout")
    public ResponseEntity<?> createCheckout(@RequestBody PagoRequestDTO request) {
        ReservaEntity reserva = reservaRepository.findById(request.getIdReserva())
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada: " + request.getIdReserva()));
        try {
            String url = stripeService.createCheckoutSession(reserva);
            return ResponseEntity.ok(Map.of("url", url));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error con Stripe: " + e.getMessage()));
        }
    }

    @GetMapping("/success")
    public ResponseEntity<Void> success(@RequestParam("reservaId") Integer reservaId) {
        HttpHeaders headers = new HttpHeaders();
        try {
            ReservaEntity reserva = reservaRepository.findById(reservaId)
                    .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

            MetodoEntity metodo = metodoRepository.findByNombre("STRIPE")
                    .orElseThrow(() -> new RuntimeException("Método STRIPE no encontrado en BD"));

            // ✅ Solo guardar pago si no existe ya (evita constraint UNIQUE)
            List<PagoEntity> pagosExistentes = pagoRepository.findByReserva_IdReserva(reservaId);
            if (pagosExistentes.isEmpty()) {
                PagoEntity pago = new PagoEntity();
                pago.setReserva(reserva);
                pago.setMetodo(metodo);
                pago.setEstado_pago("COMPLETADO");
                pago.setFecha_pago(LocalDate.now());
                pagoService.save(pago);
            }

            // ✅ Al pagar → CONFIRMADA (no COMPLETADA)
            // La reserva pasa a COMPLETADA solo cuando vence la fecha (lo gestiona el frontend)
            EstadoEntity estadoConfirmada = estadoRepository
                    .findByNombreIgnoreCase("CONFIRMADA")
                    .orElseThrow(() -> new RuntimeException("Estado CONFIRMADA no encontrado en BD"));
            reserva.setEstado(estadoConfirmada);
            reservaRepository.save(reserva);

            try {
                emailService.enviarFactura(
                        reserva.getUsuario().getEmail(),
                        reserva.getUsuario().getNombre(),
                        reserva.getIdReserva(),
                        reserva.getPrecioTotal().doubleValue()
                );
            } catch (Exception e) {
                System.err.println("Email error: " + e.getMessage());
            }

            // Redirigir a Confirmadas
            headers.add("Location",
                    "http://localhost:5173/mis-reservas?pago=ok&metodo=stripe&reservaId=" + reservaId + "&tab=confirmadas");
            return new ResponseEntity<>(headers, HttpStatus.FOUND);

        } catch (Exception e) {
            System.err.println("Error en Stripe success: " + e.getMessage());
            headers.add("Location", "http://localhost:5173/mis-reservas?pago=error");
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        }
    }

    @GetMapping("/cancel")
    public ResponseEntity<Void> cancel() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "http://localhost:5173/mis-reservas?pago=cancelado");
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
}