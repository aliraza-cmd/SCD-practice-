package shop;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Talks to an external payment provider over HTTP.
 * Slow, needs network, needs credentials: unsuitable for unit tests.
 */
public class PaymentGateway {
    private static final int TIMEOUT_MS = 5000;

    private final String endpoint;

    public PaymentGateway() {
        this("https://api.example-payments.com/v1/charges");
    }

    public PaymentGateway(String endpoint) {
        this.endpoint = endpoint; // no network activity until charge() is called
    }

    public PaymentResult charge(String cardNumber, BigDecimal amount) {
        String body = String.format(
                "{\"card\":\"%s\",\"amount\":\"%s\"}", cardNumber, amount.toPlainString());

        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(endpoint).openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setConnectTimeout(TIMEOUT_MS);
            connection.setReadTimeout(TIMEOUT_MS);
            connection.setDoOutput(true);

            try (OutputStream out = connection.getOutputStream()) {
                out.write(body.getBytes(StandardCharsets.UTF_8));
            }

            int status = connection.getResponseCode();
            if (status == 200) {
                // Real code would parse the JSON response; simplified here.
                return new PaymentResult(true, "TXN-" + System.nanoTime(), "Approved");
            }
            return new PaymentResult(false, null, "Declined: HTTP " + status);
        } catch (IOException e) {
            throw new PaymentException("Payment provider unreachable", e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}