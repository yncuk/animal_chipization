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
    latitude    double precision,
    longitude   double precision
);
create table if not exists animals
(
    animal_id            bigserial
        constraint animals_pk
            primary key
        unique,
    weight               double precision,
    length               double precision,
    height               double precision,
    gender               varchar,
    life_status          varchar,
    chipping_date_time   timestamp,
    chipper_id           integer,
    chipping_location_id bigint,
    death_date_time      timestamp
);
create table if not exists type_animal
(
    type_id   bigserial
        primary key
        unique,
    type      varchar,
    animal_id bigint
        constraint type_animal_animals_null_fk
            references animals
);