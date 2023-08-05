package course.concurrency.m3_shared.immutable;

import java.util.List;

import static course.concurrency.m3_shared.immutable.Order.Status.NEW;

public final class Order {

    public static Order from(Order order, boolean isPacked) {
        return new Order(order.id, order.items, isPacked, order.paymentInfo, order.status);
    }

    public static Order from(Order order, PaymentInfo paymentInfo) {
        return new Order(order.id, order.items, order.isPacked, paymentInfo, order.status);
    }

    public static Order from(Order order, Status status) {
        return new Order(order.id, order.items, order.isPacked, order.paymentInfo, status);
    }

    public static Order from(Long id, List<Item> items) {
        return new Order(id, items, false, null, NEW);
    }

    public enum Status {NEW, IN_PROGRESS, DELIVERED}

    private final Long id;
    private final List<Item> items;
    private final PaymentInfo paymentInfo;
    private final boolean isPacked;
    private final Status status;

    public Order(Long id, List<Item> items, boolean isPacked, PaymentInfo paymentInfo, Status status) {
        this.id = id;
        this.items = items;
        this.status = status != null ? status : NEW;
        this.isPacked = isPacked;
        this.paymentInfo = paymentInfo;
    }

    public boolean checkStatus() {
        return items != null && !items.isEmpty() && paymentInfo != null && isPacked;
    }

    public Long getId() {
        return id;
    }

    public List<Item> getItems() {
        return items;
    }

    public PaymentInfo getPaymentInfo() {
        return paymentInfo;
    }

    public boolean isPacked() {
        return isPacked;
    }

    public Status getStatus() {
        return status;
    }

}
