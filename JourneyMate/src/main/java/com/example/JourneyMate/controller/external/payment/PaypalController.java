package com.example.JourneyMate.controller.external.payment;

import com.example.JourneyMate.dao.booking.ReservaRepository;
import com.example.JourneyMate.dao.payment.MetodoRepository;
import com.example.JourneyMate.dto.pago.PagoRequestDTO;
import com.example.JourneyMate.dto.pago.PagoResponseDTO;
import com.example.JourneyMate.entity.booking.ReservaEntity;
import com.example.JourneyMate.entity.payment.MetodoEntity;
import com.example.JourneyMate.entity.payment.PagoEntity;
import com.example.JourneyMate.service.external.payment.PaypalService;
import com.example.JourneyMate.service.payment.PagoService;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PaypalController {

    private final PaypalService paypalService;
    private final PagoService pagoService;
    private final ReservaRepository reservaRepository;
    private final MetodoRepository metodoRepository;

    @PostMapping("/create")
    public ResponseEntity<String> create(@RequestBody PagoRequestDTO request) {
        ReservaEntity reserva = reservaRepository.findById(request.getIdReserva())
                .orElseThrow(() -> new RuntimeException("No existe la reserva " + request.getIdReserva()));

        try {
            Payment payment = paypalService.createPayment(reserva);
            for (Links link : payment.getLinks()) {
                if (link.getRel().equals("approval_url")) {
                    return ResponseEntity.ok(link.getHref());
                }
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error en la pasarela de PayPal");
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/success")
    public ResponseEntity<PagoResponseDTO> success(
            @RequestParam("paymentId") String paymentId,
            @RequestParam("PayerID") String payerId,
            @RequestParam("reservaId") Integer reservaId) {

        try {
            Payment payment = paypalService.executePayment(paymentId, payerId);

            if (payment.getState().equals("approved")) {
                ReservaEntity reserva = reservaRepository.findById(reservaId).get();

                MetodoEntity metodo = metodoRepository.findByNombre("PAYPAL")
                        .orElseThrow(() -> new RuntimeException("Error: Ejecuta el script SQL para insertar el método PAYPAL"));

                // Creamos el Pago conforme a tu tabla journeymate.pago
                PagoEntity pago = new PagoEntity();
                pago.setReserva(reserva);
                pago.setMetodo(metodo);
                pago.setEstado_pago("COMPLETADO");
                pago.setFecha_pago(LocalDate.now());

                PagoEntity pagoGuardado = pagoService.save(pago);

                // Devolvemos el DTO
                PagoResponseDTO response = new PagoResponseDTO();
                response.setIdPago(pagoGuardado.getIdPago());
                response.setIdReserva(reserva.getIdReserva());
                response.setIdMetodo(metodo.getIdMetodo());
                response.setEstadoPago(pagoGuardado.getEstado_pago());
                response.setFechaPago(pagoGuardado.getFecha_pago());

                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.badRequest().build();
    }
}