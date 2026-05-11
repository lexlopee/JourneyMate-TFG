package com.example.JourneyMate.controller.external.payment;

import com.example.JourneyMate.dao.booking.EstadoRepository;
import com.example.JourneyMate.dao.booking.ReservaRepository;
import com.example.JourneyMate.dao.payment.MetodoRepository;
import com.example.JourneyMate.dao.payment.PagoRepository;
import com.example.JourneyMate.dto.pago.PagoRequestDTO;
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
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
/**
 * Controlador encargado de gestionar pagos mediante PayPal.
 * <p>
 * Permite crear pagos individuales o múltiples, procesar pagos
 * aprobados por PayPal y actualizar el estado de las reservas.
 */
@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PaypalController {

    /**
     * Servicio encargado de la integración con PayPal.
     */
    private final PaypalService paypalService;

    /**
     * Servicio encargado de la lógica de pagos.
     */
    private final PagoService pagoService;

    /**
     * Repositorio para operaciones relacionadas con pagos.
     */
    private final PagoRepository pagoRepository;

    /**
     * Repositorio para operaciones relacionadas con reservas.
     */
    private final ReservaRepository reservaRepository;

    /**
     * Repositorio para métodos de pago.
     */
    private final MetodoRepository metodoRepository;

    /**
     * Repositorio para estados de reserva.
     */
    private final EstadoRepository estadoRepository;

    /**
     * Servicio encargado del envío de correos electrónicos.
     */
    private final EmailService emailService;

    /**
     * Contexto de autenticación y configuración de PayPal.
     */
    private final APIContext apiContext;

    /**
     * URL principal del frontend utilizada para redirecciones.
     */
    @Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    /**
     * Crea un pago mediante PayPal.
     * <p>
     * Permite procesar:
     * <ul>
     *     <li>Pagos individuales mediante idReserva</li>
     *     <li>Pagos múltiples mediante reservaIds</li>
     * </ul>
     *
     * @param request información necesaria para generar el pago
     * @return URL de aprobación de PayPal o mensaje de error
     */
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody PagoRequestDTO request) {

        try {

            // Pago múltiple
            if (request.getReservaIds() != null &&
                    !request.getReservaIds().isEmpty()) {

                List<ReservaEntity> reservas = request.getReservaIds().stream()
                        .map(id -> reservaRepository.findById(id).orElse(null))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

                if (reservas.isEmpty()) {
                    return ResponseEntity.badRequest()
                            .body(Map.of(
                                    "error",
                                    "No se encontraron las reservas indicadas"
                            ));
                }

                BigDecimal total = reservas.stream()
                        .map(ReservaEntity::getPrecioTotal)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                String idsParam = request.getReservaIds().stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(","));

                Amount amount = new Amount();
                amount.setCurrency("EUR");
                amount.setTotal(
                        String.format(Locale.US, "%.2f", total.doubleValue())
                );

                Transaction transaction = new Transaction();
                transaction.setDescription(
                        "JourneyMate — " + reservas.size() + " reservas"
                );
                transaction.setAmount(amount);

                Payer payer = new Payer();
                payer.setPaymentMethod("paypal");

                RedirectUrls redirectUrls = new RedirectUrls();
                redirectUrls.setCancelUrl(
                        frontendUrl + "/mis-reservas?pago=cancelado"
                );

                redirectUrls.setReturnUrl(
                        "http://localhost:8080/api/v1/payment/success-multiple?reservaIds="
                                + idsParam
                );

                com.paypal.api.payments.Payment payment =
                        new com.paypal.api.payments.Payment();

                payment.setIntent("sale");
                payment.setPayer(payer);
                payment.setTransactions(List.of(transaction));
                payment.setRedirectUrls(redirectUrls);

                com.paypal.api.payments.Payment created =
                        payment.create(apiContext);

                String approvalUrl = created.getLinks().stream()
                        .filter(l -> "approval_url".equals(l.getRel()))
                        .findFirst()
                        .map(Links::getHref)
                        .orElseThrow(() ->
                                new RuntimeException("No approval_url"));

                return ResponseEntity.ok(Map.of("url", approvalUrl));
            }

            // Pago individual
            if (request.getIdReserva() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "error",
                                "Debes indicar idReserva o reservaIds"
                        ));
            }

            ReservaEntity reserva = reservaRepository.findById(
                    request.getIdReserva()
            ).orElseThrow(() ->
                    new RuntimeException(
                            "No existe la reserva " + request.getIdReserva()
                    )
            );

            com.paypal.api.payments.Payment payment =
                    paypalService.createPayment(reserva);

            for (Links link : payment.getLinks()) {

                if ("approval_url".equals(link.getRel())) {
                    return ResponseEntity.ok(
                            Map.of("url", link.getHref())
                    );
                }
            }

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "error",
                            "No se encontró approval_url de PayPal"
                    ));

        } catch (Exception e) {

            return ResponseEntity.internalServerError()
                    .body(Map.of(
                            "error",
                            "Error en PayPal: " + e.getMessage()
                    ));
        }
    }

    /**
     * Procesa la confirmación de un pago individual aprobado por PayPal.
     * <p>
     * Guarda el pago, actualiza el estado de la reserva
     * y redirige al frontend.
     *
     * @param paymentId identificador del pago en PayPal
     * @param payerId identificador del pagador
     * @param reservaId identificador de la reserva
     * @return redirección al frontend con el estado del pago
     */
    @GetMapping("/success")
    public ResponseEntity<Void> success(
            @RequestParam("paymentId") String paymentId,
            @RequestParam("PayerID") String payerId,
            @RequestParam("reservaId") Integer reservaId) {

        HttpHeaders headers = new HttpHeaders();

        try {

            com.paypal.api.payments.Payment payment =
                    paypalService.executePayment(paymentId, payerId);

            if (!"approved".equals(payment.getState())) {

                headers.add(
                        "Location",
                        frontendUrl + "/mis-reservas?pago=error"
                );

                return new ResponseEntity<>(headers, HttpStatus.FOUND);
            }

            procesarPago(reservaId);

            headers.add(
                    "Location",
                    frontendUrl +
                            "/mis-reservas?pago=ok&metodo=paypal&reservaId="
                            + reservaId + "&tab=confirmadas"
            );

            return new ResponseEntity<>(headers, HttpStatus.FOUND);

        } catch (Exception e) {

            System.err.println(
                    "PayPal success error: " + e.getMessage()
            );

            headers.add(
                    "Location",
                    frontendUrl + "/mis-reservas?pago=error"
            );

            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        }
    }

    /**
     * Procesa la confirmación de múltiples pagos aprobados por PayPal.
     *
     * @param paymentId identificador del pago en PayPal
     * @param payerId identificador del pagador
     * @param reservaIds lista de identificadores de reservas
     * @return redirección al frontend con el estado del pago
     */
    @GetMapping("/success-multiple")
    public ResponseEntity<Void> successMultiple(
            @RequestParam("paymentId") String paymentId,
            @RequestParam("PayerID") String payerId,
            @RequestParam("reservaIds") String reservaIds) {

        HttpHeaders headers = new HttpHeaders();

        try {

            com.paypal.api.payments.Payment payment =
                    paypalService.executePayment(paymentId, payerId);

            if (!"approved".equals(payment.getState())) {

                headers.add(
                        "Location",
                        frontendUrl + "/mis-reservas?pago=error"
                );

                return new ResponseEntity<>(headers, HttpStatus.FOUND);
            }

            for (String idStr : reservaIds.split(",")) {

                try {
                    procesarPago(Integer.parseInt(idStr.trim()));
                } catch (Exception ignored) {
                }
            }

            String primerIdStr = reservaIds.split(",")[0].trim();

            headers.add(
                    "Location",
                    frontendUrl +
                            "/mis-reservas?pago=ok&metodo=paypal&reservaId="
                            + primerIdStr + "&tab=confirmadas"
            );

            return new ResponseEntity<>(headers, HttpStatus.FOUND);

        } catch (Exception e) {

            System.err.println(
                    "PayPal success-multiple error: " + e.getMessage()
            );

            headers.add(
                    "Location",
                    frontendUrl + "/mis-reservas?pago=error"
            );

            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        }
    }

    /**
     * Maneja la cancelación de un pago PayPal.
     *
     * @return redirección al frontend indicando cancelación
     */
    @GetMapping("/cancel")
    public ResponseEntity<Void> cancel() {

        HttpHeaders headers = new HttpHeaders();

        headers.add(
                "Location",
                frontendUrl + "/mis-reservas?pago=cancelado"
        );

        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    /**
     * Procesa internamente un pago confirmado.
     * <p>
     * Guarda el pago, actualiza el estado de la reserva
     * a CONFIRMADA y envía una factura por correo electrónico.
     *
     * @param idReserva identificador de la reserva pagada
     */
    private void procesarPago(Integer idReserva) {

        reservaRepository.findById(idReserva).ifPresent(reserva -> {

            MetodoEntity metodo = metodoRepository.findByNombre("PAYPAL")
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Método PAYPAL no encontrado"
                            )
                    );

            if (pagoRepository.findByReserva_IdReserva(idReserva).isEmpty()) {

                PagoEntity pago = new PagoEntity();

                pago.setReserva(reserva);
                pago.setMetodo(metodo);
                pago.setEstado_pago("COMPLETADO");
                pago.setFecha_pago(LocalDate.now());

                pagoService.save(pago);
            }

            estadoRepository.findByNombreIgnoreCase("CONFIRMADA")
                    .ifPresent(estado -> {

                        reserva.setEstado(estado);
                        reservaRepository.save(reserva);
                    });

            try {

                emailService.enviarFactura(
                        reserva.getUsuario().getEmail(),
                        reserva.getUsuario().getNombre(),
                        reserva.getIdReserva(),
                        reserva.getPrecioTotal().doubleValue(),
                        reserva.getTipoReserva().getNombre(),
                        reserva.getServicio().getNombre()
                );

            } catch (Exception e) {

                System.err.println(
                        "Email error reserva " +
                                idReserva + ": " + e.getMessage()
                );
            }
        });
    }
}