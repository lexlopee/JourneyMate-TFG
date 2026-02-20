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

    public String createCheckoutSession(ReservaEntity reserva) throws Exception {
        // Stripe usa céntimos (BigDecimal * 100)
        long amountInCents = reserva.getPrecio_total().multiply(new BigDecimal(100)).longValue();

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
                                                                .build())
                                                .build())
                                .build())
                .build();

        Session session = Session.create(params);
        return session.getUrl();
    }
}