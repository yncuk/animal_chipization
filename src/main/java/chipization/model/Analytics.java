package chipization.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Analytics {

    Long totalQuantityAnimals;
    Long totalAnimalsArrived;
    Long totalAnimalsGone;
    List<AnimalAnalytics> animalsAnalytics;
}
