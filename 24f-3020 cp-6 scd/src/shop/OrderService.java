package shop;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class OrderService {
    private static final BigDecimal DISCOUNT_THRESHOLD = new BigDecimal("100.00");
    private static final BigDecimal DISCOUNT_RATE = new BigDecimal("0.10");

    private final PaymentGateway paymentGateway;

    public OrderService() {
        this(new PaymentGateway());
    }

    public OrderService(PaymentGateway paymentGateway) {
        this.paymentGateway = paymentGateway;
    }

    public OrderConfirmation placeOrder(Order order) {
        if (order.getItems().isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }

        BigDecimal total = calculateTotal(order);

        try {
            PaymentResult result = paymentGateway.charge(order.getCardNumber(), total);
            if (result.isApproved()) {
                return new OrderConfirmation(OrderStatus.CONFIRMED, total, result.getTransactionId());
            }
            return new OrderConfirmation(OrderStatus.DECLINED, BigDecimal.ZERO.setScale(2), null);
        } catch (PaymentException e) {
            return new OrderConfirmation(OrderStatus.PAYMENT_ERROR, BigDecimal.ZERO.setScale(2), null);
        }
    }

    /** Sum of subtotals, with 10% off when the sum is at least 100.00. */
    BigDecimal calculateTotal(Order order) {
        BigDecimal subtotal = order.getItems().stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (subtotal.compareTo(DISCOUNT_THRESHOLD) >= 0) {
            subtotal = subtotal.subtract(subtotal.multiply(DISCOUNT_RATE));
        }
        return subtotal.setScale(2, RoundingMode.HALF_UP);
    }
}