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

/**
 * Controlador encargado de gestionar pagos mediante Stripe.
 * <p>
 * Permite crear sesiones de pago, procesar pagos exitosos
 * y manejar cancelaciones de transacciones.
 */
@RestController
@RequestMapping("/api/v1/stripe")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StripeController {

    /**
     * Servicio encargado de la integración con Stripe.
     */
    private final StripeService stripeService;

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
     * Crea una sesión de pago de Stripe para una reserva.
     *
     * @param request información necesaria para generar el pago
     * @return URL de la sesión de pago de Stripe
     */
    @PostMapping("/create-checkout")
    public ResponseEntity<?> createCheckout(
            @RequestBody PagoRequestDTO request) {

        ReservaEntity reserva = reservaRepository.findById(
                request.getIdReserva()
        ).orElseThrow(() ->
                new RuntimeException(
                        "Reserva no encontrada: " +
                                request.getIdReserva()
                )
        );

        try {

            String url = stripeService.createCheckoutSession(reserva);

            return ResponseEntity.ok(Map.of("url", url));

        } catch (Exception e) {

            return ResponseEntity.internalServerError()
                    .body(Map.of(
                            "error",
                            "Error con Stripe: " + e.getMessage()
                    ));
        }
    }

    /**
     * Procesa un pago exitoso realizado mediante Stripe.
     * <p>
     * Guarda el pago, actualiza el estado de la reserva
     * a CONFIRMADA y envía una factura por correo electrónico.
     *
     * @param reservaId identificador de la reserva pagada
     * @return redirección al frontend con el resultado del pago
     */
    @GetMapping("/success")
    public ResponseEntity<Void> success(
            @RequestParam("reservaId") Integer reservaId) {

        HttpHeaders headers = new HttpHeaders();

        try {

            ReservaEntity reserva = reservaRepository.findById(reservaId)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Reserva no encontrada"
                            )
                    );

            MetodoEntity metodo = metodoRepository.findByNombre("STRIPE")
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Método STRIPE no encontrado en BD"
                            )
                    );

            // Evita duplicados por constraint UNIQUE
            List<PagoEntity> pagosExistentes =
                    pagoRepository.findByReserva_IdReserva(reservaId);

            if (pagosExistentes.isEmpty()) {

                PagoEntity pago = new PagoEntity();

                pago.setReserva(reserva);
                pago.setMetodo(metodo);
                pago.setEstado_pago("COMPLETADO");
                pago.setFecha_pago(LocalDate.now());

                pagoService.save(pago);
            }

            // Cambiar estado a CONFIRMADA
            EstadoEntity estadoConfirmada = estadoRepository
                    .findByNombreIgnoreCase("CONFIRMADA")
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Estado CONFIRMADA no encontrado en BD"
                            )
                    );

            reserva.setEstado(estadoConfirmada);
            reservaRepository.save(reserva);

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
                        "Email error: " + e.getMessage()
                );
            }

            headers.add(
                    "Location",
                    "http://localhost:5173/mis-reservas?pago=ok&metodo=stripe&reservaId="
                            + reservaId + "&tab=confirmadas"
            );

            return new ResponseEntity<>(headers, HttpStatus.FOUND);

        } catch (Exception e) {

            System.err.println(
                    "Error en Stripe success: " + e.getMessage()
            );

            headers.add(
                    "Location",
                    "http://localhost:5173/mis-reservas?pago=error"
            );

            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        }
    }

    /**
     * Maneja la cancelación de un pago realizado mediante Stripe.
     *
     * @return redirección al frontend indicando cancelación del pago
     */
    @GetMapping("/cancel")
    public ResponseEntity<Void> cancel() {

        HttpHeaders headers = new HttpHeaders();

        headers.add(
                "Location",
                "http://localhost:5173/mis-reservas?pago=cancelado"
        );

        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
}