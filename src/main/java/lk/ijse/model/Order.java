package lk.ijse.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private String orderId;
    private LocalDate orderDate;
    private Double totalAmount;
    private String customerId; // Corrected from cusId
    private String paymentId;
    private String promoId;
    private String expireDiscountStatus; // Corrected from ExpireDiscountStatus
    private List<OrderProductDetail> orderItems; // Change to OrderProductDetail
    private List<Product> product; // Change to List<Product>

    public Order(String orderId, LocalDate orderDate, Double totalAmount, String customerId, String paymentId, String promoId, String expireDiscountStatus) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.customerId = customerId;
        this.paymentId = paymentId;
        this.promoId = promoId;
        this.expireDiscountStatus = expireDiscountStatus;
        this.orderItems = new ArrayList<>(); // Initialize to an empty ArrayList
        this.product = new ArrayList<>(); // Initialize to an empty ArrayList
    }

    // Getters and setters
    public List<Product> getProduct() {
        return product;
    }

    public void setProduct(List<Product> product) {
        this.product = product;
    }

    public String getExpireDiscountStatus() {
        return expireDiscountStatus; // Corrected variable name
    }

    public void setExpireDiscountStatus(String expireDiscountStatus) {
        this.expireDiscountStatus = expireDiscountStatus; // Corrected variable name
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

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
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

    public List<OrderProductDetail> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderProductDetail> orderItems) {
        this.orderItems = orderItems;
    }

    // Other methods for adding/removing items from orderItems list
    public void addItem(OrderProductDetail item) {
        orderItems.add(item);
    }

    public void removeItem(OrderProductDetail item) {
        orderItems.remove(item);
    }
}
