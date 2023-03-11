package chipization.services;

import chipization.model.User;
import chipization.model.dto.GetUsersRequest;
import chipization.model.dto.UserDto;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public interface AccountService {

    UserDto findById(Integer accountId);

    Collection<UserDto> search(GetUsersRequest request);

    UserDto update(String auth, Integer accountId, User user);

    void delete(String auth, Integer accountId);

    void checkAuthorization(String auth);

}
