package chipization.controllers;

import chipization.model.Location;
import chipization.services.AccountService;
import chipization.services.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/locations")
@RequiredArgsConstructor
@Validated
public class LocationController {

    private final LocationService locationService;
    private final AccountService accountService;


    @GetMapping("/{pointId}")
    public ResponseEntity<Location> findById(@RequestHeader(value = "Authorization", required = false) String auth,
                                             @PathVariable Long pointId) {
        accountService.checkAuthorizationForGet(auth);
        return ResponseEntity.ok(locationService.findById(pointId));
    }

    @PostMapping
    public ResponseEntity<Location> create(@RequestHeader(value = "Authorization", required = false) String auth,
                                           @Valid @RequestBody Location location) {
        accountService.checkAuthorization(auth);
        return new ResponseEntity<>(locationService.create(location), HttpStatus.CREATED);
    }

    @PutMapping("/{pointId}")
    public ResponseEntity<Location> update(@RequestHeader(value = "Authorization", required = false) String auth,
                                           @Valid @RequestBody Location location, @PathVariable Long pointId) {
        accountService.checkAuthorization(auth);
        return ResponseEntity.ok(locationService.update(pointId, location));
    }

    @DeleteMapping("/{pointId}")
    public void delete(@RequestHeader(value = "Authorization", required = false) String auth,
                       @PathVariable Long pointId) {
        accountService.checkAuthorization(auth);
        locationService.delete(pointId);
    }
}
