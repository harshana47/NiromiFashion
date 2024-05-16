package lk.ijse.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
public class User {
    private String userId;
    private String name;
    private String password;
    private String phone;

    public User(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }
}
