package chipization.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "visit_locations")
@Getter
@Setter
@ToString
public class VisitLocation {
    @Id
    @Column(name = "visit_location_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_time_of_visit_location_point")
    private LocalDateTime dateTimeOfVisitLocationPoint;

    @Column(name = "location_point_id")
    private Long locationPointId;

    @Column(name = "animal_id")
    private Long animalId;
}
