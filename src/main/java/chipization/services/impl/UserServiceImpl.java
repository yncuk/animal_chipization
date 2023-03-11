package chipization.services.impl;

import chipization.mappers.UserMapper;
import chipization.model.User;
import chipization.model.dto.UserDto;
import chipization.repositories.UserRepository;
import chipization.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto registrationUser(User user) {
        return UserMapper.toUsetDto(userRepository.save(user));
    }

    @Override
    public Boolean isAuthorized(String auth) {
        if (auth != null) {
            auth = auth.substring(auth.indexOf(' ') + 1);
        } else return false;
        String result = new String(Base64.getDecoder().decode(auth));
        String[] res = result.split(":");
        String login = res[0];
        String password = res[1];
        return userRepository.authorization(login, password).isPresent();
    }
}
