package com.example.JourneyMate.service.external.payment;

import com.example.JourneyMate.entity.booking.ReservaEntity;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class PaypalService {

    private final APIContext apiContext;
    private final String baseUrl;

    public PaypalService(APIContext apiContext, @Value("${app.base-url}") String baseUrl) {
        this.apiContext = apiContext;
        this.baseUrl = baseUrl;
    }

    public Payment createPayment(ReservaEntity reserva) throws PayPalRESTException {
        Amount amount = new Amount();
        amount.setCurrency("EUR");
        // Usamos precio_total de tu tabla reserva
        amount.setTotal(String.format(Locale.US, "%.2f", reserva.getPrecio_total()));

        Transaction transaction = new Transaction();
        transaction.setDescription("Reserva JourneyMate ID: " + reserva.getIdReserva());
        transaction.setAmount(amount);

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");

        Payment payment = new Payment();
        payment.setIntent("sale");
        payment.setPayer(payer);
        payment.setTransactions(transactions);

        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(baseUrl + "/api/v1/payment/cancel");
        redirectUrls.setReturnUrl(baseUrl + "/api/v1/payment/success?reservaId=" + reserva.getIdReserva());
        payment.setRedirectUrls(redirectUrls);

        return payment.create(apiContext);
    }

    public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException {
        Payment payment = new Payment();
        payment.setId(paymentId);
        PaymentExecution paymentExecute = new PaymentExecution();
        paymentExecute.setPayerId(payerId);
        return payment.execute(apiContext, paymentExecute);
    }
}