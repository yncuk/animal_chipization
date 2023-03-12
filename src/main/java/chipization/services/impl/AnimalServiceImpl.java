package chipization.services.impl;

import chipization.exceptions.EntityBadRequestException;
import chipization.exceptions.EntityForbiddenException;
import chipization.model.*;
import chipization.model.dto.GetAnimalsRequest;
import chipization.model.dto.TypeDto;
import chipization.model.enums.AnimalGender;
import chipization.model.enums.LifeStatus;
import chipization.repositories.*;
import chipization.services.AnimalService;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnimalServiceImpl implements AnimalService {

    private final TypeAnimalRepository typeAnimalRepository;
    private final AnimalRepository animalRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final VisitLocationRepository visitLocationRepository;

    @Override
    public Animal findAnimalById(Long animalId) {
        if (animalId <= 0) {
            throw new EntityBadRequestException("ID животного должен быть больше 0");
        }
        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new EntityNotFoundException("Не найдено животное"));
        if (animal.getVisitedLocations().isEmpty()) {
            animal.setVisitedLocations(null);
        }
        return animal;
    }

    @Override
    public Collection<Animal> search(GetAnimalsRequest getAnimalsRequest) {
        if (getAnimalsRequest.getSize() <= 0 || getAnimalsRequest.getFrom() < 0) {
            throw new EntityBadRequestException("Количество пропущенных элементов и страниц не должно быть меньше 0");
        }
        if (getAnimalsRequest.getStartDateTime() == null && getAnimalsRequest.getEndDateTime() == null
                && getAnimalsRequest.getChipperId() == null && getAnimalsRequest.getChippingLocationId() == null
                && getAnimalsRequest.getLifeStatus() == null && getAnimalsRequest.getGender() == null) {
            return new ArrayList<>();
        }

        QAnimal animal = QAnimal.animal;

        List<BooleanExpression> conditions = new ArrayList<>();

        if (getAnimalsRequest.hasStartTime()) {
            conditions.add(animal.chippingDateTime.after(getAnimalsRequest.getStartDateTime()));
        }

        if (getAnimalsRequest.hasEndTime()) {
            conditions.add(animal.chippingDateTime.before(getAnimalsRequest.getEndDateTime()));
        }

        if (getAnimalsRequest.hasChipperId()) {
            conditions.add(animal.chipperId.eq(getAnimalsRequest.getChipperId()));
        }

        if (getAnimalsRequest.hasChippingLocationId()) {
            conditions.add(animal.chippingLocationId.eq(getAnimalsRequest.getChippingLocationId()));
        }

        if (getAnimalsRequest.hasLifeStatus()) {
            conditions.add(animal.lifeStatus.eq(getAnimalsRequest.getLifeStatus()));
        }

        if (getAnimalsRequest.hasGender()) {
            conditions.add(animal.gender.eq(getAnimalsRequest.getGender()));
        }

        BooleanExpression finalCondition = conditions.stream()
                .reduce(BooleanExpression::and)
                .get();

        Sort sort = Sort.by("animal_id").ascending();

        Iterable<Animal> animals = animalRepository.findAll(finalCondition);
        List<Animal> result = new ArrayList<>();
        for (Animal currentAnimal : animals) {
            result.add(currentAnimal);
        }
        return result.stream().skip(getAnimalsRequest.getFrom()).limit(getAnimalsRequest.getSize()).sorted().collect(Collectors.toList());
        //return animalRepository.search(getAnimalsRequest.getStartDateTime());
    }


    @Override
    @Transactional
    public Animal createAnimal(Animal animal) {
        validateAnimalTypes(animal);
        validateGender(animal);
        userRepository.findById(animal.getChipperId())
                .orElseThrow(() -> new EntityNotFoundException("Не найден пользователь"));
        locationRepository.findById(animal.getChippingLocationId())
                .orElseThrow(() -> new EntityNotFoundException("Не найдена точка локации"));
        animal.setLifeStatus(LifeStatus.ALIVE);
        animal.setChippingDateTime(OffsetDateTime.now());
        VisitLocation visitLocation = new VisitLocation();
        visitLocation.setLocationPointId(animal.getChippingLocationId());
        visitLocation.setDateTimeOfVisitLocationPoint(animal.getChippingDateTime());
        visitLocationRepository.save(visitLocation);
        return animalRepository.save(animal);
    }

    @Override
    public Animal updateAnimal(Long animalId, Animal animal) {
        validateGender(animal);
        validateLifeStatus(animal);
        userRepository.findById(animal.getChipperId())
                .orElseThrow(() -> new EntityNotFoundException("Не найден пользователь"));
        locationRepository.findById(animal.getChippingLocationId())
                .orElseThrow(() -> new EntityNotFoundException("Не найдена точка локации"));
        Animal animalOld = animalRepository.findById(animalId)
                .orElseThrow(() -> new EntityNotFoundException("Не найдено животное"));
        if (animalOld.getLifeStatus().equals(LifeStatus.DEAD) && animal.getLifeStatus().equals(LifeStatus.ALIVE)) {
            throw new EntityBadRequestException("Животное не может воскреснуть :(");
        }
        animal.setId(animalId);
        animal.setAnimalTypes(animalOld.getAnimalTypes());
        animal.setChippingDateTime(animalOld.getChippingDateTime());
        animal.setVisitedLocations(animalOld.getVisitedLocations());
        animal.setDeathDateTime(animalOld.getDeathDateTime());
        return animalRepository.save(animal);
    }

    @Override
    public void deleteAnimal(Long animalId) {
        if (animalId <= 0) {
            throw new EntityBadRequestException("ID животного должен быть больше 0");
        }
        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new EntityNotFoundException("Не найдено животное"));
        if (animal.getVisitedLocations() != null) {
            if (animal.getVisitedLocations().size() > 0) {
                throw new EntityBadRequestException("Животное покинуло точку чипирования");
            }
        }

        animalRepository.deleteById(animalId);
    }

    @Override
    public Animal addAnimalTypeToAnimal(Long animalId, Long typeId) {
        if (animalId <= 0 || typeId <= 0) {
            throw new EntityBadRequestException("ID животного и типа животного должны быть больше 0");
        }
        TypeAnimal typeAnimal = typeAnimalRepository.findById(typeId)
                .orElseThrow(() -> new EntityNotFoundException("Не найден тип животного"));
        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new EntityNotFoundException("Не найдено животное"));
        List<Long> newList = animal.getAnimalTypes();
        newList.add(typeAnimal.getId());
        animal.setAnimalTypes(newList);
        return animalRepository.save(animal);
    }

    @Override
    @Transactional
    public Animal updateAnimalType(Long animalId, TypeDto typeDto) {
        if (animalId <= 0) {
            throw new EntityBadRequestException("ID животного и типы животного должны быть больше 0");
        }
        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new EntityNotFoundException("Не найдено животное"));
        List<Long> newList = animal.getAnimalTypes();
        if (!newList.contains(typeDto.getOldTypeId())) {
            throw new EntityNotFoundException("Не найден старый тип животного для изменения");
        }
        TypeAnimal oldTypeAnimal = typeAnimalRepository.findById(typeDto.getOldTypeId())
                .orElseThrow(() -> new EntityNotFoundException("Не найден тип животного"));
        newList.remove(oldTypeAnimal.getId());
        TypeAnimal newTypeAnimal = typeAnimalRepository.findById(typeDto.getNewTypeId())
                .orElseThrow(() -> new EntityNotFoundException("Не найден тип животного"));
        newList.add(newTypeAnimal.getId());
        animal.setAnimalTypes(newList);
        return animalRepository.save(animal);
    }

    @Override
    public Animal deleteAnimalTypeFromAnimal(Long animalId, Long typeId) {
        if (animalId <= 0 || typeId <= 0) {
            throw new EntityBadRequestException("ID животного и типа животного должны быть больше 0");
        }
        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new EntityNotFoundException("Не найдено животное"));
        if (!animal.getAnimalTypes().contains(typeId)) {
            throw new EntityNotFoundException("Не найден тип у животного для удаления");
        }
        if (animal.getAnimalTypes().size() == 1 && animal.getAnimalTypes().contains(typeId)) {
            throw new EntityBadRequestException("Нельзя удалить единственный тип животного");
        }
        List<Long> newList = animal.getAnimalTypes();
        TypeAnimal oldTypeAnimal = typeAnimalRepository.findById(typeId)
                .orElseThrow(() -> new EntityNotFoundException("Не найден тип животного"));
        newList.remove(oldTypeAnimal.getId());
        animal.setAnimalTypes(newList);
        return animalRepository.save(animal);
    }

    private void validateAnimalTypes(Animal animal) {
        if(animal.getAnimalTypes().isEmpty() || animal.getAnimalTypes() == null) {
            throw new EntityBadRequestException("Массив animalTypes не должны быть больше пустым");
        }
        Set<Long> newSet = new HashSet<>(animal.getAnimalTypes());
        if (newSet.size() != animal.getAnimalTypes().size()) {
            throw new DataIntegrityViolationException("В массиве animalTypes есть дубликаты");
        }
        for (Long currentType : animal.getAnimalTypes()) {
            if (currentType == null || currentType <= 0) {
                throw new EntityBadRequestException("Элементы массива animalTypes должны быть больше 0");
            }
            typeAnimalRepository.findById(currentType)
                    .orElseThrow(() -> new EntityNotFoundException("Не найден тип животного"));
        }
    }

    private void validateGender(Animal animal) {
        if (!animal.getGender().equals(AnimalGender.MALE)
                && !animal.getGender().equals(AnimalGender.FEMALE)
                && !animal.getGender().equals(AnimalGender.OTHER)) {
            throw new EntityBadRequestException("Неправильно указан гендерный признак животного доступные: MALE, FEMALE, OTHER");
        }
    }

    private void validateLifeStatus(Animal animal) {
        if (animal.getLifeStatus() == null) {
            throw new EntityBadRequestException("Жизненный статус животного не может быть равен null");
        }
        if (!animal.getLifeStatus().equals(LifeStatus.ALIVE) && !animal.getLifeStatus().equals(LifeStatus.DEAD)) {
            throw new EntityBadRequestException("Неправильно указан жизненный статус животного доступные: ALIVE, DEAD");
        }
    }
}
