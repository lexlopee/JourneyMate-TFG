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
import com.example.JourneyMate.service.external.payment.PaypalService;
import com.example.JourneyMate.service.payment.PagoService;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PaypalController {

    private final PaypalService paypalService;
    private final PagoService pagoService;
    private final PagoRepository pagoRepository;
    private final ReservaRepository reservaRepository;
    private final MetodoRepository metodoRepository;
    private final EstadoRepository estadoRepository;
    private final EmailService emailService;

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody PagoRequestDTO request) {
        ReservaEntity reserva = reservaRepository.findById(request.getIdReserva())
                .orElseThrow(() -> new RuntimeException("No existe la reserva " + request.getIdReserva()));
        try {
            Payment payment = paypalService.createPayment(reserva);
            for (Links link : payment.getLinks()) {
                if ("approval_url".equals(link.getRel())) {
                    return ResponseEntity.ok(Map.of("url", link.getHref()));
                }
            }
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "No se encontró la URL de aprobación de PayPal"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error en PayPal: " + e.getMessage()));
        }
    }

    @GetMapping("/success")
    public ResponseEntity<Void> success(
            @RequestParam("paymentId") String paymentId,
            @RequestParam("PayerID") String payerId,
            @RequestParam("reservaId") Integer reservaId) {
        HttpHeaders headers = new HttpHeaders();
        try {
            Payment payment = paypalService.executePayment(paymentId, payerId);

            if (!"approved".equals(payment.getState())) {
                headers.add("Location", "http://localhost:5173/mis-reservas?pago=error");
                return new ResponseEntity<>(headers, HttpStatus.FOUND);
            }

            ReservaEntity reserva = reservaRepository.findById(reservaId)
                    .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

            MetodoEntity metodo = metodoRepository.findByNombre("PAYPAL")
                    .orElseThrow(() -> new RuntimeException("Método PAYPAL no encontrado en BD"));

            // ✅ Solo guardar pago si NO existe ya uno para esta reserva
            // (evita el error de constraint UNIQUE uq_pago_reserva)
            List<PagoEntity> pagosExistentes = pagoRepository.findByReserva_IdReserva(reservaId);
            if (pagosExistentes.isEmpty()) {
                PagoEntity pago = new PagoEntity();
                pago.setReserva(reserva);
                pago.setMetodo(metodo);
                pago.setEstado_pago("COMPLETADO");
                pago.setFecha_pago(LocalDate.now());
                pagoService.save(pago);
            }

            // ✅ Cambiar estado a COMPLETADA (con A — igual que en la BBDD)
            EstadoEntity estadoCompletada = estadoRepository
                    .findByNombreIgnoreCase("COMPLETADA")
                    .orElseThrow(() -> new RuntimeException("Estado COMPLETADA no encontrado en BD"));
            reserva.setEstado(estadoCompletada);
            reservaRepository.save(reserva);

            // Email (no crítico)
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

            // ✅ Redirigir a /mis-reservas con tab=historial
            headers.add("Location",
                    "http://localhost:5173/mis-reservas?pago=ok&metodo=paypal&reservaId=" + reservaId + "&tab=historial");
            return new ResponseEntity<>(headers, HttpStatus.FOUND);

        } catch (Exception e) {
            System.err.println("Error en PayPal success: " + e.getMessage());
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