package lk.ijse.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Supplier {
    private String supplierId;
    private String name;
    private String address;
    private String contact;
    private String email;
    private List<SupplierProductDetail> SupplierProductDetailList;

    public Supplier(String supplierId, String name, String address, String contact, String email) {
        this.supplierId = supplierId;
        this.name = name;
        this.address = address;
        this.contact = contact;
        this.email = email;
    }
}
