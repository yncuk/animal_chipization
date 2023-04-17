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

import javax.persistence.EntityNotFoundException;
import java.time.OffsetDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class VisitLocationAnimalServiceImpl implements VisitLocationAnimalService {

    private final AnimalRepository animalRepository;
    private final VisitLocationRepository visitLocationRepository;
    private final LocationRepository locationRepository;

    @Override
    public Collection<VisitLocationResponse> findAllVisitLocations(Long animalId, String startDateTime, String endDateTime, int from, int size) {
        validateAnimalId(animalId);
        if (size <= 0 || from < 0) {
            throw new EntityBadRequestException("Количество пропущенных элементов и страниц не должно быть меньше 0");
        }
        animalRepository.findById(animalId)
                .orElseThrow(() -> new EntityNotFoundException("Не найдено животное"));
        if (startDateTime == null && endDateTime == null) {
            return VisitLocationMapper.allToVisitLocationResponse(visitLocationRepository.findAllVisitLocationsWithoutStartAndEndTime(animalId, size, from));
        }
        if (startDateTime == null) {
            return VisitLocationMapper.allToVisitLocationResponse(visitLocationRepository.findAllVisitLocationsWithoutStartTime(animalId, parseHTML(endDateTime), size, from));
        }
        if (endDateTime == null) {
            return VisitLocationMapper.allToVisitLocationResponse(visitLocationRepository.findAllVisitLocationsWithoutEndTime(animalId, parseHTML(startDateTime), size, from));
        }
        return VisitLocationMapper.allToVisitLocationResponse(visitLocationRepository.findAllVisitLocations(animalId, parseHTML(startDateTime), parseHTML(endDateTime), size, from));
    }

    @Override
    public VisitLocation addVisitLocation(Long animalId, Long pointId) {
        validateAnimalIdAndVisitedPointId(animalId, pointId);
        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new EntityNotFoundException("Не найдено животное"));
        if (animal.getLifeStatus() != null) {
            if (animal.getLifeStatus().equals(LifeStatus.DEAD)) {
                throw new EntityBadRequestException("Нельзя добавить точку локации для мертвого животного");
            }
        }
        locationRepository.findById(pointId)
                .orElseThrow(() -> new EntityNotFoundException("Не найдена точка локации"));
        if (animal.getVisitedLocations() != null) {
            if (animal.getVisitedLocations().isEmpty() && Objects.equals(animal.getChippingLocationId(), pointId)) {
                throw new EntityBadRequestException("Нельзя добавить точку локации равную точке чипирования");
            }
        } else if (animal.getVisitedLocations() == null && Objects.equals(animal.getChippingLocationId(), pointId)) {
            throw new EntityBadRequestException("Нельзя добавить точку локации равную точке чипирования");
        }

        List<VisitLocation> animalVisitLocations = visitLocationRepository.findAllWhereAnimalId(animalId);
        if (animalVisitLocations != null && animalVisitLocations.size() != 0) {
            animalVisitLocations.sort(Comparator.comparing(VisitLocation::getDateTimeOfVisitLocationPoint).reversed());
            if (Objects.equals(animalVisitLocations.get(0).getLocationPointId(), pointId)) {
                throw new EntityBadRequestException("Попытка добавить животному ту же точку");
            }
        }
        VisitLocation visitLocation = new VisitLocation();
        visitLocation.setDateTimeOfVisitLocationPoint(OffsetDateTime.now());
        visitLocation.setLocationPointId(pointId);
        VisitLocation visitLocationNew = visitLocationRepository.save(visitLocation);
        List<Long> visitedLocations;
        if (animal.getVisitedLocations() == null) {
            visitedLocations = new ArrayList<>();
        } else {
            visitedLocations = animal.getVisitedLocations();
        }
        visitedLocations.add(visitLocationNew.getId());
        animal.setVisitedLocations(visitedLocations);
        animalRepository.save(animal);
        return visitLocationNew;
    }

    @Override
    public VisitLocationResponse updateVisitLocation(Long animalId, VisitLocationDto visitLocationDto) {
        validateAnimalId(animalId);
        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new EntityNotFoundException("Не найдено животное"));
        VisitLocation visitLocation = visitLocationRepository.findById(visitLocationDto.getVisitedLocationPointId())
                .orElseThrow(() -> new EntityNotFoundException("Не найдена точка локации для изменения"));
        locationRepository.findById(visitLocationDto.getLocationPointId())
                .orElseThrow(() -> new EntityNotFoundException("Не найдена точка локации"));
        if (Objects.equals(animal.getChippingLocationId(), visitLocationDto.getLocationPointId())) {
            throw new EntityBadRequestException("Нельзя обновить первую посещенную точку на точку чипирования");
        }
        if (Objects.equals(visitLocation.getLocationPointId(), visitLocationDto.getLocationPointId())) {
            throw new EntityBadRequestException("Нельзя обновить точку на ту же самую");
        }
        if (!animal.getVisitedLocations().contains(visitLocationDto.getVisitedLocationPointId())) {
            throw new EntityNotFoundException("Не найден объект для обновления посещенной точки локации");
        }
        if (visitLocationRepository.isExpectedEarlier(animalId, visitLocationDto.getLocationPointId())) {
            throw new EntityBadRequestException("Обновление точки локации на точку, совпадающую со следующей и/или с предыдущей точками");
        }
        visitLocation.setLocationPointId(visitLocationDto.getLocationPointId());
        return VisitLocationMapper.toVisitLocationResponse(visitLocationRepository.save(visitLocation));
    }

    @Override
    public void deleteVisitLocation(Long animalId, Long visitedPointId) {
        validateAnimalIdAndVisitedPointId(animalId, visitedPointId);
        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new EntityNotFoundException("Не найдено животное"));
        if (!animal.getVisitedLocations().contains(visitedPointId)) {
            throw new EntityNotFoundException("У животного нет объекта с информацией о посещенной точке локации с таким ID");
        }
        visitLocationRepository.findById(visitedPointId)
                .orElseThrow(() -> new EntityNotFoundException("Не найдена точка локации для удаления"));

        List<Long> visitedLocations = animal.getVisitedLocations();
        VisitLocation firstVisitLocation = visitLocationRepository.findFirstVisitedLocation(animalId);
        VisitLocation secondVisitLocation = visitLocationRepository.findSecondVisitedLocation(animalId);
        if (firstVisitLocation != null && secondVisitLocation != null) {
            if (firstVisitLocation.getId().equals(visitedPointId)
                    && secondVisitLocation.getLocationPointId().equals(animal.getChippingLocationId())) {
                visitedLocations.remove(secondVisitLocation.getId());
                visitLocationRepository.deleteById(secondVisitLocation.getId());
            }
        }
        visitedLocations.remove(visitedPointId);
        animal.setVisitedLocations(visitedLocations);
        animalRepository.save(animal);
        visitLocationRepository.deleteById(visitedPointId);
    }

    private void validateAnimalIdAndVisitedPointId(Long animalId, Long visitedPointId) {
        if (animalId <= 0 || visitedPointId <= 0) {
            throw new EntityBadRequestException("ID животного и посещенной точки локации должны быть больше 0");
        }
    }

    private void validateAnimalId(Long animalId) {
        if (animalId <= 0) {
            throw new EntityBadRequestException("ID животного должно быть больше 0");
        }
    }

    private OffsetDateTime parseHTML(String dateTime) {
        if (dateTime != null) {
            return OffsetDateTime.parse(dateTime.replace("%ЗА", ":"));
        } else return null;
    }
}