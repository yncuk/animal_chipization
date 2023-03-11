package chipization.services.impl;

import chipization.exceptions.EntityBadRequestException;
import chipization.mappers.VisitLocationMapper;
import chipization.model.Animal;
import chipization.model.VisitLocation;
import chipization.model.dto.VisitLocationDto;
import chipization.model.dto.VisitLocationResponse;
import chipization.model.enums.LifeStatus;
import chipization.repositories.AnimalRepository;
import chipization.repositories.LocationRepository;
import chipization.repositories.VisitLocationRepository;
import chipization.services.VisitLocationAnimalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class VisitLocationAnimalServiceImpl implements VisitLocationAnimalService {

    private final AnimalRepository animalRepository;
    private final VisitLocationRepository visitLocationRepository;
    private final LocationRepository locationRepository;

    @Override
    public Collection<VisitLocationResponse> findAllVisitLocations(Long animalId, OffsetDateTime startDateTime, OffsetDateTime endDateTime, int from, int size) {
        if (animalId <= 0) {
            throw new EntityBadRequestException("ID животного должен быть больше 0");
        }
        if (size <= 0 || from < 0) {
            throw new EntityBadRequestException("Количество пропущенных элементов и страниц не должно быть меньше 0");
        }

        animalRepository.findById(animalId)
                .orElseThrow(() -> new EntityNotFoundException("Не найдено животное"));

        return VisitLocationMapper.allToVisitLocationResponse(visitLocationRepository.findAllVisitLocations(animalId, startDateTime, endDateTime, size, from));
    }

    @Override
    @Transactional
    public VisitLocationResponse addVisitLocation(Long animalId, Long pointId) {
        if (animalId <= 0 || pointId <= 0) {
            throw new EntityBadRequestException("ID животного и точки локации должны быть больше 0");
        }
        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new EntityNotFoundException("Не найдено животное"));
        if (animal.getLifeStatus().equals(LifeStatus.DEAD)) {
            throw new EntityBadRequestException("Нельзя добавить точку локации для мертвого животного");
        }
        locationRepository.findById(pointId)
                .orElseThrow(() -> new EntityNotFoundException("Не найдена точка локации"));
        VisitLocation visitLocation = new VisitLocation();
        visitLocation.setDateTimeOfVisitLocationPoint(OffsetDateTime.now());
        visitLocation.setLocationPointId(pointId);
        if (animal.getVisitedLocations().isEmpty() && Objects.equals(animal.getChippingLocationId(), pointId)) {
            throw new EntityBadRequestException("Нельзя добавить точку локации равную точке чипирования");
        }
        VisitLocation visitLocationNew = visitLocationRepository.save(visitLocation);
        List<Long> visitedLocations = animal.getVisitedLocations();
        visitedLocations.add(pointId);
        animal.setVisitedLocations(visitedLocations);
        animal.setChippingLocationId(pointId);
        animalRepository.save(animal);
        return VisitLocationMapper.toVisitLocationResponse(visitLocationNew);
    }

    @Override
    public VisitLocationResponse updateVisitLocation(Long animalId, VisitLocationDto visitLocationDto) {
        if (animalId <= 0) {
            throw new EntityBadRequestException("ID животного должно быть больше 0");
        }
        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new EntityNotFoundException("Не найдено животное"));
        VisitLocation visitLocation = visitLocationRepository.findById(visitLocationDto.getVisitedLocationPointId())
                .orElseThrow(() -> new EntityNotFoundException("Не найдена точка локации для изменения"));
        locationRepository.findById(visitLocationDto.getLocationPointId())
                .orElseThrow(() -> new EntityNotFoundException("Не найдена точка локации"));
        VisitLocation chippingLocation = visitLocationRepository.findChippingLocation(animalId);
        VisitLocation firstVisitedLocation = visitLocationRepository.findFirstVisitedLocation(animalId);
        if (Objects.equals(firstVisitedLocation.getId(), visitLocationDto.getVisitedLocationPointId())
                && Objects.equals(chippingLocation.getLocationPointId(), visitLocationDto.getLocationPointId())) {
            throw new EntityBadRequestException("Нельзя обновить первую посещенную точку на точку чипирования");
        }
        if (Objects.equals(visitLocation.getLocationPointId(), visitLocationDto.getLocationPointId())) {
            throw new EntityBadRequestException("Нельзя обновить точку на ту же самую");
        }

        if (!animal.getVisitedLocations().contains(visitLocationDto.getVisitedLocationPointId())) {
            throw new EntityNotFoundException("Не найден объект для обновления посещенной точки локации");
        }
        visitLocation.setDateTimeOfVisitLocationPoint(OffsetDateTime.now());
        visitLocation.setLocationPointId(visitLocationDto.getLocationPointId());

        return VisitLocationMapper.toVisitLocationResponse(visitLocationRepository.save(visitLocation));
    }

    @Override
    public void deleteVisitLocation(Long animalId, Long visitedPointId) {
        if (animalId <= 0 || visitedPointId <= 0) {
            throw new EntityBadRequestException("ID животного и посещенной точки локации должны быть больше 0");
        }
        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new EntityNotFoundException("Не найдено животное"));
        VisitLocation visitLocation = visitLocationRepository.findById(visitedPointId)
                .orElseThrow(() -> new EntityNotFoundException("Не найдена точка локации для удаления"));
        visitLocationRepository.deleteById(visitedPointId);

    }


}
