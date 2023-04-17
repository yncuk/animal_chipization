package chipization.mappers;

import chipization.model.Area;
import chipization.model.dto.AreaDto;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AreaMapper {
    public static AreaDto toAreaDto(Area area) {
        return new AreaDto(
                area.getId(),
                area.getName(),
                LocationMapper.allToLocationDto(area.getAreaPoints())
        );
    }

    public static Area toArea(AreaDto areaDto) {
        Area newArea = new Area();
        newArea.setId(areaDto.getId());
        newArea.setName(areaDto.getName());
        newArea.setAreaPoints(LocationMapper.allToLocation(areaDto.getAreaPoints()));
        return newArea;
    }

    public static List<AreaDto> allToAreaDto(Collection<Area> areas) {
        return areas.stream().map(AreaMapper::toAreaDto).collect(Collectors.toList());
    }
}
