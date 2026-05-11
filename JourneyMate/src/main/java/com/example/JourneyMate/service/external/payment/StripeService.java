package com.example.JourneyMate.service.external.payment;

import com.example.JourneyMate.entity.booking.ReservaEntity;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Servicio encargado de la integración con Stripe Checkout.
 *
 * Permite crear sesiones de pago para reservas individuales y múltiples,
 * gestionando la redirección a Stripe y el retorno al sistema.
 */
@Service
public class StripeService {

    @Value("${app.base-url}")
    private String baseUrl;

    /**
     * Crea una sesión de pago en Stripe para una única reserva.
     *
     * @param reserva entidad de la reserva a pagar
     * @return URL de la sesión de Stripe para completar el pago
     * @throws Exception si ocurre un error al crear la sesión
     */
    public String createCheckoutSession(ReservaEntity reserva) throws Exception {

        long amountInCents = reserva.getPrecioTotal()
                .multiply(new BigDecimal(100))
                .longValue();

        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(baseUrl + "/api/v1/stripe/success?reservaId=" + reserva.getIdReserva())
                .setCancelUrl(baseUrl + "/api/v1/stripe/cancel")
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("eur")
                                                .setUnitAmount(amountInCents)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Reserva JourneyMate #" + reserva.getIdReserva())
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .build();

        Session session = Session.create(params);
        return session.getUrl();
    }

    /**
     * Crea una sesión de pago en Stripe para múltiples reservas agrupadas.
     *
     * El importe total corresponde a la suma de todas las reservas.
     * Los IDs se envían en la URL de retorno para su posterior procesamiento.
     *
     * @param reservaVirtual entidad temporal con el precio total agregado
     * @param reservaIdsStr IDs de reservas separados por coma (ej: "5,6,7")
     * @return URL de la sesión de Stripe para completar el pago
     * @throws Exception si ocurre un error al crear la sesión
     */
    public String createCheckoutSessionMultiple(ReservaEntity reservaVirtual,
                                                String reservaIdsStr) throws Exception {

        long amountInCents = reservaVirtual.getPrecioTotal()
                .multiply(new BigDecimal(100))
                .longValue();

        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(baseUrl + "/api/v1/stripe/success?reservaIds=" + reservaIdsStr)
                .setCancelUrl(baseUrl + "/api/v1/stripe/cancel")
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("eur")
                                                .setUnitAmount(amountInCents)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("JourneyMate — Pago de " +
                                                                        reservaIdsStr.split(",").length +
                                                                        " reservas")
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .build();

        Session session = Session.create(params);
        return session.getUrl();
    }
}