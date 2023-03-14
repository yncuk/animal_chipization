package chipization.model.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VisitLocationDto {

    @Positive
    @NotNull
    Long visitedLocationPointId;

    @Positive
    @NotNull
    Long locationPointId;
}
