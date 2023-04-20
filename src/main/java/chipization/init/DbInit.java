package chipization.init;

import chipization.model.User;
import chipization.model.enums.UserRole;
import chipization.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class DbInit {
    @Autowired
    private UserRepository userRepository;

    @PostConstruct
    private void postConstruct() {
        User admin = new User();
        admin.setId(1);
        admin.setFirstName("adminFirstName");
        admin.setLastName("adminLastName");
        admin.setEmail("admin@simbirsoft.com");
        admin.setPassword("qwerty123");
        admin.setRole(UserRole.ADMIN);

        User chipper = new User();
        chipper.setId(2);
        chipper.setFirstName("chipperFirstName");
        chipper.setLastName("chipperLastName");
        chipper.setEmail("chipper@simbirsoft.com");
        chipper.setPassword("qwerty123");
        chipper.setRole(UserRole.CHIPPER);

        User user = new User();
        user.setId(3);
        user.setFirstName("userFirstName");
        user.setLastName("userLastName");
        user.setEmail("user@simbirsoft.com");
        user.setPassword("qwerty123");
        user.setRole(UserRole.USER);

        userRepository.save(admin);
        userRepository.save(chipper);
        userRepository.save(user);
    }
}
