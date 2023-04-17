package chipization.controllers;

import chipization.model.User;
import chipization.model.dto.AreaDto;
import chipization.model.dto.UserDto;
import chipization.services.AreaService;
import chipization.services.AuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/areas")
@RequiredArgsConstructor
@Validated
public class AreaController {
    private final AreaService areaService;
    private final AuthorizationService authorizationService;

    @GetMapping("/{areaId}")
    public ResponseEntity<AreaDto> findById(@RequestHeader(value = "Authorization", required = false) String auth,
                                            @PathVariable Long areaId) {
        authorizationService.checkAuthorization(auth);
        return ResponseEntity.ok(areaService.findById(areaId));
    }

    @PostMapping
    public ResponseEntity<AreaDto> createUser(@RequestHeader(value = "Authorization", required = false) String auth,
                                              @Valid @RequestBody AreaDto areaDto) {
        authorizationService.checkAuthorizationForAdminRights(auth);
        return new ResponseEntity<>(areaService.createArea(areaDto), HttpStatus.CREATED);
    }

    @PutMapping("/{areaId}")
    public ResponseEntity<AreaDto> update(@RequestHeader(value = "Authorization", required = false) String auth,
                                          @RequestBody @Valid AreaDto areaDto, @PathVariable Long areaId) {
        authorizationService.checkAuthorizationForAdminRights(auth);
        return ResponseEntity.ok(areaService.update(areaId, areaDto));
    }

    @DeleteMapping("/{areaId}")
    public void delete(@RequestHeader(value = "Authorization", required = false) String auth,
                       @PathVariable Long areaId) {
        authorizationService.checkAuthorizationForAdminRights(auth);
        areaService.delete(areaId);
    }
}
