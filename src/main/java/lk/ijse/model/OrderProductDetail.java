package lk.ijse.model;

import lombok.ToString;

@ToString
public class OrderProductDetail {
    private String orderId;
    private String productId;
    private int qty;
    private double discount;
    private double price;

    public OrderProductDetail(String orderId, String productId, int qty , double discount) {
        this.orderId = orderId;
        this.productId = productId;
        this.qty = qty;
        this.discount = discount;
        this.price = price;
    }

    // Getters and setters
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
