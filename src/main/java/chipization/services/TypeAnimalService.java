package chipization.services;

import chipization.model.TypeAnimal;

public interface TypeAnimalService {
    TypeAnimal findById(Long typeId);

    TypeAnimal create(TypeAnimal typeAnimal);

    TypeAnimal update(Long typeId, TypeAnimal typeAnimal);

    void delete(Long typeId);
}
