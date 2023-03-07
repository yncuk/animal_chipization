package chipization.model;


import chipization.model.enums.AnimalGender;
import chipization.model.enums.LifeStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "animals")
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Animal {

    @Id
    @Column(name = "animal_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToMany(mappedBy = "animal_id")
    List<TypeAnimal> animalTypes;

    Float weight;

    Float length;

    Float height;

    @Enumerated(EnumType.STRING)
    AnimalGender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "life_status")
    LifeStatus lifeStatus;

    @Column(name = "chipping_date_time")
    LocalDateTime chippingDateTime;

    @Column(name = "chipper_id")
    Integer chipperId;

    @Column(name = "chipping_location_id")
    Long chippingLocationId;

    @OneToMany(mappedBy = "animal_id")
    List<Location> visitedLocations;

    @Column(name = "death_date_time")
    LocalDateTime deathDateTime;

}
