package chipization.services;

import chipization.model.Animal;
import chipization.model.TypeAnimal;
import chipization.model.dto.GetAnimalsRequest;
import chipization.model.dto.TypeDto;

import java.util.Collection;

public interface AnimalService {
    TypeAnimal findById(Long typeId);

    TypeAnimal create(TypeAnimal typeAnimal);

    TypeAnimal update(Long typeId, TypeAnimal typeAnimal);

    void delete(Long typeId);

    Animal findAnimalById(Long animalId);

    Collection<Animal> search(GetAnimalsRequest getAnimalsRequest);

    Animal createAnimal(Animal animal);

    Animal updateAnimal(Long animalId, Animal animal);

    void deleteAnimal(Long animalId);

    Animal addAnimalTypeToAnimal(Long animalId, Long typeId);

    Animal updateAnimalType(Long animalId, TypeDto typeDto);

    Animal deleteAnimalTypeFromAnimal(Long animalId, Long typeId);
}
