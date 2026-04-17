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
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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
    private final APIContext apiContext;

    @Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    /**
     * Crea el pago PayPal.
     * Acepta:
     *   - { "idReserva": 5 }          → pago individual
     *   - { "reservaIds": [5, 6, 7] } → pago múltiple (suma todos)
     */
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody PagoRequestDTO request) {
        try {
            // ── Pago múltiple ──────────────────────────────────────────────
            if (request.getReservaIds() != null && !request.getReservaIds().isEmpty()) {

                List<ReservaEntity> reservas = request.getReservaIds().stream()
                        .map(id -> reservaRepository.findById(id).orElse(null))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

                if (reservas.isEmpty()) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "No se encontraron las reservas indicadas"));
                }

                BigDecimal total = reservas.stream()
                        .map(ReservaEntity::getPrecioTotal)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                String idsParam = request.getReservaIds().stream()
                        .map(String::valueOf).collect(Collectors.joining(","));

                // Construir pago PayPal manualmente con el total sumado
                Amount amount = new Amount();
                amount.setCurrency("EUR");
                amount.setTotal(String.format(Locale.US, "%.2f", total.doubleValue()));

                Transaction transaction = new Transaction();
                transaction.setDescription("JourneyMate — " + reservas.size() + " reservas");
                transaction.setAmount(amount);

                Payer payer = new Payer();
                payer.setPaymentMethod("paypal");

                RedirectUrls redirectUrls = new RedirectUrls();
                redirectUrls.setCancelUrl(frontendUrl + "/mis-reservas?pago=cancelado");
                redirectUrls.setReturnUrl(frontendUrl + "/api/v1/payment/success-multiple?reservaIds=" + idsParam);

                // Usamos app.base-url para el returnUrl del backend
                redirectUrls.setReturnUrl("http://localhost:8080/api/v1/payment/success-multiple?reservaIds=" + idsParam);

                com.paypal.api.payments.Payment payment = new com.paypal.api.payments.Payment();
                payment.setIntent("sale");
                payment.setPayer(payer);
                payment.setTransactions(List.of(transaction));
                payment.setRedirectUrls(redirectUrls);

                com.paypal.api.payments.Payment created = payment.create(apiContext);

                String approvalUrl = created.getLinks().stream()
                        .filter(l -> "approval_url".equals(l.getRel()))
                        .findFirst().map(Links::getHref)
                        .orElseThrow(() -> new RuntimeException("No approval_url"));

                return ResponseEntity.ok(Map.of("url", approvalUrl));
            }

            // ── Pago individual ────────────────────────────────────────────
            if (request.getIdReserva() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Debes indicar idReserva o reservaIds"));
            }

            ReservaEntity reserva = reservaRepository.findById(request.getIdReserva())
                    .orElseThrow(() -> new RuntimeException("No existe la reserva " + request.getIdReserva()));

            com.paypal.api.payments.Payment payment = paypalService.createPayment(reserva);
            for (Links link : payment.getLinks()) {
                if ("approval_url".equals(link.getRel())) {
                    return ResponseEntity.ok(Map.of("url", link.getHref()));
                }
            }
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "No se encontró approval_url de PayPal"));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error en PayPal: " + e.getMessage()));
        }
    }

    /**
     * PayPal redirige aquí tras aprobar un pago INDIVIDUAL.
     * Guarda pago + cambia estado a CONFIRMADA + redirige al frontend.
     */
    @GetMapping("/success")
    public ResponseEntity<Void> success(
            @RequestParam("paymentId") String paymentId,
            @RequestParam("PayerID")   String payerId,
            @RequestParam("reservaId") Integer reservaId) {
        HttpHeaders headers = new HttpHeaders();
        try {
            com.paypal.api.payments.Payment payment = paypalService.executePayment(paymentId, payerId);
            if (!"approved".equals(payment.getState())) {
                headers.add("Location", frontendUrl + "/mis-reservas?pago=error");
                return new ResponseEntity<>(headers, HttpStatus.FOUND);
            }
            procesarPago(reservaId);
            headers.add("Location",
                    frontendUrl + "/mis-reservas?pago=ok&metodo=paypal&reservaId=" + reservaId + "&tab=confirmadas");
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        } catch (Exception e) {
            System.err.println("PayPal success error: " + e.getMessage());
            headers.add("Location", frontendUrl + "/mis-reservas?pago=error");
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        }
    }

    /**
     * PayPal redirige aquí tras aprobar un pago MÚLTIPLE.
     */
    @GetMapping("/success-multiple")
    public ResponseEntity<Void> successMultiple(
            @RequestParam("paymentId")  String paymentId,
            @RequestParam("PayerID")    String payerId,
            @RequestParam("reservaIds") String reservaIds) {
        HttpHeaders headers = new HttpHeaders();
        try {
            com.paypal.api.payments.Payment payment = paypalService.executePayment(paymentId, payerId);
            if (!"approved".equals(payment.getState())) {
                headers.add("Location", frontendUrl + "/mis-reservas?pago=error");
                return new ResponseEntity<>(headers, HttpStatus.FOUND);
            }
            for (String idStr : reservaIds.split(",")) {
                try { procesarPago(Integer.parseInt(idStr.trim())); }
                catch (Exception ignored) {}
            }
            String primerIdStr = reservaIds.split(",")[0].trim();
            headers.add("Location",
                    frontendUrl + "/mis-reservas?pago=ok&metodo=paypal&reservaId=" + primerIdStr + "&tab=confirmadas");
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        } catch (Exception e) {
            System.err.println("PayPal success-multiple error: " + e.getMessage());
            headers.add("Location", frontendUrl + "/mis-reservas?pago=error");
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        }
    }

    @GetMapping("/cancel")
    public ResponseEntity<Void> cancel() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", frontendUrl + "/mis-reservas?pago=cancelado");
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    // ── Helper: guardar pago + cambiar estado a CONFIRMADA ───────────────────
    private void procesarPago(Integer idReserva) {
        reservaRepository.findById(idReserva).ifPresent(reserva -> {
            MetodoEntity metodo = metodoRepository.findByNombre("PAYPAL")
                    .orElseThrow(() -> new RuntimeException("Método PAYPAL no encontrado"));

            // Solo guardar si no existe ya (constraint UNIQUE uq_pago_reserva)
            if (pagoRepository.findByReserva_IdReserva(idReserva).isEmpty()) {
                PagoEntity pago = new PagoEntity();
                pago.setReserva(reserva);
                pago.setMetodo(metodo);
                pago.setEstado_pago("COMPLETADO");
                pago.setFecha_pago(LocalDate.now());
                pagoService.save(pago);
            }

            // ✅ Al pagar → CONFIRMADA (pasa a Historial solo cuando vence la fecha)
            estadoRepository.findByNombreIgnoreCase("CONFIRMADA").ifPresent(estado -> {
                reserva.setEstado(estado);
                reservaRepository.save(reserva);
            });

            try {
                emailService.enviarFactura(
                        reserva.getUsuario().getEmail(),
                        reserva.getUsuario().getNombre(),
                        reserva.getIdReserva(),
                        reserva.getPrecioTotal().doubleValue()
                );
            } catch (Exception e) {
                System.err.println("Email error reserva " + idReserva + ": " + e.getMessage());
            }
        });
    }
}