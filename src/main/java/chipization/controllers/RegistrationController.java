package chipization.controllers;

import chipization.model.User;
import chipization.model.dto.UserDto;
import chipization.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/registration")
@RequiredArgsConstructor
public class RegistrationController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> registrationUser(@RequestHeader(value = "Authorization", required = false) String auth, @Valid @RequestBody User user) {
        return new ResponseEntity<>(userService.registrationUser(user), HttpStatus.CREATED);
    }

    @GetMapping
    public String get(@RequestHeader(value = "Authorization", required = false) String auth) {
        return userService.getText(auth);
    }
}
