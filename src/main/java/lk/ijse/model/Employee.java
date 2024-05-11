package lk.ijse.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Data

public class Employee {
    private String employeeId;
    private String name;
    private String depId;
    private String position;
    private String duty;
    private String email;

}
