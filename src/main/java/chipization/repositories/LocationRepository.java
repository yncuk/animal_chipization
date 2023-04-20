package chipization.repositories;

import chipization.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {

    Optional<Location> findByLatitudeAndLongitude(Double latitude, Double longitude);

    @Query(value = " select l.* from locations l " +
            "left join visit_locations vl on l.location_id = vl.location_point_id " +
            "left join animals_visit_locations avl on vl.visit_location_id = avl.location_id " +
            "left join animals a on avl.animal_id = a.animal_id " +
            "where a.animal_id = ?1 and " +
            "vl.date_time_of_visit_location_point >= ?2 and " +
            "vl.date_time_of_visit_location_point <= ?3 ", nativeQuery = true)
    List<Location> findAllVisitAnimalLocationInTime(Long animalId, OffsetDateTime start, OffsetDateTime end);
}
