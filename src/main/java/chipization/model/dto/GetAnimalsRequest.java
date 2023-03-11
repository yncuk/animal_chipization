package chipization.model.dto;

import chipization.model.enums.AnimalGender;
import chipization.model.enums.LifeStatus;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetAnimalsRequest {
    OffsetDateTime startDateTime;
    OffsetDateTime endDateTime;
    Integer chipperId;
    Long chippingLocationId;
    LifeStatus lifeStatus;
    AnimalGender gender;
    int from;
    int size;

    public static GetAnimalsRequest of(OffsetDateTime startDateTime,
                                       OffsetDateTime endDateTime,
                                       Integer chipperId,
                                       Long chippingLocationId,
                                       String lifeStatus,
                                       String gender,
                                       int from,
                                       int size) {
        GetAnimalsRequest request = new GetAnimalsRequest();
        if (startDateTime != null) {
            request.setStartDateTime(startDateTime);
        }
        if (endDateTime != null) {
            request.setEndDateTime(endDateTime);
        }
        if (chipperId != null) {
            request.setChipperId(chipperId);
        }
        if (chippingLocationId != null) {
            request.setChippingLocationId(chippingLocationId);
        }
        if (lifeStatus != null) {
            request.setLifeStatus(LifeStatus.valueOf(lifeStatus.toUpperCase()));
        }
        if (gender != null) {
            request.setGender(AnimalGender.valueOf(gender.toUpperCase()));
        }
        request.setFrom(from);
        request.setSize(size);
        return request;
    }

    public boolean hasStartTime() {
        return startDateTime != null;
    }

    public boolean hasEndTime() {
        return endDateTime != null;
    }

    public boolean hasChipperId() {
        return chipperId != null;
    }

    public boolean hasChippingLocationId() {
        return chippingLocationId != null;
    }

    public boolean hasLifeStatus() {
        return lifeStatus != null;
    }

    public boolean hasGender() {
        return gender != null;
    }
}
