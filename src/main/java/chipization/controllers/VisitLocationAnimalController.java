package chipization.controllers;

import chipization.model.Animal;
import chipization.model.VisitLocation;
import chipization.model.dto.GetAnimalsRequest;
import chipization.model.dto.TypeDto;
import chipization.model.dto.VisitLocationDto;
import chipization.services.VisitLocationAnimalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collection;

@RestController
@RequestMapping("/animals")
@RequiredArgsConstructor
public class VisitLocationAnimalController {
    private final VisitLocationAnimalService visitLocationAnimalService;

    @GetMapping("/{animalId}/locations")
    public Collection<VisitLocation> search(@PathVariable Long animalId,
                                            @RequestParam(required = false) LocalDateTime startDateTime,
                                            @RequestParam(required = false) LocalDateTime endDateTime,
                                            @RequestParam(defaultValue = "0") Integer from,
                                            @RequestParam(defaultValue = "10") Integer size) {
        return visitLocationAnimalService.findAllVisitLocations(animalId, startDateTime, endDateTime, from, size);
    }

    @PostMapping("/{animalId}/locations/{pointId}")
    public ResponseEntity<VisitLocation> addVisitLocation(@RequestHeader(value = "Authorization", required = false) String auth, @PathVariable Long animalId, @PathVariable Long pointId) {
        return ResponseEntity.ok(visitLocationAnimalService.addVisitLocation(animalId, pointId));
    }

    @PutMapping("/{animalId}/locations")
    public ResponseEntity<VisitLocation> updateVisitLocation(@RequestHeader(value = "Authorization", required = false) String auth, @RequestBody VisitLocationDto visitLocationDto, @PathVariable Long animalId) {
        return ResponseEntity.ok(visitLocationAnimalService.updateVisitLocation(animalId, visitLocationDto));
    }

    @DeleteMapping("/{animalId}/locations/{visitedPointId}")
    public void deleteVisitLocation(@RequestHeader(value = "Authorization", required = false) String auth, @PathVariable Long animalId, @PathVariable Long visitedPointId) {
        visitLocationAnimalService.deleteVisitLocation(animalId, visitedPointId);
    }
}
