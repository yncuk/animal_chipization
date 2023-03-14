package chipization.controllers;

import chipization.exceptions.EntityForbiddenException;
import chipization.model.User;
import chipization.model.dto.UserDto;
import chipization.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/registration")
@RequiredArgsConstructor
@Validated
public class RegistrationController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> registrationUser(@RequestHeader(value = "Authorization", required = false) String auth,
                                                    @Valid @RequestBody User user) {
        if (userService.isAuthorized(auth)) {
            throw new EntityForbiddenException("Пользователь уже авторизован");
        }
        return new ResponseEntity<>(userService.registrationUser(user), HttpStatus.CREATED);
    }
}
