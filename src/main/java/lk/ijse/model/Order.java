package lk.ijse.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private String orderId;
    private LocalDate orderDate;
    private double totalAmount;
    private String customerId;
    private String paymentId;
    private String promoId;
    private List<OrderItem> orderItems;
    private String ExpireDiscountStatus;

    public String getExpireDiscountStatus() {
        return ExpireDiscountStatus;
    }

    public void setExpireDiscountStatus(String expireDiscountStatus) {
        ExpireDiscountStatus = expireDiscountStatus;
    }

    public Order(String orderId, LocalDate orderDate, double totalAmount, String customerId, String paymentId, String promoId, String ExpireDiscountStatus) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.customerId = customerId;
        this.paymentId = paymentId;
        this.promoId = promoId;
        this.orderItems = new ArrayList<>();
        this.ExpireDiscountStatus = ExpireDiscountStatus;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getPromoId() {
        return promoId;
    }

    public void setPromoId(String promoId) {
        this.promoId = promoId;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public void addItem(String productId, int quantity, double itemPrice) {
        OrderItem item = new OrderItem(productId, quantity, itemPrice);
        orderItems.add(item);
    }

    public void removeItem(String productId) {
        orderItems.removeIf(item -> item.getProductId().equals(productId));
    }

    public OrderItem findItem(String productId) {
        return orderItems.stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .orElse(null);
    }
}
