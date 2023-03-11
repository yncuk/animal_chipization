package chipization.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
public class VisitLocationDto {

    @Positive
    @NotNull
    private Long visitedLocationPointId;

    @Positive
    @NotNull
    private Long locationPointId;

}
