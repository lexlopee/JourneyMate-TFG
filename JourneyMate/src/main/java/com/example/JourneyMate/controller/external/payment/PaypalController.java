package com.example.JourneyMate.controller.external.payment;

import com.example.JourneyMate.dao.booking.ReservaRepository;
import com.example.JourneyMate.dao.payment.MetodoRepository;
import com.example.JourneyMate.dto.pago.PagoRequestDTO;
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
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PaypalController {

    private final PaypalService paypalService;
    private final PagoService pagoService;
    private final ReservaRepository reservaRepository;
    private final MetodoRepository metodoRepository;
    private final EmailService emailService;

    /**
     * El frontend llama aquí con { idReserva } en el body.
     * Devuelve { url } con la URL de aprobación de PayPal.
     */
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody PagoRequestDTO request) {
        ReservaEntity reserva = reservaRepository.findById(request.getIdReserva())
                .orElseThrow(() -> new RuntimeException("No existe la reserva " + request.getIdReserva()));
        try {
            Payment payment = paypalService.createPayment(reserva);
            for (Links link : payment.getLinks()) {
                if ("approval_url".equals(link.getRel())) {
                    // ✅ Devolvemos { url } como JSON para que el frontend redirija
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

    /**
     * PayPal redirige aquí tras la aprobación del usuario.
     * Ejecutamos el pago, guardamos en BBDD y redirigimos al frontend.
     */
    @GetMapping("/success")
    public ResponseEntity<Void> success(
            @RequestParam("paymentId") String paymentId,
            @RequestParam("PayerID") String payerId,
            @RequestParam("reservaId") Integer reservaId) {
        try {
            Payment payment = paypalService.executePayment(paymentId, payerId);

            if ("approved".equals(payment.getState())) {
                ReservaEntity reserva = reservaRepository.findById(reservaId)
                        .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

                MetodoEntity metodo = metodoRepository.findByNombre("PAYPAL")
                        .orElseThrow(() -> new RuntimeException("Método PAYPAL no encontrado en BD"));

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

                // ✅ Redirigir al frontend
                HttpHeaders headers = new HttpHeaders();
                headers.add("Location",
                        "http://localhost:5173/pago-exitoso?metodo=paypal&reservaId=" + reservaId);
                return new ResponseEntity<>(headers, HttpStatus.FOUND);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.add("Location", "http://localhost:5173/pago-cancelado");
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