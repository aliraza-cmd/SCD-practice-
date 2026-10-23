package shop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Order {
    private final String customerId;
    private final String cardNumber;
    private final List<OrderItem> items = new ArrayList<>();

    public Order(String customerId, String cardNumber) {
        this.customerId = customerId;
        this.cardNumber = cardNumber;
    }

    public void addItem(OrderItem item) {
        items.add(item);
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }
}