package chipization.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "locations")
@Getter
@Setter
@ToString
public class Location {
    @Id
    @Column(name = "location_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(-90)
    @Max(90)
    @NotNull
    private Double latitude;

    @Min(-180)
    @Max(180)
    @NotNull
    private Double longitude;
}
