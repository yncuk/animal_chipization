package chipization.services.impl;

import chipization.model.Animal;
import chipization.model.Location;
import chipization.model.TypeAnimal;
import chipization.model.dto.GetAnimalsRequest;
import chipization.model.dto.TypeDto;
import chipization.repositories.AnimalRepository;
import chipization.repositories.TypeAnimalRepository;
import chipization.services.AnimalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnimalServiceImpl implements AnimalService {

    private final TypeAnimalRepository typeAnimalRepository;
    private final AnimalRepository animalRepository;

    @Override
    public TypeAnimal findById(Long typeId) {
        return typeAnimalRepository.findById(typeId)
                .orElseThrow(() -> new EntityNotFoundException("Не найден тип животного"));
    }

    @Override
    public TypeAnimal create(TypeAnimal typeAnimal) {
        return typeAnimalRepository.save(typeAnimal);
    }

    @Override
    public TypeAnimal update(Long typeId, TypeAnimal typeAnimal) {
        TypeAnimal oldTypeAnimal = typeAnimalRepository.findById(typeId)
                .orElseThrow(() -> new EntityNotFoundException("Не найден тип животного для обновления"));
        typeAnimal.setId(typeId);
        return typeAnimalRepository.save(typeAnimal);
    }

    @Override
    public void delete(Long typeId) {
        typeAnimalRepository.deleteById(typeId);
    }

    @Override
    public Animal findAnimalById(Long animalId) {
        return animalRepository.findById(animalId)
                .orElseThrow(() -> new EntityNotFoundException("Не найдено животное"));
    }

    @Override
    public Collection<Animal> search(GetAnimalsRequest getAnimalsRequest) {
        return null;
    }

    @Override
    public Animal createAnimal(Animal animal) {
        return animalRepository.save(animal);
    }

    @Override
    public Animal updateAnimal(Long animalId, Animal animal) {
        animalRepository.findById(animalId)
                .orElseThrow(() -> new EntityNotFoundException("Не найдено животное"));
        animal.setId(animalId);
        return animalRepository.save(animal);
    }

    @Override
    public void deleteAnimal(Long animalId) {
        animalRepository.deleteById(animalId);
    }

    @Override
    public Animal addAnimalTypeToAnimal(Long animalId, Long typeId) {
        TypeAnimal typeAnimal = typeAnimalRepository.findById(typeId)
                .orElseThrow(() -> new EntityNotFoundException("Не найден тип животного"));
        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new EntityNotFoundException("Не найдено животное"));
        List<TypeAnimal> newList = animal.getAnimalTypes();
        newList.add(typeAnimal);
        animal.setAnimalTypes(newList);
        return animalRepository.save(animal);
    }

    @Override
    @Transactional
    public Animal updateAnimalType(Long animalId, TypeDto typeDto) {
        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new EntityNotFoundException("Не найдено животное"));
        List<TypeAnimal> newList = animal.getAnimalTypes();
        newList.remove(typeAnimalRepository.findById(typeDto.getOldTypeId())
                .orElseThrow(() -> new EntityNotFoundException("Не найден тип животного")));
        newList.add(typeAnimalRepository.findById(typeDto.getNewTypeId())
                .orElseThrow(() -> new EntityNotFoundException("Не найден тип животного")));
        animal.setAnimalTypes(newList);
        return animalRepository.save(animal);
    }

    @Override
    public Animal deleteAnimalTypeFromAnimal(Long animalId, Long typeId) {
        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new EntityNotFoundException("Не найдено животное"));
        List<TypeAnimal> newList = animal.getAnimalTypes();
        newList.remove(typeAnimalRepository.findById(typeId)
                .orElseThrow(() -> new EntityNotFoundException("Не найден тип животного")));
        animal.setAnimalTypes(newList);
        return animalRepository.save(animal);
    }


}
