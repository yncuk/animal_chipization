package chipization.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GetUsersRequest {
    private String firstName;
    private String lastName;
    private String email;
    private int from;
    private int size;

    public static GetUsersRequest of(String firstName,
                                     String lastName,
                                     String email,
                                     int from,
                                     int size) {
        GetUsersRequest request = new GetUsersRequest();
        request.setFirstName(firstName);
        request.setLastName(lastName);
        request.setEmail(email);
        request.setFrom(from);
        request.setSize(size);
        return request;
    }

}
