package chipization.controllers;

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

    @GetMapping
    public ResponseEntity<Long> findByLatitudeAndLongitude(@RequestHeader(value = "Authorization", required = false) String auth,
                                                           @RequestParam Double latitude,
                                                           @RequestParam Double longitude) {
        authorizationService.checkAuthorization(auth);
        return ResponseEntity.ok(locationService.findByLatitudeAndLongitude(latitude, longitude).getId());
    }

    @GetMapping("/geohash")
    public ResponseEntity<String> findByLatitudeAndLongitudeGeohash(@RequestHeader(value = "Authorization", required = false) String auth,
                                                                    @RequestParam Double latitude,
                                                                    @RequestParam Double longitude) {
        authorizationService.checkAuthorization(auth);
        return ResponseEntity.ok(locationService.getGeoHash(latitude, longitude));
    }

    @GetMapping("/geohashv2")
    public ResponseEntity<String> findByLatitudeAndLongitudeGeohashv2(@RequestHeader(value = "Authorization", required = false) String auth,
                                                                      @RequestParam Double latitude,
                                                                      @RequestParam Double longitude) {
        authorizationService.checkAuthorization(auth);
        return ResponseEntity.ok(locationService.getGeoHashInBase64(latitude, longitude));
    }

    @GetMapping("/geohashv3")
    public ResponseEntity<LocationDtoResponse> findByLatitudeAndLongitudeGeohashv3(@RequestHeader(value = "Authorization", required = false) String auth,
                                                                                   @RequestParam Double latitude,
                                                                                   @RequestParam Double longitude) {
        authorizationService.checkAuthorization(auth);
        return ResponseEntity.ok(locationService.findByLatitudeAndLongitude(latitude, longitude));
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
