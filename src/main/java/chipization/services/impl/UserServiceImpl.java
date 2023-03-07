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
    public String getText(String text) {
        String result;
        text = text.substring(text.indexOf(' ') + 1);
        //byte[] encodedBytes = Base64.getEncoder().encode("Test".getBytes());
        //System.out.println("encodedBytes " + new String(encodedBytes));
        byte[] decodedBytes = Base64.getDecoder().decode(text);
        result = new String(decodedBytes);
        String[] auth = result.split(":");
        String login = auth[0];
        String password = auth[1];
        System.out.println(login + " " + password);
        return result;
    }
}
