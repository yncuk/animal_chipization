package chipization.mappers;

import chipization.model.Area;
import chipization.model.dto.AreaDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AreaMapper {
    public static AreaDto toAreaDto(Area area) {
        return new AreaDto(
                area.getId(),
                area.getName(),
                null
        );
    }

    public static Area toArea(AreaDto areaDto, List<Long> listLocationsId) {
        Area newArea = new Area();
        newArea.setId(areaDto.getId());
        newArea.setName(areaDto.getName());
        newArea.setAreaPoints(listLocationsId);
        return newArea;
    }
}
