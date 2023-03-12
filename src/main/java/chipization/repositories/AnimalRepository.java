package chipization.repositories;

import chipization.model.Animal;
import chipization.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;
import java.util.Optional;

public interface AnimalRepository extends JpaRepository<Animal, Long>, QuerydslPredicateExecutor<Animal> {

    @Query(value = " select * from users u " +
            "where lower(u.first_name) like lower(concat('%',?1,'%')) and " +
            "lower(u.last_name) like lower(concat('%',?2,'%')) and " +
            "lower(u.email) like lower(concat('%',?3,'%')) " +
            "order by u.user_id asc " +
            "limit ?4 offset ?5", nativeQuery = true)
    List<Animal> search(String firstName, String lastName, String email, int size, int from);

    Optional<Animal> findByChipperId(Integer id);

    @Query(value = " select a.*, avl.location_id, l.location_id from animals a " +
            "left join animals_visit_locations as avl on a.animal_id = avl.animal_id " +
            "left join locations as l on avl.location_id = l.location_id " +
            "where l.location_id = ?1", nativeQuery = true)
    Optional<Animal> findAnimalLocation(Long id);
}
