package chipization.services;

import chipization.model.Analytics;
import chipization.model.dto.AreaDto;

import java.text.ParseException;

public interface AreaService {
    AreaDto findById(Long areaId);
    AreaDto createArea(AreaDto areaDto);
    AreaDto update(Long areaId, AreaDto areaDto);
    void delete(Long areaId);
    Analytics analytics(Long areaId, String startDate, String endDate) throws ParseException;
}
