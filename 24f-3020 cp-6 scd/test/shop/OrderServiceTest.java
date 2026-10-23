package shop;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OrderServiceTest {

    private static final String CARD = "4111111111111111";
    private Order order;

    static class StubPaymentGateway extends PaymentGateway {
        private final PaymentResult cannedResult;
        String lastCardNumber;
        BigDecimal lastAmount;
        int callCount = 0;

        StubPaymentGateway(PaymentResult cannedResult) {
            this.cannedResult = cannedResult;
        }

        @Override
        public PaymentResult charge(String cardNumber, BigDecimal amount) {
            callCount++;
            this.lastCardNumber = cardNumber;
            this.lastAmount = amount;
            return cannedResult;
        }
    }

    static class UnreachablePaymentGateway extends PaymentGateway {
        @Override
        public PaymentResult charge(String cardNumber, BigDecimal amount) {
            throw new PaymentException("Payment provider unreachable", new IOException("timeout"));
        }
    }

    @BeforeEach
    void setUp() {
        order = new Order("customer-42", CARD);
    }

    private static StubPaymentGateway approvingGateway() {
        return new StubPaymentGateway(new PaymentResult(true, "TXN-123", "Approved"));
    }

    @Test
    @DisplayName("Approved payment yields a CONFIRMED order with the transaction id")
    void approvedPaymentConfirmsOrder() {
        order.addItem(new OrderItem("Book", new BigDecimal("50.00"), 1));
        StubPaymentGateway gateway = approvingGateway();
        OrderService service = new OrderService(gateway);

        OrderConfirmation confirmation = service.placeOrder(order);

        assertEquals(OrderStatus.CONFIRMED, confirmation.getStatus());
        assertEquals(new BigDecimal("50.00"), confirmation.getAmountCharged());
        assertEquals("TXN-123", confirmation.getTransactionId());
        assertEquals(1, gateway.callCount);
    }

    @Test
    @DisplayName("Declined payment yields DECLINED and charges nothing")
    void declinedPaymentDeclinesOrder() {
        order.addItem(new OrderItem("Book", new BigDecimal("50.00"), 1));
        StubPaymentGateway gateway = new StubPaymentGateway(
                new PaymentResult(false, null, "Declined: Insufficient funds"));
        OrderService service = new OrderService(gateway);

        OrderConfirmation confirmation = service.placeOrder(order);

        assertEquals(OrderStatus.DECLINED, confirmation.getStatus());
        assertEquals(new BigDecimal("0.00"), confirmation.getAmountCharged());
        assertNull(confirmation.getTransactionId());
        assertEquals(1, gateway.callCount);
    }

    @Test
    @DisplayName("Gateway outage is handled and reported as PAYMENT_ERROR")
    void gatewayOutageIsReportedAsPaymentError() {
        order.addItem(new OrderItem("Book", new BigDecimal("50.00"), 1));
        UnreachablePaymentGateway gateway = new UnreachablePaymentGateway();
        OrderService service = new OrderService(gateway);

        OrderConfirmation confirmation = service.placeOrder(order);

        assertEquals(OrderStatus.PAYMENT_ERROR, confirmation.getStatus());
        assertEquals(new BigDecimal("0.00"), confirmation.getAmountCharged());
        assertNull(confirmation.getTransactionId());
    }

    @Test
    @DisplayName("Orders of 100.00 or more get a 10% discount before charging")
    void discountAppliedAtThreshold() {
        order.addItem(new OrderItem("Gadget", new BigDecimal("100.00"), 1));
        StubPaymentGateway gateway = approvingGateway();
        OrderService service = new OrderService(gateway);

        OrderConfirmation confirmation = service.placeOrder(order);

        assertEquals(new BigDecimal("90.00"), confirmation.getAmountCharged());
        assertEquals(new BigDecimal("90.00"), gateway.lastAmount);
    }

    @Test
    @DisplayName("Orders below 100.00 are not discounted")
    void noDiscountBelowThreshold() {
        order.addItem(new OrderItem("Gadget", new BigDecimal("99.99"), 1));
        StubPaymentGateway gateway = approvingGateway();
        OrderService service = new OrderService(gateway);

        OrderConfirmation confirmation = service.placeOrder(order);

        assertEquals(new BigDecimal("99.99"), confirmation.getAmountCharged());
        assertEquals(new BigDecimal("99.99"), gateway.lastAmount);
    }

    @Test
    @DisplayName("The customer's card number is passed through to the gateway")
    void cardNumberIsForwarded() {
        order.addItem(new OrderItem("Book", new BigDecimal("50.00"), 1));
        StubPaymentGateway gateway = approvingGateway();
        OrderService service = new OrderService(gateway);

        service.placeOrder(order);

        assertEquals(CARD, gateway.lastCardNumber);
    }

    @Test
    @DisplayName("Empty orders are rejected and the gateway is never called")
    void emptyOrderIsRejected() {
        StubPaymentGateway gateway = approvingGateway();
        OrderService service = new OrderService(gateway);

        assertThrows(IllegalArgumentException.class, () -> service.placeOrder(order));
        assertEquals(0, gateway.callCount);
    }
}
