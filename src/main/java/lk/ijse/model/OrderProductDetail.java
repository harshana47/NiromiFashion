package lk.ijse.model;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@ToString
@Data
@Builder
public class OrderProductDetail {
    private String orderId;
    private String productId;
    private int qty;
    private double total;
    private LocalDate orderDate; // New field for order date

}
