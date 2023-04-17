package chipization.model.dto;

import chipization.model.enums.AnimalGender;
import chipization.model.enums.LifeStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;


import java.time.OffsetDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AnimalDto {

    Long id;

    List<Long> animalTypes;

    Float weight;

    Float length;

    Float height;

    AnimalGender gender;

    LifeStatus lifeStatus;

    OffsetDateTime chippingDateTime;

    Integer chipperId;

    Long chippingLocationId;

    List<Long> visitedLocations;

    OffsetDateTime deathDateTime;
}
