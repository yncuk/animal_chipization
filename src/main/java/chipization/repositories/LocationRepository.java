package chipization.repositories;

import chipization.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {

    Optional<Location> findByLatitudeAndLongitude(Double latitude, Double longitude);
}
