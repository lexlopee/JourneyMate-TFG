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

/**
 * Servicio encargado de la integración con PayPal.
 *
 * Permite crear pagos individuales y múltiples de reservas,
 * así como ejecutar la confirmación del pago tras la aprobación del usuario.
 */
@Service
public class PaypalService {

    private final APIContext apiContext;
    private final String baseUrl;

    public PaypalService(APIContext apiContext,
                         @Value("${app.base-url}") String baseUrl) {
        this.apiContext = apiContext;
        this.baseUrl = baseUrl;
    }

    /**
     * Crea un pago de PayPal para una única reserva.
     *
     * @param reserva entidad de la reserva a pagar
     * @return objeto {@link Payment} creado en PayPal
     * @throws PayPalRESTException si ocurre un error en la API de PayPal
     */
    public Payment createPayment(ReservaEntity reserva) throws PayPalRESTException {

        Amount amount = new Amount();
        amount.setCurrency("EUR");
        amount.setTotal(String.format(Locale.US, "%.2f", reserva.getPrecioTotal()));

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
        redirectUrls.setReturnUrl(
                baseUrl + "/api/v1/payment/success?reservaId=" + reserva.getIdReserva()
        );

        payment.setRedirectUrls(redirectUrls);

        return payment.create(apiContext);
    }

    /**
     * Crea un pago de PayPal para múltiples reservas agrupadas.
     *
     * El importe total corresponde a la suma de todas las reservas.
     * Los IDs se envían en la URL de retorno para su posterior confirmación.
     *
     * @param reservaVirtual entidad temporal con el precio total agregado
     * @param reservaIdsStr IDs de reservas separados por coma (ej: "5,6,7")
     * @return objeto {@link Payment} creado en PayPal
     * @throws PayPalRESTException si ocurre un error en la API de PayPal
     */
    public Payment createPaymentMultiple(ReservaEntity reservaVirtual,
                                         String reservaIdsStr) throws PayPalRESTException {

        Amount amount = new Amount();
        amount.setCurrency("EUR");
        amount.setTotal(String.format(Locale.US, "%.2f", reservaVirtual.getPrecioTotal()));

        Transaction transaction = new Transaction();
        transaction.setDescription(
                "JourneyMate — Pago de " + reservaIdsStr.split(",").length + " reservas"
        );
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
        redirectUrls.setReturnUrl(
                baseUrl + "/api/v1/payment/success?reservaIds=" + reservaIdsStr
        );

        payment.setRedirectUrls(redirectUrls);

        return payment.create(apiContext);
    }

    /**
     * Ejecuta la confirmación de un pago tras la aprobación del usuario en PayPal.
     *
     * @param paymentId identificador del pago generado por PayPal
     * @param payerId identificador del pagador en PayPal
     * @return objeto {@link Payment} ejecutado y confirmado
     * @throws PayPalRESTException si ocurre un error durante la ejecución del pago
     */
    public Payment executePayment(String paymentId, String payerId)
            throws PayPalRESTException {

        Payment payment = new Payment();
        payment.setId(paymentId);

        PaymentExecution paymentExecute = new PaymentExecution();
        paymentExecute.setPayerId(payerId);

        return payment.execute(apiContext, paymentExecute);
    }
}