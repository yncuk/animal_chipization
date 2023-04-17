package chipization.controllers;

import chipization.model.Animal;
import chipization.model.dto.AnimalDto;
import chipization.model.dto.GetAnimalsRequest;
import chipization.model.dto.TypeDto;
import chipization.services.AnimalService;
import chipization.services.AuthorizationService;
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
public class AnimalController {

    private final AnimalService animalService;
    private final AuthorizationService authorizationService;

    @GetMapping("/{animalId}")
    public ResponseEntity<Animal> findAnimalById(@RequestHeader(value = "Authorization", required = false) String auth,
                                                 @PathVariable Long animalId) {
        authorizationService.checkAuthorization(auth);
        return ResponseEntity.ok(animalService.findAnimalById(animalId));
    }

    @GetMapping("/search")
    public Collection<Animal> search(@RequestHeader(value = "Authorization", required = false) String auth,
                                     @RequestParam(required = false) String startDateTime,
                                     @RequestParam(required = false) String endDateTime,
                                     @RequestParam(required = false) Integer chipperId,
                                     @RequestParam(required = false) Long chippingLocationId,
                                     @RequestParam(required = false) String lifeStatus,
                                     @RequestParam(required = false) String gender,
                                     @RequestParam(defaultValue = "0") Integer from,
                                     @RequestParam(defaultValue = "10") Integer size) {
        authorizationService.checkAuthorization(auth);
        return animalService.search(GetAnimalsRequest.of(startDateTime, endDateTime, chipperId, chippingLocationId, lifeStatus, gender, from, size));
    }

    @PostMapping
    public ResponseEntity<Animal> createAnimal(@RequestHeader(value = "Authorization", required = false) String auth,
                                               @Valid @RequestBody Animal animal) {
        authorizationService.checkAuthorizationForAdminOrChipperRights(auth);
        return new ResponseEntity<>(animalService.createAnimal(animal), HttpStatus.CREATED);
    }

    @PutMapping("/{animalId}")
    public ResponseEntity<Animal> updateAnimal(@RequestHeader(value = "Authorization", required = false) String auth,
                                               @Valid @RequestBody Animal animal, @PathVariable Long animalId) {
        authorizationService.checkAuthorizationForAdminOrChipperRights(auth);
        return ResponseEntity.ok(animalService.updateAnimal(animalId, animal));
    }

    @DeleteMapping("/{animalId}")
    public void deleteAnimal(@RequestHeader(value = "Authorization", required = false) String auth,
                             @PathVariable Long animalId) {
        authorizationService.checkAuthorizationForAdminRights(auth);
        animalService.deleteAnimal(animalId);
    }

    @PostMapping("/{animalId}/types/{typeId}")
    public ResponseEntity<Animal> addAnimalTypeToAnimal(@RequestHeader(value = "Authorization", required = false) String auth,
                                                           @PathVariable Long animalId, @PathVariable Long typeId) {
        authorizationService.checkAuthorizationForAdminOrChipperRights(auth);
        return new ResponseEntity<>(animalService.addAnimalTypeToAnimal(animalId, typeId), HttpStatus.CREATED);
    }

    @PutMapping("/{animalId}/types")
    public ResponseEntity<Animal> updateAnimalType(@RequestHeader(value = "Authorization", required = false) String auth,
                                                   @Valid @RequestBody TypeDto typeDto, @PathVariable Long animalId) {
        authorizationService.checkAuthorizationForAdminOrChipperRights(auth);
        return ResponseEntity.ok(animalService.updateAnimalType(animalId, typeDto));
    }

    @DeleteMapping("/{animalId}/types/{typeId}")
    public ResponseEntity<Animal> deleteAnimalTypeFromAnimal(@RequestHeader(value = "Authorization", required = false) String auth,
                                                             @PathVariable Long animalId, @PathVariable Long typeId) {
        authorizationService.checkAuthorizationForAdminOrChipperRights(auth);
        return ResponseEntity.ok(animalService.deleteAnimalTypeFromAnimal(animalId, typeId));
    }
}
