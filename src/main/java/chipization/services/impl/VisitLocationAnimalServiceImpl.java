package chipization.services.impl;

import chipization.model.Animal;
import chipization.model.VisitLocation;
import chipization.model.dto.VisitLocationDto;
import chipization.repositories.AnimalRepository;
import chipization.repositories.VisitLocationRepository;
import chipization.services.VisitLocationAnimalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class VisitLocationAnimalServiceImpl implements VisitLocationAnimalService {

    private final AnimalRepository animalRepository;
    private final VisitLocationRepository visitLocationRepository;

    @Override
    public Collection<VisitLocation> findAllVisitLocations(Long animalId, LocalDateTime startDateTime, LocalDateTime endDateTime, int from, int size) {
        return null;
    }

    @Override
    public VisitLocation addVisitLocation(Long animalId, Long pointId) {
        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new EntityNotFoundException("Не найдено животное"));

        VisitLocation visitLocation = new VisitLocation();
        visitLocation.setDateTimeOfVisitLocationPoint(LocalDateTime.now());
        visitLocation.setLocationPointId(pointId);
        visitLocation.setAnimalId(animalId);
        return visitLocationRepository.save(visitLocation);
    }

    @Override
    public VisitLocation updateVisitLocation(Long animalId, VisitLocationDto visitLocationDto) {
        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new EntityNotFoundException("Не найдено животное"));
        VisitLocation visitLocation = visitLocationRepository.findById(visitLocationDto.getVisitedLocationPointId())
                .orElseThrow(() -> new EntityNotFoundException("Не найдена точка локации для изменения"));
        visitLocation.setDateTimeOfVisitLocationPoint(LocalDateTime.now());
        visitLocation.setLocationPointId(visitLocationDto.getLocationPointId());
        return visitLocationRepository.save(visitLocation);
    }

    @Override
    public void deleteVisitLocation(Long animalId, Long visitedPointId) {
        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new EntityNotFoundException("Не найдено животное"));
        VisitLocation visitLocation = visitLocationRepository.findById(visitedPointId)
                .orElseThrow(() -> new EntityNotFoundException("Не найдена точка локации для удаления"));
        visitLocationRepository.deleteById(visitedPointId);

    }


}
