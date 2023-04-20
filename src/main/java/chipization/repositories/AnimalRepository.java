package chipization.repositories;

import chipization.model.Animal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface AnimalRepository extends JpaRepository<Animal, Long>, QuerydslPredicateExecutor<Animal> {

    List<Animal> findByChipperId(Integer id);

    @Query(value = " select a.*, avl.location_id, vl.*, l.location_id from animals a " +
            "left join animals_visit_locations as avl on a.animal_id = avl.animal_id " +
            "left join visit_locations as vl on avl.location_id = vl.visit_location_id " +
            "left join locations as l on vl.location_point_id = l.location_id " +
            "where l.location_id = ?1", nativeQuery = true)
    Optional<Animal> findAnimalLocation(Long id);

    @Query(value = " select a.* from animals a " +
            "left join animals_visit_locations as avl on a.animal_id = avl.animal_id " +
            "left join visit_locations as vl on avl.location_id = vl.visit_location_id " +
            "where vl.visit_location_id = ?1", nativeQuery = true)
    Optional<Animal> findAnimalVisitLocation(Long id);

    @Query(" select a from Animal a " +
            "where a.chippingLocationId = ?1")
    List<Animal> findAnimalsByChippingLocationId(Long id);

    @Query(" select a from Animal a " +
            "where a.chippingDateTime >= ?1 and " +
            "a.chippingDateTime <= ?2")
    List<Animal> findAnimalsChippingLocationInTime(OffsetDateTime start, OffsetDateTime end);
}