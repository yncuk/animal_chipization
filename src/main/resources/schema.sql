create table if not exists users
(
    user_id    serial
        primary key
        unique,
    first_name varchar not null,
    last_name  varchar not null,
    email      varchar not null
        unique,
    password   varchar not null
);
create table if not exists locations
(
    location_id bigserial
        primary key
        unique,
    latitude    double precision not null,
    longitude   double precision not null
);
create table if not exists animals
(
    animal_id            bigserial
        constraint animals_pk
            primary key
        unique,
    weight               double precision not null,
    length               double precision not null,
    height               double precision not null,
    gender               varchar not null,
    life_status          varchar,
    chipping_date_time   timestamp,
    chipper_id           integer not null
        constraint animals_users_user_id_fk
            references users,
    chipping_location_id bigint not null
        constraint animals_locations_location_id_fk
            references locations,
    death_date_time      timestamp
);
create table if not exists type_animal
(
    type_id   bigserial
        primary key
        unique,
    type      varchar not null
        unique
);
create table if not exists animals_type_animal
(
    animal_id bigint not null
        constraint animals_type_animal_animals_animal_id_fk
            references animals,
    type_id   bigint not null
        constraint animals_type_animal_type_animal_type_id_fk
            references type_animal,
    constraint animals_type_animal_pk
        primary key (animal_id, type_id)
);
create table if not exists visit_locations
(
    visit_location_id      bigserial
        primary key
        unique,
    date_time_of_visit_location_point timestamp,
    location_point_id                 bigint
        constraint visit_location_locations_location_id_fk
            references locations
);
create table if not exists animals_visit_locations
(
    animal_id   bigint not null
        constraint animals_locations_animals_animal_id_fk
            references animals,
    location_id bigint not null
        constraint animals_visit_locations_visit_locations_visit_location_id_fk
            references locations,
    constraint animals_locations_pk
        primary key (animal_id, location_id)
);