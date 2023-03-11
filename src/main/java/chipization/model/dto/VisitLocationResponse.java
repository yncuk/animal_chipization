package chipization.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
public class VisitLocationResponse {
    private Long id;

    private OffsetDateTime dateTimeOfVisitLocationPoint;

    private Long locationPointId;
}
