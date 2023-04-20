package chipization.services.impl;

import chipization.exceptions.EntityBadRequestException;
import chipization.mappers.LocationMapper;
import chipization.model.Location;
import chipization.model.dto.LocationDto;
import chipization.model.dto.LocationDtoResponse;
import chipization.repositories.AnimalRepository;
import chipization.repositories.LocationRepository;
import chipization.services.LocationService;
import com.github.davidmoten.geo.GeoHash;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Base64;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;
    private final AnimalRepository animalRepository;

    @Override
    public LocationDtoResponse findById(Long locationId) {
        validatePointId(locationId);
        return LocationMapper.toLocationDtoResponse(locationRepository.findById(locationId)
                .orElseThrow(() -> new EntityNotFoundException("Не найдена локация")));
    }

    @Override
    public LocationDtoResponse create(LocationDto locationDto) {
        Optional<Location> location = locationRepository.findByLatitudeAndLongitude(locationDto.getLatitude(), locationDto.getLongitude());
        return location.map(LocationMapper::toLocationDtoResponse)
                .orElseGet(() -> LocationMapper.toLocationDtoResponse(locationRepository.save(LocationMapper.toLocationFromLocationDto(locationDto))));
    }

    @Override
    public LocationDtoResponse update(Long pointId, LocationDto locationDto) {
        validatePointId(pointId);
        locationRepository.findById(pointId)
                .orElseThrow(() -> new EntityNotFoundException("Не найдена точка локации"));
        if (locationRepository.findByLatitudeAndLongitude(locationDto.getLatitude(), locationDto.getLongitude()).isPresent()) {
            throw new DataIntegrityViolationException("Точка локации с такими latitude и longitude уже существует");
        }
        Location newLocation = LocationMapper.toLocationFromLocationDto(locationDto);
        newLocation.setId(pointId);
        return LocationMapper.toLocationDtoResponse(locationRepository.save(newLocation));
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

    @Override
    public LocationDtoResponse findByLatitudeAndLongitude(Double latitude, Double longitude) {
        return LocationMapper.toLocationDtoResponse(locationRepository.findByLatitudeAndLongitude(latitude, longitude)
                .orElseThrow(() -> new EntityNotFoundException("Не найдена локация")));
    }

    @Override
    public String getGeoHash(Double latitude, Double longitude) {
        return GeoHash.encodeHash(latitude, longitude);
    }

    @Override
    public String getGeoHashInBase64(Double latitude, Double longitude) {
        String geoHash = getGeoHash(latitude, longitude);
        return Base64.getEncoder().encodeToString(geoHash.getBytes());
    }

    private void validatePointId(Long pointId) {
        if (pointId <= 0) {
            throw new EntityBadRequestException("ID локации должен быть больше 0");
        }
    }
}