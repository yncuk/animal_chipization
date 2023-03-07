package chipization.services;

import chipization.model.VisitLocation;
import chipization.model.dto.VisitLocationDto;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.Collection;

public interface VisitLocationAnimalService {
    Collection<VisitLocation> findAllVisitLocations(Long animalId,
                                                    LocalDateTime startDateTime,
                                                    LocalDateTime endDateTime,
                                                    int from,
                                                    int size);
    VisitLocation addVisitLocation(Long animalId, Long pointId);

    VisitLocation updateVisitLocation(Long animalId, VisitLocationDto visitLocationDto);

    void deleteVisitLocation(Long animalId, Long visitedPointId);
}
