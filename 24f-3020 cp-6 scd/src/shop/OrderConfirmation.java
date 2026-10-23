package shop;

import java.math.BigDecimal;

public class OrderConfirmation {
    private final OrderStatus status;
    private final BigDecimal amountCharged;
    private final String transactionId;

    public OrderConfirmation(OrderStatus status, BigDecimal amountCharged, String transactionId) {
        this.status = status;
        this.amountCharged = amountCharged;
        this.transactionId = transactionId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public BigDecimal getAmountCharged() {
        return amountCharged;
    }

    public String getTransactionId() {
        return transactionId;
    }
}