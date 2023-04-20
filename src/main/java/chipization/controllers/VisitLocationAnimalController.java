package chipization.controllers;

import chipization.model.VisitLocation;
import chipization.model.dto.VisitLocationDto;
import chipization.model.dto.VisitLocationResponse;
import chipization.services.AuthorizationService;
import chipization.services.VisitLocationAnimalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/animals")
@RequiredArgsConstructor
@Validated
public class VisitLocationAnimalController {
    private final VisitLocationAnimalService visitLocationAnimalService;
    private final AuthorizationService authorizationService;


    @GetMapping("/{animalId}/locations")
    public ResponseEntity<Collection<VisitLocationResponse>> search(@RequestHeader(value = "Authorization", required = false) String auth,
                                                                    @PathVariable Long animalId,
                                                                    @RequestParam(required = false) String startDateTime,
                                                                    @RequestParam(required = false) String endDateTime,
                                                                    @RequestParam(defaultValue = "0") Integer from,
                                                                    @RequestParam(defaultValue = "10") Integer size) {
        authorizationService.checkAuthorization(auth);
        return ResponseEntity.ok(visitLocationAnimalService.findAllVisitLocations(animalId, startDateTime, endDateTime, from, size));
    }

    @PostMapping("/{animalId}/locations/{pointId}")
    public ResponseEntity<VisitLocation> addVisitLocation(@RequestHeader(value = "Authorization", required = false) String auth,
                                                          @PathVariable Long animalId, @PathVariable Long pointId) {
        authorizationService.checkAuthorizationForAdminOrChipperRights(auth);
        return new ResponseEntity<>(visitLocationAnimalService.addVisitLocation(animalId, pointId), HttpStatus.CREATED);
    }

    @PutMapping("/{animalId}/locations")
    public ResponseEntity<VisitLocationResponse> updateVisitLocation(@RequestHeader(value = "Authorization", required = false) String auth,
                                                                     @Valid @RequestBody VisitLocationDto visitLocationDto, @PathVariable Long animalId) {
        authorizationService.checkAuthorizationForAdminOrChipperRights(auth);
        return ResponseEntity.ok(visitLocationAnimalService.updateVisitLocation(animalId, visitLocationDto));
    }

    @DeleteMapping("/{animalId}/locations/{visitedPointId}")
    public void deleteVisitLocation(@RequestHeader(value = "Authorization", required = false) String auth,
                                    @PathVariable Long animalId, @PathVariable Long visitedPointId) {
        authorizationService.checkAuthorizationForAdminRights(auth);
        visitLocationAnimalService.deleteVisitLocation(animalId, visitedPointId);
    }
}
