package chipization.controllers;

import chipization.model.Location;
import chipization.model.User;
import chipization.model.dto.UserDto;
import chipization.services.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;

@RestController
@RequestMapping("/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @GetMapping("/{pointId}")
    public ResponseEntity<Location> findById(@RequestHeader(value = "Authorization", required = false) String auth, @PathVariable Long pointId) {
        return ResponseEntity.ok(locationService.findById(pointId));
    }

    @PostMapping
    public ResponseEntity<Location> create(@RequestHeader(value = "Authorization", required = false) String auth, @RequestBody Location location) {
        return ResponseEntity.ok(locationService.create(location));
    }

    @PutMapping("/{pointId}")
    public ResponseEntity<Location> update(@RequestHeader(value = "Authorization", required = false) String auth, @RequestBody Location location, @PathVariable Long pointId) {
        return ResponseEntity.ok(locationService.update(pointId, location));
    }

    @DeleteMapping("/{pointId}")
    public void delete(@RequestHeader(value = "Authorization", required = false) String auth, @PathVariable Long pointId) {
        locationService.delete(pointId);
    }
}
