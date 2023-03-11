package chipization.mappers;

import chipization.model.TypeAnimal;
import chipization.model.User;
import chipization.model.dto.TypeAnimalDto;
import chipization.model.dto.UserDto;
import org.springframework.stereotype.Component;

@Component
public class TypeAnimalMapper {
    public static TypeAnimalDto toTypeAnimalDto(TypeAnimal typeAnimal) {
        return new TypeAnimalDto(
                typeAnimal.getId(),
                typeAnimal.getType()
        );
    }
}
