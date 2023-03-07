package chipization.controllers;

import chipization.model.Animal;
import chipization.model.TypeAnimal;
import chipization.model.dto.GetAnimalsRequest;
import chipization.model.dto.TypeDto;
import chipization.model.enums.AnimalGender;
import chipization.model.enums.LifeStatus;
import chipization.services.AnimalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collection;

@RestController
@RequestMapping("/animals")
@RequiredArgsConstructor
public class AnimalController {

    private final AnimalService animalService;

    @GetMapping("/types/{typeId}")
    public ResponseEntity<TypeAnimal> findById(@RequestHeader(value = "Authorization", required = false) String auth, @PathVariable Long typeId) {
        return ResponseEntity.ok(animalService.findById(typeId));
    }

    @PostMapping("/types")
    public ResponseEntity<TypeAnimal> create(@RequestHeader(value = "Authorization", required = false) String auth, @RequestBody TypeAnimal typeAnimal) {
        return ResponseEntity.ok(animalService.create(typeAnimal));
    }

    @PutMapping("/types/{typeId}")
    public ResponseEntity<TypeAnimal> update(@RequestHeader(value = "Authorization", required = false) String auth, @RequestBody TypeAnimal typeAnimal, @PathVariable Long typeId) {
        return ResponseEntity.ok(animalService.update(typeId, typeAnimal));
    }

    @DeleteMapping("/types/{typeId}")
    public void delete(@RequestHeader(value = "Authorization", required = false) String auth, @PathVariable Long typeId) {
        animalService.delete(typeId);
    }

    @GetMapping("/{animalId}")
    public ResponseEntity<Animal> findAnimalById(@RequestHeader(value = "Authorization", required = false) String auth, @PathVariable Long animalId) {
        return ResponseEntity.ok(animalService.findAnimalById(animalId));
    }

    @GetMapping("/search")
    public Collection<Animal> search(@RequestParam(required = false) LocalDateTime startDateTime,
                                     @RequestParam(required = false) LocalDateTime endDateTime,
                                     @RequestParam(required = false) Integer chipperId,
                                     @RequestParam(required = false) Long chippingLocationId,
                                     @RequestParam(required = false) String lifeStatus,
                                     @RequestParam(required = false) String gender,
                                     @RequestParam(defaultValue = "0") Integer from,
                                     @RequestParam(defaultValue = "10") Integer size) {
        return animalService.search(GetAnimalsRequest.of(startDateTime, endDateTime, chipperId, chippingLocationId, lifeStatus, gender, from, size));
    }

    @PostMapping
    public ResponseEntity<Animal> createAnimal(@RequestHeader(value = "Authorization", required = false) String auth, @RequestBody Animal animal) {
        return ResponseEntity.ok(animalService.createAnimal(animal));
    }

    @PutMapping("/{animalId}")
    public ResponseEntity<Animal> updateAnimal(@RequestHeader(value = "Authorization", required = false) String auth, @RequestBody Animal animal, @PathVariable Long animalId) {
        return ResponseEntity.ok(animalService.updateAnimal(animalId, animal));
    }

    @DeleteMapping("/{animalId}")
    public void deleteAnimal(@RequestHeader(value = "Authorization", required = false) String auth, @PathVariable Long animalId) {
        animalService.deleteAnimal(animalId);
    }

    @PostMapping("/{animalId}/types/{typeId}")
    public ResponseEntity<Animal> addAnimalTypeToAnimal(@RequestHeader(value = "Authorization", required = false) String auth, @PathVariable Long animalId, @PathVariable Long typeId) {
        return ResponseEntity.ok(animalService.addAnimalTypeToAnimal(animalId, typeId));
    }

    @PutMapping("/{animalId}/types")
    public ResponseEntity<Animal> updateAnimalType(@RequestHeader(value = "Authorization", required = false) String auth, @RequestBody TypeDto typeDto, @PathVariable Long animalId) {
        return ResponseEntity.ok(animalService.updateAnimalType(animalId, typeDto));
    }

    @DeleteMapping("/{animalId}/types/{typeId}")
    public ResponseEntity<Animal> deleteAnimalTypeFromAnimal(@RequestHeader(value = "Authorization", required = false) String auth, @PathVariable Long animalId, @PathVariable Long typeId) {
        return ResponseEntity.ok(animalService.deleteAnimalTypeFromAnimal(animalId, typeId));
    }
}
