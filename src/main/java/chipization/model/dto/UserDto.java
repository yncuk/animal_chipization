package chipization.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDto {

    private int id;
    private String firstName;
    private String lastName;
    private String email;
}
