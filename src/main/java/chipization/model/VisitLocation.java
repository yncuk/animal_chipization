package chipization.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "visit_locations")
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VisitLocation {
    @Id
    @Column(name = "visit_location_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "date_time_of_visit_location_point")
    OffsetDateTime dateTimeOfVisitLocationPoint;

    @Column(name = "location_point_id")
    Long locationPointId;
}
