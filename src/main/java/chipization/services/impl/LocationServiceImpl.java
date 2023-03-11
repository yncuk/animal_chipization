package chipization.services.impl;

import chipization.exceptions.EntityBadRequestException;
import chipization.mappers.UserMapper;
import chipization.model.Location;
import chipization.model.User;
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
        if (locationId <= 0) {
            throw new EntityBadRequestException("ID локации должен быть больше 0");
        }
        return locationRepository.findById(locationId)
                .orElseThrow(() -> new EntityNotFoundException("Не найдена локация"));
    }

    @Override
    public Location create(Location location) {
        if (location.getLatitude() < -90 || location.getLatitude() > 90 || location.getLongitude() < -180 || location.getLongitude() > 180) {
            throw new EntityBadRequestException("Неверно заданы широта(от -90 до 90)/долгота(от -180 до 180)");
        }
        if (locationRepository.findByLatitudeAndLongitude(location.getLatitude(), location.getLongitude()).isPresent()) {
            throw new DataIntegrityViolationException("Точка локации с такими latitude и longitude уже существует");
        }
        return locationRepository.save(location);
    }

    @Override
    public Location update(Long pointId, Location location) {
        if (pointId <= 0) {
            throw new EntityBadRequestException("ID локации должен быть больше 0");
        }
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
        if (pointId <= 0) {
            throw new EntityBadRequestException("ID локации должен быть больше 0");
        }
        if(animalRepository.findByChippingLocationId(pointId).isPresent()) {
            throw new EntityBadRequestException("ID локации связан с животным и не может быть удален");
        }
        locationRepository.findById(pointId)
                .orElseThrow(() -> new EntityNotFoundException("Не найдена точка локации"));
        locationRepository.deleteById(pointId);
    }


}
