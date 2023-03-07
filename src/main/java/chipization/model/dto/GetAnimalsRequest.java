package chipization.model.dto;

import chipization.model.enums.AnimalGender;
import chipization.model.enums.LifeStatus;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetAnimalsRequest {
    LocalDateTime startDateTime;
    LocalDateTime endDateTime;
    int chipperId;
    Long chippingLocationId;
    LifeStatus lifeStatus;
    AnimalGender gender;
    int from;
    int size;

    public static GetAnimalsRequest of(LocalDateTime startDateTime,
                                       LocalDateTime endDateTime,
                                       int chipperId,
                                       Long chippingLocationId,
                                       String lifeStatus,
                                       String gender,
                                       int from,
                                       int size) {
        GetAnimalsRequest request = new GetAnimalsRequest();
        request.setStartDateTime(startDateTime);
        request.setEndDateTime(endDateTime);
        request.setChipperId(chipperId);
        request.setChippingLocationId(chippingLocationId);
        request.setLifeStatus(LifeStatus.valueOf(lifeStatus.toUpperCase()));
        request.setGender(AnimalGender.valueOf(gender.toUpperCase()));
        request.setFrom(from);
        request.setSize(size);
        return request;
    }
}
