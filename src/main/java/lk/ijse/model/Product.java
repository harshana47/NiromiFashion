package lk.ijse.model;

import lombok.Builder;

@Builder
public class Product {
    private String productId;
    private String name;
    private String expireDate;
    private Double price;
    private int qtyOnHand;
    private String employeeId;
    private String promoId;

    public Product() {
    }

    public Product(String productId, String name, String expireDate, Double price, int qtyOnHand, String employeeId, String promoId) {
        this.productId = productId;
        this.name = name;
        this.expireDate = expireDate;
        this.price = price;
        this.qtyOnHand = qtyOnHand;
        this.employeeId = employeeId;
        this.promoId = promoId;
    }

    // Getters and setters
    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public int getQtyOnHand() {
        return qtyOnHand;
    }

    public void setQtyOnHand(int qtyOnHand) {
        this.qtyOnHand = qtyOnHand;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getPromoId() {
        return promoId;
    }

    public void setPromoId(String promoId) {
        this.promoId = promoId;
    }

    @Override
    public String toString() {
        return "Product{" +
                "productId='" + productId + '\'' +
                ", name='" + name + '\'' +
                ", expireDate='" + expireDate + '\'' +
                ", price=" + price +
                ", qtyOnHand=" + qtyOnHand +
                ", employeeId='" + employeeId + '\'' +
                ", promoId='" + promoId + '\'' +
                '}';
    }
}
