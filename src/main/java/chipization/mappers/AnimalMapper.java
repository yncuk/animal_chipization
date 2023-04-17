package chipization.mappers;

import chipization.model.Animal;
import chipization.model.dto.AnimalDto;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.Collectors;

@Component
public class AnimalMapper {
    public static AnimalDto toAnimalDto(Animal animal) {
        return new AnimalDto(
                animal.getId(),
                animal.getAnimalTypes(),
                animal.getWeight(),
                animal.getLength(),
                animal.getHeight(),
                animal.getGender(),
                animal.getLifeStatus(),
                animal.getChippingDateTime(),
                animal.getChipperId(),
                animal.getChippingLocationId(),
                animal.getVisitedLocations(),
                animal.getDeathDateTime()
        );
    }

    public static Collection<AnimalDto> allToAnimalDto(Collection<Animal> animals) {
        return animals.stream().map(AnimalMapper::toAnimalDto).collect(Collectors.toList());
    }
}
