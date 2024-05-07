package lk.ijse.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Data
public class DepartmentDTO {
    private String depId;
    private String name;
    private int staffCount;
}
