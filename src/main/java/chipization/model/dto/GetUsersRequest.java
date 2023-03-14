package chipization.model.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetUsersRequest {
    String firstName;
    String lastName;
    String email;
    int from;
    int size;

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
