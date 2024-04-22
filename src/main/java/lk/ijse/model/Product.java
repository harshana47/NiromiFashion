package lk.ijse.model;

public class Product {
    private String productId;
    private String name; // New attribute
    private String expireDate;
    private Double price;
    private int qtyOnHand;
    private String employeeId;

    public Product() {
    }

    public Product(String productId, String name, String expireDate, double price, int qtyOnHand, String employeeId) {
        this.productId = productId;
        this.name = name;
        this.expireDate = expireDate;
        this.price = price;
        this.qtyOnHand = qtyOnHand;
        this.employeeId = employeeId;
    }

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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
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

    @Override
    public String toString() {
        return "Product{" +
                "productId='" + productId + '\'' +
                ", name='" + name + '\'' +
                ", expireDate='" + expireDate + '\'' +
                ", price=" + price +
                ", qtyOnHand=" + qtyOnHand +
                ", employeeId='" + employeeId + '\'' +
                '}';
    }
}
