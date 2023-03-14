package chipization.repositories;

import chipization.model.VisitLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;

public interface VisitLocationRepository extends JpaRepository<VisitLocation, Long> {

    @Query(value = " select * from visit_locations vl " +
            "left join animals_visit_locations as avl on vl.visit_location_id = avl.location_id " +
            "where animal_id = ?1 and " +
            "date_time_of_visit_location_point > ?2 and " +
            "date_time_of_visit_location_point < ?3 " +
            "order by date_time_of_visit_location_point " +
            "limit ?4 offset ?5", nativeQuery = true)
    Collection<VisitLocation> findAllVisitLocations(Long animalId, OffsetDateTime startDateTime, OffsetDateTime endDateTime, int size, int from);

    @Query(value = " select * from visit_locations vl " +
            "left join animals_visit_locations as avl on vl.visit_location_id = avl.location_id " +
            "where animal_id = ?1 and " +
            "date_time_of_visit_location_point < ?2 " +
            "order by date_time_of_visit_location_point " +
            "limit ?3 offset ?4", nativeQuery = true)
    Collection<VisitLocation> findAllVisitLocationsWithoutStartTime(Long animalId, OffsetDateTime endDateTime, int size, int from);

    @Query(value = " select * from visit_locations vl " +
            "left join animals_visit_locations as avl on vl.visit_location_id = avl.location_id " +
            "where animal_id = ?1 and " +
            "date_time_of_visit_location_point > ?2 " +
            "order by date_time_of_visit_location_point " +
            "limit ?3 offset ?4", nativeQuery = true)
    Collection<VisitLocation> findAllVisitLocationsWithoutEndTime(Long animalId, OffsetDateTime startDateTime, int size, int from);

    @Query(value = " select * from visit_locations vl " +
            "left join animals_visit_locations as avl on vl.visit_location_id = avl.location_id " +
            "where animal_id = ?1 " +
            "order by date_time_of_visit_location_point " +
            "limit ?2 offset ?3", nativeQuery = true)
    Collection<VisitLocation> findAllVisitLocationsWithoutStartAndEndTime(Long animalId, int size, int from);


    @Query(value = "select * from visit_locations as vl " +
            "left join animals_visit_locations as avl on vl.visit_location_id = avl.location_id " +
            "where animal_id = ?1 " +
            "order by date_time_of_visit_location_point " +
            "limit 1 offset 0", nativeQuery = true)
    VisitLocation findFirstVisitedLocation(Long animalId);

    @Query(value = "select * from visit_locations as vl " +
            "left join animals_visit_locations as avl on vl.visit_location_id = avl.location_id " +
            "where animal_id = ?1 " +
            "order by date_time_of_visit_location_point " +
            "limit 1 offset 1", nativeQuery = true)
    VisitLocation findSecondVisitedLocation(Long animalId);

    @Query(value = "select * from visit_locations as vl " +
            "left join animals_visit_locations as avl on vl.visit_location_id = avl.location_id " +
            "where animal_id = ?1 ", nativeQuery = true)
    List<VisitLocation> findAllWhereAnimalId(Long animalId);

    @Query(value = " select exists (select * from visit_locations as vl " +
            "left join animals_visit_locations as avl on vl.visit_location_id = avl.location_id " +
            "where animal_id = ?1 and vl.location_point_id = ?2)", nativeQuery = true)
    Boolean isExpectedEarlier(Long animalId, Long locationPointId);
}