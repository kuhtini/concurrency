package course.concurrency.m3_shared.immutable;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class OrderService {

    private final ConcurrentMap<Long, Order> currentOrders = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(0);

    private long nextId() {
        return nextId.getAndIncrement();
    }

    public long createOrder(List<Item> items) {
        long id = nextId();
        Order order = Order.from(id, items);
        currentOrders.put(id, order);
        return id;
    }

    public void updatePaymentInfo(long orderId, PaymentInfo paymentInfo) {
        currentOrders.computeIfPresent(orderId, (key, order) -> Order.from(order, paymentInfo));
        if (currentOrders.get(orderId).checkStatus()) {
            deliver(currentOrders.get(orderId));
        }
    }

    public void setPacked(long orderId) {
        currentOrders.computeIfPresent(orderId, (key, order) -> Order.from(order, true));
        if (currentOrders.get(orderId).checkStatus()) {
            deliver(currentOrders.get(orderId));
        }
    }

    private void deliver(Order order) {
        currentOrders.computeIfPresent(order.getId(), (key, order1) -> Order.from(order1, Order.Status.DELIVERED));

    }

    public boolean isDelivered(long orderId) {
        return currentOrders.get(orderId).getStatus().equals(Order.Status.DELIVERED);
    }
}
