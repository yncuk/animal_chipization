package chipization.model.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VisitLocationResponse {
    Long id;
    OffsetDateTime dateTimeOfVisitLocationPoint;
    Long locationPointId;
}
