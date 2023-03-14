package chipization.services;

import chipization.model.User;
import chipization.model.dto.UserDto;

public interface UserService {
    UserDto registrationUser(User user);

    Boolean isAuthorized(String auth);
}