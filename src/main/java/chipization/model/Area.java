package chipization.model;

import chipization.model.dto.LocationDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Entity
@Table(name = "areas")
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Area {

    @Id
    @Column(name = "area_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String name;

    /*@ElementCollection
    @CollectionTable(name = "areas_points",
            joinColumns = @JoinColumn(name = "area_id"))
    @JoinColumn(name = "area_id")
    @Column(name = "location_id")*/

    @OneToMany(mappedBy = "areaId")
    List<Location> areaPoints;
}

