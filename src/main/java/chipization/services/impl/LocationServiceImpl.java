package chipization.services.impl;

import chipization.exceptions.EntityBadRequestException;
import chipization.model.Location;
import chipization.repositories.AnimalRepository;
import chipization.repositories.LocationRepository;
import chipization.services.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;
    private final AnimalRepository animalRepository;

    @Override
    public Location findById(Long locationId) {
        validatePointId(locationId);
        return locationRepository.findById(locationId)
                .orElseThrow(() -> new EntityNotFoundException("Не найдена локация"));
    }

    @Override
    public Location create(Location location) {
        if (locationRepository.findByLatitudeAndLongitude(location.getLatitude(), location.getLongitude()).isPresent()) {
            throw new DataIntegrityViolationException("Точка локации с такими latitude и longitude уже существует");
        }
        return locationRepository.save(location);
    }

    @Override
    public Location update(Long pointId, Location location) {
        validatePointId(pointId);
        locationRepository.findById(pointId)
                .orElseThrow(() -> new EntityNotFoundException("Не найдена точка локации"));
        if (locationRepository.findByLatitudeAndLongitude(location.getLatitude(), location.getLongitude()).isPresent()) {
            throw new DataIntegrityViolationException("Точка локации с такими latitude и longitude уже существует");
        }
        location.setId(pointId);
        return locationRepository.save(location);
    }

    @Override
    public void delete(Long pointId) {
        validatePointId(pointId);
        if (animalRepository.findAnimalLocation(pointId).isPresent()) {
            throw new EntityBadRequestException("ID локации связан с животным и не может быть удален");
        }
        if (!animalRepository.findAnimalsByChippingLocationId(pointId).isEmpty()) {
            throw new EntityBadRequestException("ID локации связан с животным и не может быть удален");
        }
        locationRepository.findById(pointId)
                .orElseThrow(() -> new EntityNotFoundException("Не найдена точка локации"));
        locationRepository.deleteById(pointId);
    }

    private void validatePointId(Long pointId) {
        if (pointId <= 0) {
            throw new EntityBadRequestException("ID локации должен быть больше 0");
        }
    }
}