package chipization.services.impl;

import chipization.exceptions.EntityBadRequestException;
import chipization.mappers.AreaMapper;
import chipization.mappers.LocationMapper;
import chipization.model.Area;
import chipization.model.Location;
import chipization.model.dto.AreaDto;
import chipization.model.dto.LocationDto;
import chipization.repositories.AreaRepository;
import chipization.repositories.LocationRepository;
import chipization.services.AreaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static chipization.services.impl.AreaServiceImpl.Vector.cross;

@Service
@RequiredArgsConstructor
public class AreaServiceImpl implements AreaService {
    private final AreaRepository areaRepository;
    private final LocationRepository locationRepository;

    @Override
    public AreaDto findById(Long areaId) {
        validateAreaId(areaId);
        return AreaMapper.toAreaDto(areaRepository.findById(areaId).
                orElseThrow(EntityNotFoundException::new));
    }

    @Override
    public AreaDto createArea(AreaDto areaDto) {
        checkLineFunction(areaDto.getAreaPoints());
        checkLineCrossing(areaDto.getAreaPoints());
        List<AreaDto> areas = AreaMapper.allToAreaDto(areaRepository.findAllFetch());
        checkPolygonIntersection(areas, areaDto);
        Area newArea = areaRepository.save(AreaMapper.toArea(areaDto));
        for (Location currentLocation : LocationMapper.allToLocation(areaDto.getAreaPoints())) {
            currentLocation.setAreaId(newArea.getId());
            locationRepository.save(currentLocation);
        }
        return AreaMapper.toAreaDto(newArea);
    }

    @Override
    public AreaDto update(Long areaId, AreaDto areaDto) {
        validateAreaId(areaId);
        checkLineFunction(areaDto.getAreaPoints());
        checkLineCrossing(areaDto.getAreaPoints());
        List<AreaDto> areas = AreaMapper.allToAreaDto(areaRepository.findAllFetch());
        checkPolygonIntersection(areas, areaDto);
        Area newArea = areaRepository.save(AreaMapper.toArea(areaDto));
        for (Location currentLocation : LocationMapper.allToLocation(areaDto.getAreaPoints())) {
            currentLocation.setAreaId(newArea.getId());
            locationRepository.save(currentLocation);
        }
        return AreaMapper.toAreaDto(newArea);
    }

    @Override
    public void delete(Long areaId) {
        validateAreaId(areaId);
        areaRepository.findById(areaId)
                .orElseThrow(() -> new EntityNotFoundException("Не найдена зона с таким ID"));
        areaRepository.deleteById(areaId);
    }

    private void validateAreaId(Long areaId) {
        if (areaId <= 0) {
            throw new EntityBadRequestException("ID зоны должен быть больше 0");
        }
    }

    private void checkLineFunction(List<LocationDto> list) {
        for (int i = 1; i < list.size() - 1; i++) {
            Double x1 = list.get(i - 1).getLatitude();
            Double y1 = list.get(i - 1).getLongitude();
            Double x2 = list.get(i).getLatitude();
            Double y2 = list.get(i).getLongitude();
            Double x3 = list.get(i + 1).getLatitude();
            Double y3 = list.get(i + 1).getLongitude();
            if ((x1 - x2) * (y1 - y3) == (x1 - x3) * (y1 - y2)) {
                throw new EntityBadRequestException("Точки лежат на одной прямой");
            }
        }
    }

    private void checkLineCrossing(List<LocationDto> list) {
        if (list.size() == 3) {
            return;
        }
        for (int i = 0; i < list.size() - 3; i++) {
            Double x1 = list.get(i).getLatitude();
            Double y1 = list.get(i).getLongitude();
            Double x2 = list.get(i + 1).getLatitude();
            Double y2 = list.get(i + 1).getLongitude();
            Double x3 = list.get(i + 2).getLatitude();
            Double y3 = list.get(i + 2).getLongitude();
            Double x4 = list.get(i + 3).getLatitude();
            Double y4 = list.get(i + 3).getLongitude();

            if (linesIntersect(new Dot(x1, y1), new Dot(x2, y2), new Dot(x3, y3), new Dot(x4, y4))) {
                throw new EntityBadRequestException("Границы зоны пересекаются между собой");
            }
            Double x5 = list.get(0).getLatitude();
            Double y5 = list.get(0).getLongitude();
            Double x6 = list.get(list.size() - 1).getLatitude();
            Double y6 = list.get(list.size() - 1).getLongitude();
            if (linesIntersect(new Dot(x5, y5), new Dot(x6, y6), new Dot(x2, y2), new Dot(x3, y3))) {
                throw new EntityBadRequestException("Границы зоны пересекаются между собой");
            }
        }
    }

    static class Dot {
        double x;
        double y;

        Dot(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    public static class Vector {
        double x;
        double y;

        public Vector(Dot d1, Dot d2) {
            this.x = d2.x - d1.x;
            this.y = d2.y - d1.y;
        }

        public static double cross(Vector v1, Vector v2) {
            return v1.x * v2.y - v1.y * v2.x;
        }
    }

    public static boolean fromDifferentSides(Vector main, Vector v1, Vector v2) {
        double product1 = cross(main, v1), product2 = cross(main, v2);
        return (product1 >= 0 && product2 <= 0 || product1 <= 0 && product2 >= 0);
    }

    public static boolean linesIntersect(Dot a, Dot b, Dot c, Dot d) {
        Vector main = new Vector(a, b);
        Vector v1 = new Vector(a, c);
        Vector v2 = new Vector(a, d);

        if (fromDifferentSides(main, v1, v2)) {
            main = new Vector(c, d);
            v1 = new Vector(c, a);
            v2 = new Vector(c, b);
            return fromDifferentSides(main, v1, v2);
        }
        return false;
    }


    private void checkPolygonIntersection(List<AreaDto> areas, AreaDto newArea) {
        for (AreaDto currentAreas : areas) {
            List<LocationDto> locationDto = currentAreas.getAreaPoints();
            List<LocationDto> newLocationDto = newArea.getAreaPoints();
            for (int i = 0; i < locationDto.size() - 1; i++) {
                List<LocationDto> extremeLocationDto = new ArrayList<>();
                extremeLocationDto.add(locationDto.get(0));
                extremeLocationDto.add(locationDto.get(locationDto.size() - 1));
                List<LocationDto> currentListLocationDto = new ArrayList<>();
                currentListLocationDto.add(locationDto.get(i));
                currentListLocationDto.add(locationDto.get(i + 1));
                for (int j = 0; j < newLocationDto.size() - 1; j++) {
                    currentListLocationDto.add(newLocationDto.get(j));
                    currentListLocationDto.add(newLocationDto.get(j + 1));
                    checkLineCrossing(currentListLocationDto);
                    currentListLocationDto.remove(newLocationDto.get(j));
                    currentListLocationDto.remove(newLocationDto.get(j + 1));

                    extremeLocationDto.add(newLocationDto.get(j));
                    extremeLocationDto.add(newLocationDto.get(j + 1));
                    checkLineCrossing(extremeLocationDto);
                    extremeLocationDto.remove(newLocationDto.get(j));
                    extremeLocationDto.remove(newLocationDto.get(j + 1));
                }
                currentListLocationDto.add(newLocationDto.get(0));
                currentListLocationDto.add(newLocationDto.get(newLocationDto.size() - 1));
                checkLineCrossing(currentListLocationDto);
            }
        }
    }


}
