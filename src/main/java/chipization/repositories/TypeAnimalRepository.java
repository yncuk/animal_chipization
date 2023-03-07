package chipization.repositories;

import chipization.model.TypeAnimal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TypeAnimalRepository extends JpaRepository<TypeAnimal, Long> {
}
