package lk.ijse.model;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@ToString
@Data
@Builder
public class OrderProductDetail {
    private String orderId;
    private String productId;
    private int qty;
    private double discount;
    private double price;
}
