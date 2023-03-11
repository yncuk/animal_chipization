package chipization.repositories;

import chipization.model.VisitLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Collection;

public interface VisitLocationRepository extends JpaRepository<VisitLocation, Long> {

    @Query(value = " select * from visit_locations vl " +
            "where date_time_of_visit_location_point > ?2 and " +
            "date_time_of_visit_location_point < ?3 " +
            "order by date_time_of_visit_location_point asc " +
            "limit ?4 offset ?5", nativeQuery = true)
    Collection<VisitLocation> findAllVisitLocations(Long animalId, OffsetDateTime startDateTime, OffsetDateTime endDateTime, int size, int from);

    @Query( value = "select * from visit_locations as vl " +
            "left join animals_visit_locations as avl on vl.visit_location_id = avl.location_id " +
            "where animal_id = ?1 " +
            "order by date_time_of_visit_location_point " +
            "limit 1", nativeQuery = true)
    VisitLocation findChippingLocation(Long animalId);

    @Query( value = "select * from visit_locations as vl " +
            "left join animals_visit_locations as avl on vl.visit_location_id = avl.location_id " +
            "where animal_id = ?1 " +
            "order by date_time_of_visit_location_point " +
            "limit 1 offset 1", nativeQuery = true)
    VisitLocation findFirstVisitedLocation(Long animalId);

}
