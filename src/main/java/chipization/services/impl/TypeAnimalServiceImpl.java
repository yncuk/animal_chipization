package chipization.services.impl;

import chipization.exceptions.EntityBadRequestException;
import chipization.model.TypeAnimal;
import chipization.repositories.TypeAnimalRepository;
import chipization.services.TypeAnimalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class TypeAnimalServiceImpl implements TypeAnimalService {

    private final TypeAnimalRepository typeAnimalRepository;

    @Override
    public TypeAnimal findById(Long typeId) {
        validateTypeId(typeId);
        return typeAnimalRepository.findById(typeId)
                .orElseThrow(() -> new EntityNotFoundException("Не найден тип животного"));
    }

    @Override
    public TypeAnimal create(TypeAnimal typeAnimal) {
        return typeAnimalRepository.save(typeAnimal);
    }

    @Override
    public TypeAnimal update(Long typeId, TypeAnimal typeAnimal) {
        validateTypeId(typeId);
        typeAnimalRepository.findById(typeId)
                .orElseThrow(() -> new EntityNotFoundException("Не найден тип животного для обновления"));
        typeAnimal.setId(typeId);
        return typeAnimalRepository.save(typeAnimal);
    }

    @Override
    public void delete(Long typeId) {
        validateTypeId(typeId);
        if (typeAnimalRepository.findAnimalWithType(typeId)) {
            throw new EntityBadRequestException("ID типа животного связан с животным и не может быть удален");
        }
        typeAnimalRepository.findById(typeId)
                .orElseThrow(() -> new EntityNotFoundException("Не найден тип животного для удаления"));
        typeAnimalRepository.deleteById(typeId);
    }

    private void validateTypeId(Long typeId) {
        if (typeId <= 0) {
            throw new EntityBadRequestException("ID типа животного не может быть отрицательным");
        }
    }
}