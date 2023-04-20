package chipization.repositories;

import chipization.model.Area;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AreaRepository extends JpaRepository<Area, Long> {

    @Query(" select distinct a from Area a " +
            "left join fetch a.areaPoints")
    List<Area> findAllFetch();

    @Query(" select distinct a from Area a " +
            "left join fetch a.areaPoints " +
            "where a.id <> ?1")
    List<Area> findAllFetchForUpdate(Long areaId);

}
