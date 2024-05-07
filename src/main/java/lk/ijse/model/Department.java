// Department.java (model class)
package lk.ijse.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Data
public class Department {
    private String depId;
    private String name;
    private int staffCount;
}
