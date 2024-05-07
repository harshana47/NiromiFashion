package lk.ijse.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CustomerDTO {
    private String customerId;
    private String name;
    private String email;
    private String phone;
}
