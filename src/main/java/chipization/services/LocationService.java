package chipization.services;

import chipization.model.Location;

public interface LocationService {

     Location findById(Long locationId);

     Location create(Location location);

     Location update(Long pointId, Location location);

     void delete(Long pointId);
}
