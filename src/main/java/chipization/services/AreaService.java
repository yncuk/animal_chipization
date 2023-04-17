package chipization.services;

import chipization.model.dto.AreaDto;

public interface AreaService {
    AreaDto findById(Long areaId);
    AreaDto createArea(AreaDto areaDto);
    AreaDto update(Long areaId, AreaDto areaDto);
    void delete(Long areaId);

}
