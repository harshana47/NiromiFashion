package lk.ijse.model;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@ToString
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderProductDetail {
    private String orderId;
    private String productId;
    private int qty;
    private double total;
    private LocalDate orderDate; // New field for order date

}
