package lk.ijse.model;

public class OrderProductDetail {
    private String orderId;
    private String productId;
    private String details;

    public OrderProductDetail() {
    }

    public OrderProductDetail(String orderId, String productId, String details) {
        this.orderId = orderId;
        this.productId = productId;
        this.details = details;
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

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
