package chipization.services;

import chipization.exceptions.EntityForbiddenException;
import chipization.exceptions.EntityNotAuthorizedException;
import chipization.model.User;
import chipization.model.enums.UserRole;
import chipization.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
@RequiredArgsConstructor
public class AuthorizationService {
    private final UserRepository userRepository;

    public void checkAuthorization(String auth) {
        if (auth == null) {
            throw new EntityNotAuthorizedException("Анонимный пользователь, запрос недоступен");
        }
        userRepository.authorization(getLogin(auth), getPassword(auth))
                .orElseThrow(() -> new EntityNotAuthorizedException("Пользователь с таким логином и паролем не найден"));
    }

    public void checkAuthorizationForUserWithoutAdminRights(String auth, Integer accountId) {
        if (getLoginAndPassword(auth) != null) {
            checkForUserOrChipper(userRepository.authorization(getLoginAndPassword(auth)[0], getLoginAndPassword(auth)[1])
                    .orElseThrow(() -> new EntityNotAuthorizedException("Пользователь с таким логином и паролем не найден")), accountId);
        }
    }

    public void checkAuthorizationForAdminRights(String auth) {
        if (getLoginAndPassword(auth) != null) {
            checkForAdmin(userRepository.authorization(getLoginAndPassword(auth)[0], getLoginAndPassword(auth)[1])
                    .orElseThrow(() -> new EntityNotAuthorizedException("Пользователь с таким логином и паролем не найден")));
        }
    }

    public void checkAuthorizationForAdminOrChipperRights(String auth) {
        if (getLoginAndPassword(auth) != null) {
            checkForAdminOrChipper(userRepository.authorization(getLoginAndPassword(auth)[0], getLoginAndPassword(auth)[1])
                    .orElseThrow(() -> new EntityNotAuthorizedException("Пользователь с таким логином и паролем не найден")));
        }
    }

    private String[] getLoginAndPassword(String auth) {
        if (auth != null) {
            auth = auth.substring(auth.indexOf(' ') + 1);
        } else return null;

        byte[] decodedBytes = Base64.getDecoder().decode(auth);
        String result = new String(decodedBytes);
        return result.split(":");
    }

    private void checkForUserOrChipper(User verifiableUser, Integer accountId) {
        if (accountId != verifiableUser.getId()
                && (verifiableUser.getRole() == UserRole.USER
                || verifiableUser.getRole() == UserRole.CHIPPER)) {
            throw new EntityForbiddenException("Нет прав на такой запрос");
        }
    }

    private void checkForAdmin(User verifiableUser) {
        if (verifiableUser.getRole() != UserRole.ADMIN) {
            throw new EntityForbiddenException("У пользователя нет админских прав");
        }
    }

    private void checkForAdminOrChipper(User verifiableUser) {
        if (verifiableUser.getRole() != UserRole.ADMIN
                && verifiableUser.getRole() != UserRole.CHIPPER) {
            throw new EntityForbiddenException("Нет прав на такой запрос у USER");
        }
    }

    private String getLogin(String auth) {
        auth = auth.substring(auth.indexOf(' ') + 1);
        byte[] decodedBytes = Base64.getDecoder().decode(auth);
        String result = new String(decodedBytes);
        String[] res = result.split(":");
        return res[0];
    }

    private String getPassword(String auth) {
        auth = auth.substring(auth.indexOf(' ') + 1);
        byte[] decodedBytes = Base64.getDecoder().decode(auth);
        String result = new String(decodedBytes);
        String[] res = result.split(":");
        return res[1];
    }
}
