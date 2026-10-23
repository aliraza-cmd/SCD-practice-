package shop;

public class PaymentResult {
    private final boolean approved;
    private final String transactionId;
    private final String message;

    public PaymentResult(boolean approved, String transactionId, String message) {
        this.approved = approved;
        this.transactionId = transactionId;
        this.message = message;
    }

    public boolean isApproved() { return approved; }
    public String getTransactionId() { return transactionId; }
    public String getMessage() { return message; }
}