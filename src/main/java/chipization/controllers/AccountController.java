package chipization.controllers;

import chipization.model.User;
import chipization.model.dto.GetUsersRequest;
import chipization.model.dto.UserDto;
import chipization.services.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.Collection;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
@Validated
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/{accountId}")
    public ResponseEntity<UserDto> findById(@RequestHeader(value = "Authorization", required = false) String auth, @PathVariable @Min(1) Integer accountId) {
        return ResponseEntity.ok(accountService.findById(accountId));
    }

    @GetMapping("/search")
    public Collection<UserDto> search(@RequestParam(required = false) String firstName,
                                      @RequestParam(required = false) String lastName,
                                      @RequestParam(required = false) String email,
                                      @RequestParam(defaultValue = "0") Integer from,
                                      @RequestParam(defaultValue = "10") Integer size) {
        return accountService.search(GetUsersRequest.of(firstName, lastName, email, from, size));
    }

    @PutMapping("/{accountId}")
    public ResponseEntity<UserDto> update(@RequestHeader(value = "Authorization", required = false) String auth, @RequestBody User user, @PathVariable Integer accountId) {
        return ResponseEntity.ok(accountService.update(accountId, user));
    }

    @DeleteMapping("/{accountId}")
    public void delete(@RequestHeader(value = "Authorization", required = false) String auth, @PathVariable Integer accountId) {
        accountService.delete(accountId);
    }

}
