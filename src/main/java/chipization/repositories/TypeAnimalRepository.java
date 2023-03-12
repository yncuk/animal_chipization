package chipization.repositories;

import chipization.model.Animal;
import chipization.model.TypeAnimal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TypeAnimalRepository extends JpaRepository<TypeAnimal, Long> {

    @Query(value = " select exists (select a.*, ata.type_id, ta.type_id from animals a " +
            "left join animals_type_animal as ata on a.animal_id = ata.animal_id " +
            "left join type_animal as ta on ata.type_id = ta.type_id " +
            "where ta.type_id = ?1)", nativeQuery = true)
    Boolean findAnimalWithType(Long id);
}
