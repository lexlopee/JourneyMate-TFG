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
import com.example.JourneyMate.service.external.payment.StripeService;
import com.example.JourneyMate.service.payment.PagoService;
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
@RequestMapping("/api/v1/stripe")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StripeController {

    private final StripeService stripeService;
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
     *
     * En el caso de varias reservas, creamos una sesión de Stripe
     * con el importe sumado de todas ellas y pasamos los IDs
     * concatenados en el successUrl para procesarlas todas al volver.
     */
    @PostMapping("/create-checkout")
    public ResponseEntity<?> createCheckout(@RequestBody PagoRequestDTO request) {
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

                // Creamos una reserva "virtual" con el total sumado para pasarla a Stripe
                ReservaEntity primera = reservas.get(0);
                BigDecimal total = reservas.stream()
                        .map(ReservaEntity::getPrecioTotal)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                // Creamos una ReservaEntity temporal con el precio total (no se persiste)
                ReservaEntity virtual = new ReservaEntity();
                virtual.setIdReserva(primera.getIdReserva()); // se usa en el nombre del producto
                virtual.setPrecioTotal(total);
                virtual.setUsuario(primera.getUsuario());
                virtual.setServicio(primera.getServicio());
                virtual.setEstado(primera.getEstado());
                virtual.setTipoReserva(primera.getTipoReserva());

                // IDs separados por comas para el successUrl
                String ids = request.getReservaIds().stream()
                        .map(String::valueOf)
                        .reduce((a, b) -> a + "," + b)
                        .orElse("");

                String url = stripeService.createCheckoutSessionMultiple(virtual, ids);
                return ResponseEntity.ok(Map.of("url", url));
            }

            // ── Modo pago individual ────────────────────────────────────────
            if (request.getIdReserva() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Debes indicar idReserva o reservaIds"));
            }

            ReservaEntity reserva = reservaRepository.findById(request.getIdReserva())
                    .orElseThrow(() -> new RuntimeException("Reserva no encontrada: " + request.getIdReserva()));

            String url = stripeService.createCheckoutSession(reserva);
            return ResponseEntity.ok(Map.of("url", url));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error con Stripe: " + e.getMessage()));
        }
    }

    /**
     * Stripe redirige aquí tras el pago exitoso.
     * Acepta tanto reservaId (individual) como reservaIds (múltiples, separados por coma).
     */
    @GetMapping("/success")
    public ResponseEntity<Void> success(
            @RequestParam(value = "reservaId",  required = false) Integer reservaId,
            @RequestParam(value = "reservaIds", required = false) String reservaIds,
            @RequestParam(value = "session_id", required = false) String sessionId) {

        HttpHeaders headers = new HttpHeaders();
        try {
            MetodoEntity metodo = metodoRepository.findByNombre("STRIPE")
                    .orElseThrow(() -> new RuntimeException("Método STRIPE no configurado en BD"));

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
                // Redirigir con todos los IDs en la URL (mostramos el primero para la pantalla)
                String primerIdStr = reservaIds.split(",")[0].trim();
                headers.add("Location",
                        frontendUrl + "/pago-exitoso?metodo=stripe&reservaId=" + primerIdStr
                                + "&reservaIds=" + reservaIds);
                return new ResponseEntity<>(headers, HttpStatus.FOUND);
            }

            // ── Individual ──
            if (reservaId != null) {
                procesarPagoExitoso(reservaId, metodo, estadoConfirmada);
                headers.add("Location",
                        frontendUrl + "/pago-exitoso?metodo=stripe&reservaId=" + reservaId);
                return new ResponseEntity<>(headers, HttpStatus.FOUND);
            }

            headers.add("Location", frontendUrl + "/pago-cancelado");
            return new ResponseEntity<>(headers, HttpStatus.FOUND);

        } catch (Exception e) {
            System.err.println("Error en Stripe success: " + e.getMessage());
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
            // Guardar pago
            PagoEntity pago = new PagoEntity();
            pago.setReserva(reserva);
            pago.setMetodo(metodo);
            pago.setEstado_pago("COMPLETADO");
            pago.setFecha_pago(LocalDate.now());
            pagoService.save(pago);

            // Cambiar estado a CONFIRMADA
            reserva.setEstado(estadoConfirmada);
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
                System.err.println("Error email reserva " + idReserva + ": " + e.getMessage());
            }
        });
    }
}