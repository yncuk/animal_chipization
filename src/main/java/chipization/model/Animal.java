package chipization.model;


import chipization.model.enums.AnimalGender;
import chipization.model.enums.LifeStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Table(name = "animals")
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Animal implements Serializable {

    @Id
    @Column(name = "animal_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ElementCollection
    @CollectionTable(name = "animals_type_animal",
            joinColumns = @JoinColumn(name = "animal_id"))
    @JoinColumn(name = "animal_id")
    @Column(name = "type_id")
    List<Long> animalTypes;

    @Positive
    @NotNull
    Float weight;

    @Positive
    @NotNull
    Float length;

    @Positive
    @NotNull
    Float height;

    @NotNull
    @Enumerated(EnumType.STRING)
    AnimalGender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "life_status")
    LifeStatus lifeStatus;

    @Column(name = "chipping_date_time")
    OffsetDateTime chippingDateTime;

    @Positive
    @NotNull
    @Column(name = "chipper_id")
    Integer chipperId;

    @Positive
    @NotNull
    @Column(name = "chipping_location_id")
    Long chippingLocationId;

    @ElementCollection
    @CollectionTable(name = "animals_visit_locations",
            joinColumns = @JoinColumn(name = "animal_id"))
    @JoinColumn(name = "animal_id")
    @Column(name = "location_id")
    List<Long> visitedLocations;

    @Column(name = "death_date_time")
    OffsetDateTime deathDateTime;

}
