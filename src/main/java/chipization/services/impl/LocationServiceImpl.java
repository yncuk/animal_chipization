package chipization.services.impl;

import chipization.mappers.UserMapper;
import chipization.model.Location;
import chipization.model.User;
import chipization.repositories.LocationRepository;
import chipization.services.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;

    @Override
    public Location findById(Long locationId) {
        return locationRepository.findById(locationId)
                .orElseThrow(() -> new EntityNotFoundException("Не найдена локация"));
    }

    @Override
    public Location create(Location location) {
        return locationRepository.save(location);
    }

    @Override
    public Location update(Long pointId, Location location) {
        Location oldLocation = locationRepository.findById(pointId)
                .orElseThrow(() -> new EntityNotFoundException("Не найдена точка локации"));
        location.setId(pointId);
        return locationRepository.save(location);
    }

    @Override
    public void delete(Long pointId) {
        locationRepository.deleteById(pointId);
    }


}
