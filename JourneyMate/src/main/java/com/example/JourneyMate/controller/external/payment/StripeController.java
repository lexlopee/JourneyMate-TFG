package com.example.JourneyMate.controller.external.payment;

import com.example.JourneyMate.dao.booking.ReservaRepository;
import com.example.JourneyMate.dao.payment.MetodoRepository;
import com.example.JourneyMate.dto.pago.PagoRequestDTO;
import com.example.JourneyMate.dto.pago.PagoResponseDTO;
import com.example.JourneyMate.entity.booking.ReservaEntity;
import com.example.JourneyMate.entity.payment.MetodoEntity;
import com.example.JourneyMate.entity.payment.PagoEntity;
import com.example.JourneyMate.service.external.payment.StripeService;
import com.example.JourneyMate.service.payment.PagoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/stripe")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StripeController {

    private final StripeService stripeService;
    private final PagoService pagoService;
    private final ReservaRepository reservaRepository;
    private final MetodoRepository metodoRepository;

    @PostMapping("/create-checkout")
    public ResponseEntity<String> createCheckout(@RequestBody PagoRequestDTO request) {
        ReservaEntity reserva = reservaRepository.findById(request.getIdReserva())
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        try {
            String url = stripeService.createCheckoutSession(reserva);
            return ResponseEntity.ok(url);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error con Stripe: " + e.getMessage());
        }
    }

    @GetMapping("/success")
    public ResponseEntity<PagoResponseDTO> success(@RequestParam("reservaId") Integer reservaId) {
        // Reutilizamos la misma lógica que en PayPal
        ReservaEntity reserva = reservaRepository.findById(reservaId).get();
        MetodoEntity metodo = metodoRepository.findByNombre("STRIPE").get();

        PagoEntity pago = new PagoEntity();
        pago.setReserva(reserva);
        pago.setMetodo(metodo);
        pago.setEstado_pago("COMPLETADO (STRIPE)");
        pago.setFecha_pago(LocalDate.now());

        PagoEntity pagoGuardado = pagoService.save(pago);

        // Mapear al mismo DTO para que el Frontend no note la diferencia
        PagoResponseDTO response = new PagoResponseDTO();
        response.setIdPago(pagoGuardado.getIdPago());
        response.setIdReserva(reserva.getIdReserva());
        response.setIdMetodo(metodo.getIdMetodo());
        response.setEstadoPago(pagoGuardado.getEstado_pago());
        response.setFechaPago(pagoGuardado.getFecha_pago());

        return ResponseEntity.ok(response);
    }
}