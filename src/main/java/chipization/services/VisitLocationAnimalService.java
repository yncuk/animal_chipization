package chipization.services;

import chipization.model.VisitLocation;
import chipization.model.dto.VisitLocationDto;
import chipization.model.dto.VisitLocationResponse;

import java.time.OffsetDateTime;
import java.util.Collection;

public interface VisitLocationAnimalService {
    Collection<VisitLocationResponse> findAllVisitLocations(Long animalId,
                                                            OffsetDateTime startDateTime,
                                                            OffsetDateTime endDateTime,
                                                            int from,
                                                            int size);

    VisitLocation addVisitLocation(Long animalId, Long pointId);

    VisitLocationResponse updateVisitLocation(Long animalId, VisitLocationDto visitLocationDto);

    void deleteVisitLocation(Long animalId, Long visitedPointId);
}