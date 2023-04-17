package chipization.services;

import chipization.model.dto.LocationDto;
import chipization.model.dto.LocationDtoResponse;

public interface LocationService {
    LocationDtoResponse findById(Long locationId);

    LocationDtoResponse create(LocationDto locationDto);

    LocationDtoResponse update(Long pointId, LocationDto locationDto);

    void delete(Long pointId);
}