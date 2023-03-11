package chipization.controllers;

import chipization.exceptions.EntityNotAuthorizedException;
import chipization.model.Animal;
import chipization.model.VisitLocation;
import chipization.model.dto.GetAnimalsRequest;
import chipization.model.dto.TypeDto;
import chipization.model.dto.VisitLocationDto;
import chipization.model.dto.VisitLocationResponse;
import chipization.services.AccountService;
import chipization.services.VisitLocationAnimalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.OffsetDateTime;
import java.util.Collection;

@RestController
@RequestMapping("/animals")
@RequiredArgsConstructor
@Validated
public class VisitLocationAnimalController {
    private final VisitLocationAnimalService visitLocationAnimalService;
    private final AccountService accountService;

    @GetMapping("/{animalId}/locations")
    public Collection<VisitLocationResponse> search(@RequestHeader(value = "Authorization", required = false) String auth,
                                                    @PathVariable Long animalId,
                                                    @RequestParam(required = false) OffsetDateTime startDateTime,
                                                    @RequestParam(required = false) OffsetDateTime endDateTime,
                                                    @RequestParam(defaultValue = "0") Integer from,
                                                    @RequestParam(defaultValue = "10") Integer size) {
        accountService.checkAuthorization(auth);
        return visitLocationAnimalService.findAllVisitLocations(animalId, startDateTime, endDateTime, from, size);
    }

    @PostMapping("/{animalId}/locations/{pointId}")
    public ResponseEntity<VisitLocationResponse> addVisitLocation(@RequestHeader(value = "Authorization", required = false) String auth, @PathVariable Long animalId, @PathVariable Long pointId) {
        if (auth == null) {
            throw new EntityNotAuthorizedException("Анонимный пользователь, запрос недоступен");
        }
        accountService.checkAuthorization(auth);
        return new ResponseEntity<>(visitLocationAnimalService.addVisitLocation(animalId, pointId), HttpStatus.CREATED);
    }

    @PutMapping("/{animalId}/locations")
    public ResponseEntity<VisitLocationResponse> updateVisitLocation(@RequestHeader(value = "Authorization", required = false) String auth, @Valid @RequestBody VisitLocationDto visitLocationDto, @PathVariable Long animalId) {
        if (auth == null) {
            throw new EntityNotAuthorizedException("Анонимный пользователь, запрос недоступен");
        }
        accountService.checkAuthorization(auth);
        return ResponseEntity.ok(visitLocationAnimalService.updateVisitLocation(animalId, visitLocationDto));
    }

    @DeleteMapping("/{animalId}/locations/{visitedPointId}")
    public void deleteVisitLocation(@RequestHeader(value = "Authorization", required = false) String auth, @PathVariable Long animalId, @PathVariable Long visitedPointId) {
        if (auth == null) {
            throw new EntityNotAuthorizedException("Анонимный пользователь, запрос недоступен");
        }
        accountService.checkAuthorization(auth);
        visitLocationAnimalService.deleteVisitLocation(animalId, visitedPointId);
    }
}
