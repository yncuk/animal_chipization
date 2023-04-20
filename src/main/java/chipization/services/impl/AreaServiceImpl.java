package chipization.services.impl;

import chipization.exceptions.EntityBadRequestException;
import chipization.mappers.AreaMapper;
import chipization.mappers.LocationMapper;
import chipization.model.*;
import chipization.model.dto.AreaDto;
import chipization.model.dto.LocationDto;
import chipization.model.geometry.Dot;
import chipization.model.geometry.Vector;
import chipization.repositories.AnimalRepository;
import chipization.repositories.AreaRepository;
import chipization.repositories.LocationRepository;
import chipization.repositories.VisitLocationRepository;
import chipization.services.AreaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.List;

import static chipization.model.geometry.Vector.cross;


@Service
@RequiredArgsConstructor
public class AreaServiceImpl implements AreaService {
    private final AreaRepository areaRepository;
    private final LocationRepository locationRepository;
    private final AnimalRepository animalRepository;
    private final VisitLocationRepository visitLocationRepository;

    @Override
    public AreaDto findById(Long areaId) {
        validateAreaId(areaId);
        return getAreaDtoWithLocationsDto(areaRepository.findById(areaId).
                orElseThrow(EntityNotFoundException::new));
    }

    @Override
    public AreaDto createArea(AreaDto areaDto) {
        if (areaDto.getAreaPoints().size() < 3) {
            throw new EntityBadRequestException("Точек должно быть не меньше 3");
        }
        checkDuplicate(areaDto.getAreaPoints());
        checkLineFunction(areaDto.getAreaPoints());
        checkLineCrossing(areaDto.getAreaPoints());

        List<Area> areas = areaRepository.findAllFetch();
        List<Long> listLocationsId = checkPolygon(areas, areaDto);

        Area newArea = areaRepository.save(AreaMapper.toArea(areaDto, listLocationsId));
        return getAreaDtoWithLocationsDto(newArea);
    }

    @Override
    public AreaDto update(Long areaId, AreaDto areaDto) {
        validateAreaId(areaId);
        if (areaDto.getAreaPoints().size() < 3) {
            throw new EntityBadRequestException("Точек должно быть не меньше 3");
        }

        areaRepository.findById(areaId)
                .orElseThrow(() -> new EntityNotFoundException("Не найдена зона с таким id"));
        checkDuplicate(areaDto.getAreaPoints());
        checkLineFunction(areaDto.getAreaPoints());
        checkLineCrossing(areaDto.getAreaPoints());

        List<Area> areas = areaRepository.findAllFetchForUpdate(areaId);
        List<Long> listLocationsId = checkPolygon(areas, areaDto);
        areaDto.setId(areaId);
        Area newArea = areaRepository.save(AreaMapper.toArea(areaDto, listLocationsId));
        return getAreaDtoWithLocationsDto(newArea);
    }

    private List<Long> checkPolygon(List<Area> areas, AreaDto areaDto) {
        List<AreaDto> areasWithLocationsDto = new ArrayList<>();
        if (!areas.isEmpty()) {
            for (Area currentArea : areas) {
                areasWithLocationsDto.add(getAreaDtoWithLocationsDto(currentArea));
            }
            checkPolygonIntersection(areasWithLocationsDto, areaDto);
            checkContainsAreaInOtherArea(areasWithLocationsDto, areaDto);
        }
        List<Long> listLocationsId = new ArrayList<>();
        for (Location currentLocation : LocationMapper.allToLocation(areaDto.getAreaPoints())) {
            if (locationRepository.findByLatitudeAndLongitude(currentLocation.getLatitude(), currentLocation.getLongitude()).isEmpty()) {
                Location location = locationRepository.save(currentLocation);
                listLocationsId.add(location.getId());
            } else {
                listLocationsId.add(locationRepository.findByLatitudeAndLongitude(currentLocation.getLatitude(), currentLocation.getLongitude()).get().getId());
            }
        }
        return listLocationsId;
    }

    @Override
    public void delete(Long areaId) {
        validateAreaId(areaId);
        areaRepository.findById(areaId)
                .orElseThrow(() -> new EntityNotFoundException("Не найдена зона с таким ID"));
        areaRepository.deleteById(areaId);
    }

    @Override
    public Analytics analytics(Long areaId, String startDate, String endDate) throws ParseException {
        validateAreaId(areaId);
        OffsetDateTime startOffsetDateTime = parseStringDate(startDate);
        OffsetDateTime endOffsetDateTime = parseStringDate(endDate);
        if (startOffsetDateTime.isAfter(endOffsetDateTime) || startOffsetDateTime.isEqual(endOffsetDateTime)) {
            throw new EntityBadRequestException("Дата начала позже даты окончания или они равны");
        }
        Area area = areaRepository.findById(areaId)
                .orElseThrow(() -> new EntityNotFoundException("Не найдена зона с таким ID"));
        List<Location> locations = new ArrayList<>();
        for (Long currentId : area.getAreaPoints()) {
            locations.add(locationRepository.findById(currentId)
                    .orElseThrow(() -> new EntityNotFoundException("Не найдена точка локации с таким ID")));
        }
        AnimalAnalytics animalAnalytics = new AnimalAnalytics();

        List<VisitLocation> allAnimalLocation = visitLocationRepository.getAllPoints(startOffsetDateTime, endOffsetDateTime);
        List<Animal> chippingAnimals = animalRepository.findAnimalsChippingLocationInTime(startOffsetDateTime, endOffsetDateTime);

        List<Animal> inAreaAnimals = new ArrayList<>();
        List<Animal> animalsArrived = new ArrayList<>();
        List<Animal> animalsGone = new ArrayList<>();

        for (Animal currentAnimal : chippingAnimals) {
            if (checkContainsPoint(locations, locationRepository.findById(currentAnimal.getChippingLocationId())
                    .orElseThrow(() -> new EntityNotFoundException("Не найдено животное с такой локацией чипирования")))
                    && !isOutOfZone(locations, currentAnimal.getId(), startOffsetDateTime, endOffsetDateTime)) {
                if (!inAreaAnimals.contains(currentAnimal)) {
                    inAreaAnimals.add(currentAnimal);
                }
            }
            if (visitLocationRepository.findPreviousChippingVisitedLocation(currentAnimal.getId(), currentAnimal.getChippingDateTime()).isPresent()) {
                Location previousChippingVisitedLocation = locationRepository.findById(visitLocationRepository
                                .findPreviousChippingVisitedLocation(currentAnimal.getId(), currentAnimal.getChippingDateTime()).get().getLocationPointId())
                        .orElseThrow(() -> new EntityNotFoundException("Не найдена точка локации для точки посещения"));
                if (!checkContainsPoint(locations, previousChippingVisitedLocation) && !animalsArrived.contains(currentAnimal)) {
                    animalsArrived.add(currentAnimal);
                }
            }
            if (visitLocationRepository.findNextChippingVisitedLocation(currentAnimal.getId(), currentAnimal.getChippingDateTime()).isPresent()) {

                Location nextChippingVisitedLocation = locationRepository.findById(visitLocationRepository
                                .findNextChippingVisitedLocation(currentAnimal.getId(), currentAnimal.getChippingDateTime()).get().getLocationPointId())
                        .orElseThrow(() -> new EntityNotFoundException("Не найдена точка локации для точки посещения"));
                if (!checkContainsPoint(locations, nextChippingVisitedLocation) && !animalsGone.contains(currentAnimal)) {
                    animalsGone.add(currentAnimal);
                }
            }

        }

        for (VisitLocation currentVisitLocation : allAnimalLocation) {
            Location currentLocation = locationRepository.findById(currentVisitLocation.getLocationPointId())
                    .orElseThrow(() -> new EntityNotFoundException("Не найдена точка локации для точки посещения"));

            if (checkContainsPoint(locations, currentLocation)) {
                Animal animal;
                if (animalRepository.findAnimalVisitLocation(currentVisitLocation.getId()).isPresent()) {
                    animal = animalRepository.findAnimalVisitLocation(currentVisitLocation.getId()).get();
                    if (!inAreaAnimals.contains(animal) && !isOutOfZone(locations, animal.getId(), startOffsetDateTime, endOffsetDateTime)) {
                        inAreaAnimals.add(animal);
                    }

                    if (visitLocationRepository.findPreviousVisitedLocation(animal.getId(), currentVisitLocation.getDateTimeOfVisitLocationPoint()).isPresent()) {
                        Location previousVisitedLocation = locationRepository.findById(visitLocationRepository
                                        .findPreviousVisitedLocation(animal.getId(), currentVisitLocation.getDateTimeOfVisitLocationPoint()).get().getLocationPointId())
                                .orElseThrow(() -> new EntityNotFoundException("Не найдена точка локации для точки посещения"));
                        if (!checkContainsPoint(locations, previousVisitedLocation) && !animalsArrived.contains(animal)) {
                            animalsArrived.add(animal);
                        }
                    }

                    if (visitLocationRepository.findNextVisitedLocation(animal.getId(), currentVisitLocation.getDateTimeOfVisitLocationPoint()).isPresent()) {
                        Location nextVisitedLocation = locationRepository.findById(visitLocationRepository
                                        .findNextVisitedLocation(animal.getId(), currentVisitLocation.getDateTimeOfVisitLocationPoint()).get().getLocationPointId())
                                .orElseThrow(() -> new EntityNotFoundException("Не найдена точка локации для точки посещения"));
                        if (!checkContainsPoint(locations, nextVisitedLocation) && !animalsGone.contains(animal)) {
                            animalsGone.add(animal);
                        }
                    }
                }
            }
        }

        long totalQuantityAnimals = inAreaAnimals.size();
        long totalAnimalsArrived = animalsArrived.size();
        long totalAnimalsGone = animalsGone.size();

        Analytics analytics = new Analytics();
        analytics.setTotalQuantityAnimals(totalQuantityAnimals);
        analytics.setTotalAnimalsArrived(totalAnimalsArrived);
        analytics.setTotalAnimalsGone(totalAnimalsGone);
        return analytics;
    }

    private boolean isOutOfZone(List<Location> locations, Long animalId, OffsetDateTime start, OffsetDateTime end) {
        List<Location> list = locationRepository.findAllVisitAnimalLocationInTime(animalId, start, end);
        if (list.isEmpty()) {
            return false;
        } else {
            for (Location currentLocation : list) {
                if (!checkContainsPoint(locations, currentLocation)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkContainsPoint(List<Location> locations, Location checkLocation) {
        int i;
        int j;
        boolean result = false;
        for (int k = 0; k < locations.size() - 2; k++) {
            if (isOnTheSegment(locations.get(k), locations.get(k + 1), checkLocation)) {
                return true;
            }
        }
        if (isOnTheSegment(locations.get(locations.size() - 1), locations.get(0), checkLocation)) {
            return true;
        }
        for (Location currentLocation : locations) {
            if (Objects.equals(currentLocation.getId(), checkLocation.getId())) {
                return true;
            }
        }
        for (i = 0, j = locations.size() - 1; i < locations.size(); j = i++) {
            if ((locations.get(i).getLatitude() > checkLocation.getLatitude()) != (locations.get(j).getLatitude() > checkLocation.getLatitude()) &&
                    (checkLocation.getLongitude() < (locations.get(j).getLongitude() - locations.get(i).getLongitude())
                            * (checkLocation.getLatitude() - locations.get(i).getLatitude())
                            / (locations.get(j).getLatitude() - locations.get(i).getLatitude())
                            + locations.get(i).getLongitude())) {
                result = !result;
            }
        }
        return result;
    }

    private boolean checkContainsAreas(List<Location> locations, Location checkLocation) {
        int i;
        int j;
        boolean result = false;
        for (i = 0, j = locations.size() - 1; i < locations.size(); j = i++) {
            if ((locations.get(i).getLatitude() > checkLocation.getLatitude()) != (locations.get(j).getLatitude() > checkLocation.getLatitude()) &&
                    (checkLocation.getLongitude() < (locations.get(j).getLongitude() - locations.get(i).getLongitude())
                            * (checkLocation.getLatitude() - locations.get(i).getLatitude())
                            / (locations.get(j).getLatitude() - locations.get(i).getLatitude())
                            + locations.get(i).getLongitude())) {
                result = !result;
            }
        }
        return result;
    }

    private boolean isOnTheSegment(Location main1, Location main2, Location checkLocation) {
        return (checkLocation.getLatitude() - main1.getLatitude())
                / (checkLocation.getLongitude() - main1.getLongitude())
                == (checkLocation.getLatitude() - main2.getLatitude())
                / (checkLocation.getLongitude() - main2.getLongitude());
    }

    private OffsetDateTime parseStringDate(String date) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date newDate = simpleDateFormat.parse(date);
        return newDate.toInstant().atOffset(ZoneOffset.UTC);
    }

    private AreaDto getAreaDtoWithLocationsDto(Area area) {
        List<Long> listLocationsId = area.getAreaPoints();
        List<LocationDto> newList = new ArrayList<>();
        for (Long currentId : listLocationsId) {
            newList.add(LocationMapper.toLocationDto(locationRepository.findById(currentId).get()));
        }
        AreaDto areaDto = AreaMapper.toAreaDto(area);
        areaDto.setAreaPoints(newList);
        return areaDto;
    }

    private void validateAreaId(Long areaId) {
        if (areaId <= 0) {
            throw new EntityBadRequestException("ID зоны должен быть больше 0");
        }
    }

    private void checkDuplicate(List<LocationDto> list) {
        Set<LocationDto> set = new HashSet<>(list);
        if (set.size() < list.size()) {
            throw new EntityBadRequestException("В зоне дубликаты точек");
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

    private boolean checkOneLine(List<LocationDto> list) {
        for (int i = 1; i < list.size() - 1; i++) {
            Double x1 = list.get(i - 1).getLatitude();
            Double y1 = list.get(i - 1).getLongitude();
            Double x2 = list.get(i).getLatitude();
            Double y2 = list.get(i).getLongitude();
            Double x3 = list.get(i + 1).getLatitude();
            Double y3 = list.get(i + 1).getLongitude();
            if ((x1 - x2) * (y1 - y3) == (x1 - x3) * (y1 - y2)) {
                return true;
            }
        }
        return false;
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

    private void checkLineCrossingForNewPolygon(List<LocationDto> list) {
        if (checkOneLine(list)) {
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

            if (linesIntersectForNewPolygon(new Dot(x1, y1), new Dot(x2, y2), new Dot(x3, y3), new Dot(x4, y4))) {
                throw new EntityBadRequestException("Границы зоны пересекаются с существующей зоной");
            }
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

    public static boolean fromDifferentSidesForNewPolygon(Vector main, Vector v1, Vector v2) {
        double product1 = cross(main, v1), product2 = cross(main, v2);
        return (product1 >= 0 && product2 <= 0 || product1 <= 0 && product2 >= 0);
    }

    public static boolean linesIntersectForNewPolygon(Dot a, Dot b, Dot c, Dot d) {
        Vector main = new Vector(a, b);
        Vector v1 = new Vector(a, c);
        Vector v2 = new Vector(a, d);


        if (main.equals(v1) && main.equals(v2) || (a.equals(c) || a.equals(d) || b.equals(c) || b.equals(d))) {
            return false;
        }

        if (fromDifferentSidesForNewPolygon(main, v1, v2)) {
            main = new Vector(c, d);
            v1 = new Vector(c, a);
            v2 = new Vector(c, b);
            return fromDifferentSidesForNewPolygon(main, v1, v2);
        }
        return false;
    }

    private void checkContainsAreaInOtherArea(List<AreaDto> areas, AreaDto newArea) {
        Location location = LocationMapper.toLocationFromLocationDto(newArea.getAreaPoints().get(0));
        for (AreaDto currentArea : areas) {
            List<Location> locations = LocationMapper.allToLocation(currentArea.getAreaPoints());
            if (checkContainsAreas(locations, location)) {
                throw new EntityBadRequestException("Зона находится внутри существующей зоны");
            }
            Location location2 = LocationMapper.toLocationFromLocationDto(currentArea.getAreaPoints().get(0));
            List<Location> locations2 = LocationMapper.allToLocation(newArea.getAreaPoints());
            if (checkContainsAreas(locations2, location2)) {
                throw new EntityBadRequestException("Зона находится вокруг существующей зоны");
            }
        }
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
                    checkLineCrossingForNewPolygon(currentListLocationDto);
                    currentListLocationDto.remove(newLocationDto.get(j));
                    currentListLocationDto.remove(newLocationDto.get(j + 1));

                    extremeLocationDto.add(newLocationDto.get(j));
                    extremeLocationDto.add(newLocationDto.get(j + 1));
                    checkLineCrossingForNewPolygon(extremeLocationDto);
                    extremeLocationDto.remove(newLocationDto.get(j));
                    extremeLocationDto.remove(newLocationDto.get(j + 1));
                }
                currentListLocationDto.add(newLocationDto.get(0));
                currentListLocationDto.add(newLocationDto.get(newLocationDto.size() - 1));
                checkLineCrossingForNewPolygon(currentListLocationDto);
            }
        }
    }
}
