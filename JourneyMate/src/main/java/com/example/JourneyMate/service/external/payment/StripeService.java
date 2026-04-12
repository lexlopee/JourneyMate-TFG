package com.example.JourneyMate.service.external.payment;

import com.example.JourneyMate.entity.booking.ReservaEntity;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class StripeService {

    @Value("${app.base-url}")
    private String baseUrl;

    /**
     * Sesión de Stripe para UNA reserva (pago individual).
     */
    public String createCheckoutSession(ReservaEntity reserva) throws Exception {
        long amountInCents = reserva.getPrecioTotal().multiply(new BigDecimal(100)).longValue();

        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                // ⭐ successUrl incluye reservaId para que el controller sepa qué confirmar
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
                                                                .build())
                                                .build())
                                .build())
                .build();

        Session session = Session.create(params);
        return session.getUrl();
    }

    /**
     * ⭐ Sesión de Stripe para VARIAS reservas (pago total agrupado).
     * El precio es la suma de todas. Los IDs se pasan en el successUrl
     * para que el controller los procese todos al volver.
     *
     * @param reservaVirtual  entidad con el precio total sumado (no persistida)
     * @param reservaIdsStr   IDs separados por coma, ej: "5,6,7"
     */
    public String createCheckoutSessionMultiple(ReservaEntity reservaVirtual, String reservaIdsStr) throws Exception {
        long amountInCents = reservaVirtual.getPrecioTotal().multiply(new BigDecimal(100)).longValue();

        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                // ⭐ successUrl incluye reservaIds (plural) para procesarlos todos
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
                                                                .setName("JourneyMate — Pago de " + reservaIdsStr.split(",").length + " reservas")
                                                                .build())
                                                .build())
                                .build())
                .build();

        Session session = Session.create(params);
        return session.getUrl();
    }
}