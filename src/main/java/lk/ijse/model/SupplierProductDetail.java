package lk.ijse.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierProductDetail {
    private String supplierProductId;
    private String productId;
    private String supplierId;

    // Getters and setters
}
