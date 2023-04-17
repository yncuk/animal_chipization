package chipization.controllers;

import chipization.model.User;
import chipization.model.dto.GetUsersRequest;
import chipization.model.dto.UserDto;
import chipization.services.AccountService;
import chipization.services.AuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
@Validated
public class AccountController {

    private final AccountService accountService;
    private final AuthorizationService authorizationService;

    @GetMapping("/{accountId}")
    public ResponseEntity<UserDto> findById(@RequestHeader(value = "Authorization", required = false) String auth,
                                            @PathVariable Integer accountId) {
        authorizationService.checkAuthorizationForUserWithoutAdminRights(auth, accountId);
        return ResponseEntity.ok(accountService.findById(accountId));
    }

    @GetMapping("/search")
    public ResponseEntity<Collection<UserDto>> search(@RequestHeader(value = "Authorization", required = false) String auth,
                                                      @RequestParam(required = false) String firstName,
                                                      @RequestParam(required = false) String lastName,
                                                      @RequestParam(required = false) String email,
                                                      @RequestParam(defaultValue = "0") Integer from,
                                                      @RequestParam(defaultValue = "10") Integer size) {
        authorizationService.checkAuthorizationForAdminRights(auth);
        return ResponseEntity.ok(accountService.search(GetUsersRequest.of(firstName, lastName, email, from, size)));
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestHeader(value = "Authorization", required = false) String auth,
                                              @Valid @RequestBody User user) {
        authorizationService.checkAuthorization(auth);
        return new ResponseEntity<>(accountService.createUser(user), HttpStatus.CREATED);
    }

    @PutMapping("/{accountId}")
    public ResponseEntity<UserDto> update(@RequestHeader(value = "Authorization", required = false) String auth,
                                          @RequestBody @Valid User user, @PathVariable Integer accountId) {
        authorizationService.checkAuthorizationForUserWithoutAdminRights(auth, accountId);
        return ResponseEntity.ok(accountService.update(auth, accountId, user));
    }

    @DeleteMapping("/{accountId}")
    public void delete(@RequestHeader(value = "Authorization", required = false) String auth,
                       @PathVariable Integer accountId) {
        authorizationService.checkAuthorizationForUserWithoutAdminRights(auth, accountId);
        accountService.delete(auth, accountId);
    }
}
