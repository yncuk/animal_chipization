package chipization.mappers;

import chipization.model.User;
import chipization.model.dto.UserDto;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.Collectors;

@Component
public class UserMapper {
    public static UserDto toUsetDto(User user) {
        return new UserDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail()
        );
    }

    public static User toUser(UserDto userDto) {
        User user = new User();
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        return user;
    }

    public static Collection<UserDto> allToUserDto(Collection<User> users) {
        return users.stream().map(UserMapper::toUsetDto).collect(Collectors.toList());
    }

}
