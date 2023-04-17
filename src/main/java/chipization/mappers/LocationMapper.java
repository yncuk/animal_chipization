package chipization.mappers;

import chipization.model.Area;
import chipization.model.Location;
import chipization.model.dto.AreaDto;
import chipization.model.dto.LocationDto;
import chipization.model.dto.LocationDtoResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class LocationMapper {
    public static LocationDtoResponse toLocationDtoResponse(Location location) {
        return new LocationDtoResponse(
                location.getId(),
                location.getLatitude(),
                location.getLongitude()
        );
    }

    public static LocationDto toLocationDto(Location location) {
        return new LocationDto(
                location.getLatitude(),
                location.getLongitude()
        );
    }

    public static Location toLocationFromLocationDto(LocationDto locationDto) {
        Location newLocation = new Location();
        newLocation.setLatitude(locationDto.getLatitude());
        newLocation.setLongitude(locationDto.getLongitude());
        return newLocation;
    }

    public static List<LocationDto> allToLocationDto(Collection<Location> locations) {
        return locations.stream().map(LocationMapper::toLocationDto).collect(Collectors.toList());
    }

    public static List<Location> allToLocation(Collection<LocationDto> locationsDto) {
        return locationsDto.stream().map(LocationMapper::toLocationFromLocationDto).collect(Collectors.toList());
    }
}
