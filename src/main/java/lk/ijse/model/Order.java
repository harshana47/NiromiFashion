package lk.ijse.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private String orderId;
    private LocalDate orderDate;
    private Double totalAmount; // Change to BigDecimal
    private String customerId;
    private String paymentId;
    private String promoId;
    private List<OrderItem> orderItems;
    private String expireDiscountStatus; // Corrected naming convention

    public String getExpireDiscountStatus() {
        return expireDiscountStatus; // Corrected variable name
    }

    public void setExpireDiscountStatus(String expireDiscountStatus) {
        this.expireDiscountStatus = expireDiscountStatus; // Corrected variable name
    }

    public Order(String orderId, LocalDate orderDate, Double totalAmount, String customerId, String paymentId, String promoId, String expireDiscountStatus) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount; // Change parameter type to BigDecimal
        this.customerId = customerId;
        this.paymentId = paymentId;
        this.promoId = promoId;
        this.orderItems = new ArrayList<>();
        this.expireDiscountStatus = expireDiscountStatus; // Corrected variable name
    }

    public Order() {
    }

    public Order(String orderId, LocalDate orderDate, Double totalAmount, String customerId, String paymentId, String promoId, List<OrderItem> orderItems, String expireDiscountStatus) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.customerId = customerId;
        this.paymentId = paymentId;
        this.promoId = promoId;
        this.orderItems = orderItems;
        this.expireDiscountStatus = expireDiscountStatus;
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

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", orderDate=" + orderDate +
                ", totalAmount=" + totalAmount +
                ", customerId='" + customerId + '\'' +
                ", paymentId='" + paymentId + '\'' +
                ", promoId='" + promoId + '\'' +
                ", orderItems=" + orderItems +
                ", expireDiscountStatus='" + expireDiscountStatus + '\'' +
                '}';
    }
}
