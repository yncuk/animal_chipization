package chipization.mappers;

import chipization.model.VisitLocation;
import chipization.model.dto.VisitLocationResponse;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.Collectors;

@Component
public class VisitLocationMapper {
    public static VisitLocationResponse toVisitLocationResponse(VisitLocation visitLocation) {
        return new VisitLocationResponse(
                visitLocation.getId(),
                visitLocation.getDateTimeOfVisitLocationPoint(),
                visitLocation.getLocationPointId()
        );
    }

    public static Collection<VisitLocationResponse> allToVisitLocationResponse(Collection<VisitLocation> visitLocation) {
        return visitLocation.stream().map(VisitLocationMapper::toVisitLocationResponse).collect(Collectors.toList());
    }
}
