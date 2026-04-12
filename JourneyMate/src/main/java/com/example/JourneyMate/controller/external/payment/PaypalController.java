package com.example.JourneyMate.controller.external.payment;

import com.example.JourneyMate.dao.booking.EstadoRepository;
import com.example.JourneyMate.dao.booking.ReservaRepository;
import com.example.JourneyMate.dao.payment.MetodoRepository;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
    private final EstadoRepository estadoRepository;
    private final EmailService emailService;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    /**
     * Pago de UNA o VARIAS reservas.
     *
     * Cuerpo para una reserva:    { "idReserva": 5 }
     * Cuerpo para varias:         { "reservaIds": [5, 6, 7] }
     */
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody PagoRequestDTO request) {
        try {
            // ── Modo pago múltiple ──────────────────────────────────────────
            if (request.getReservaIds() != null && !request.getReservaIds().isEmpty()) {

                List<ReservaEntity> reservas = new ArrayList<>();
                for (Integer id : request.getReservaIds()) {
                    reservaRepository.findById(id).ifPresent(reservas::add);
                }

                if (reservas.isEmpty()) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "No se encontraron las reservas indicadas"));
                }

                BigDecimal total = reservas.stream()
                        .map(ReservaEntity::getPrecioTotal)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                // IDs concatenados para el returnUrl
                String ids = request.getReservaIds().stream()
                        .map(String::valueOf)
                        .reduce((a, b) -> a + "," + b)
                        .orElse("");

                // Usamos la primera reserva como base, con precio total sumado
                ReservaEntity virtual = new ReservaEntity();
                virtual.setIdReserva(reservas.get(0).getIdReserva());
                virtual.setPrecioTotal(total);
                virtual.setUsuario(reservas.get(0).getUsuario());
                virtual.setServicio(reservas.get(0).getServicio());
                virtual.setEstado(reservas.get(0).getEstado());
                virtual.setTipoReserva(reservas.get(0).getTipoReserva());

                Payment payment = paypalService.createPaymentMultiple(virtual, ids);
                for (Links link : payment.getLinks()) {
                    if ("approval_url".equals(link.getRel())) {
                        return ResponseEntity.ok(Map.of("url", link.getHref()));
                    }
                }
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "No se encontró URL de aprobación PayPal"));
            }

            // ── Modo pago individual ────────────────────────────────────────
            if (request.getIdReserva() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Debes indicar idReserva o reservaIds"));
            }

            ReservaEntity reserva = reservaRepository.findById(request.getIdReserva())
                    .orElseThrow(() -> new RuntimeException("No existe la reserva " + request.getIdReserva()));

            Payment payment = paypalService.createPayment(reserva);
            for (Links link : payment.getLinks()) {
                if ("approval_url".equals(link.getRel())) {
                    return ResponseEntity.ok(Map.of("url", link.getHref()));
                }
            }
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "No se encontró URL de aprobación PayPal"));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error en PayPal: " + e.getMessage()));
        }
    }

    /**
     * PayPal redirige aquí tras la aprobación.
     * Acepta tanto reservaId (individual) como reservaIds (múltiples, separados por coma).
     */
    @GetMapping("/success")
    public ResponseEntity<Void> success(
            @RequestParam("paymentId") String paymentId,
            @RequestParam("PayerID") String payerId,
            @RequestParam(value = "reservaId",  required = false) Integer reservaId,
            @RequestParam(value = "reservaIds", required = false) String reservaIds) {

        HttpHeaders headers = new HttpHeaders();
        try {
            Payment payment = paypalService.executePayment(paymentId, payerId);

            if (!"approved".equals(payment.getState())) {
                headers.add("Location", frontendUrl + "/pago-cancelado");
                return new ResponseEntity<>(headers, HttpStatus.FOUND);
            }

            MetodoEntity metodo = metodoRepository.findByNombre("PAYPAL")
                    .orElseThrow(() -> new RuntimeException("Método PAYPAL no encontrado en BD"));

            EstadoEntity estadoConfirmada = estadoRepository
                    .findByNombreIgnoreCase("CONFIRMADA")
                    .orElseThrow(() -> new RuntimeException("Estado CONFIRMADA no encontrado en BD"));

            // ── Múltiples IDs ──
            if (reservaIds != null && !reservaIds.isBlank()) {
                for (String idStr : reservaIds.split(",")) {
                    try {
                        int id = Integer.parseInt(idStr.trim());
                        procesarPagoExitoso(id, metodo, estadoConfirmada);
                    } catch (NumberFormatException ignored) {}
                }
                String primerIdStr = reservaIds.split(",")[0].trim();
                headers.add("Location",
                        frontendUrl + "/pago-exitoso?metodo=paypal&reservaId=" + primerIdStr
                                + "&reservaIds=" + reservaIds);
                return new ResponseEntity<>(headers, HttpStatus.FOUND);
            }

            // ── Individual ──
            if (reservaId != null) {
                procesarPagoExitoso(reservaId, metodo, estadoConfirmada);
                headers.add("Location",
                        frontendUrl + "/pago-exitoso?metodo=paypal&reservaId=" + reservaId);
                return new ResponseEntity<>(headers, HttpStatus.FOUND);
            }

            headers.add("Location", frontendUrl + "/pago-cancelado");
            return new ResponseEntity<>(headers, HttpStatus.FOUND);

        } catch (Exception e) {
            System.err.println("Error en PayPal success: " + e.getMessage());
            headers.add("Location", frontendUrl + "/pago-cancelado");
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        }
    }

    @GetMapping("/cancel")
    public ResponseEntity<Void> cancel() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", frontendUrl + "/pago-cancelado");
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    // ── Helper ────────────────────────────────────────────────────────────
    private void procesarPagoExitoso(Integer idReserva, MetodoEntity metodo, EstadoEntity estadoConfirmada) {
        reservaRepository.findById(idReserva).ifPresent(reserva -> {
            PagoEntity pago = new PagoEntity();
            pago.setReserva(reserva);
            pago.setMetodo(metodo);
            pago.setEstado_pago("COMPLETADO");
            pago.setFecha_pago(LocalDate.now());
            pagoService.save(pago);

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
                System.err.println("Error email reserva " + idReserva + ": " + e.getMessage());
            }
        });
    }
}