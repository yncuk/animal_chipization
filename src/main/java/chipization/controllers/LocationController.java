package chipization.controllers;

import chipization.model.Location;
import chipization.model.dto.LocationDto;
import chipization.model.dto.LocationDtoResponse;
import chipization.services.AuthorizationService;
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

    private final AuthorizationService authorizationService;


    @GetMapping("/{pointId}")
    public ResponseEntity<LocationDtoResponse> findById(@RequestHeader(value = "Authorization", required = false) String auth,
                                                        @PathVariable Long pointId) {
        authorizationService.checkAuthorization(auth);
        return ResponseEntity.ok(locationService.findById(pointId));
    }

    @PostMapping
    public ResponseEntity<LocationDtoResponse> create(@RequestHeader(value = "Authorization", required = false) String auth,
                                                      @Valid @RequestBody LocationDto locationDto) {
        authorizationService.checkAuthorizationForAdminOrChipperRights(auth);
        return new ResponseEntity<>(locationService.create(locationDto), HttpStatus.CREATED);
    }

    @PutMapping("/{pointId}")
    public ResponseEntity<LocationDtoResponse> update(@RequestHeader(value = "Authorization", required = false) String auth,
                                           @Valid @RequestBody LocationDto locationDto, @PathVariable Long pointId) {
        authorizationService.checkAuthorizationForAdminOrChipperRights(auth);
        return ResponseEntity.ok(locationService.update(pointId, locationDto));
    }

    @DeleteMapping("/{pointId}")
    public void delete(@RequestHeader(value = "Authorization", required = false) String auth,
                       @PathVariable Long pointId) {
        authorizationService.checkAuthorizationForAdminRights(auth);
        locationService.delete(pointId);
    }
}
