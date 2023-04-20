package chipization.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AnimalAnalytics {
    String animalType;
    Long animalTypeId;
    Long quantityAnimals;
    Long animalsArrived;
    Long animalsGone;
}
