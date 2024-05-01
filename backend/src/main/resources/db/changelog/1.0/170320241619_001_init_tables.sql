CREATE SCHEMA lift_gate_osm;

CREATE TABLE lift_gate_osm.country
(
    country_id       BIGINT PRIMARY KEY,
    country_name     VARCHAR(255),
    ISO3166_1        VARCHAR(255),
    ISO3166_1_alpha2 VARCHAR(255)
);

CREATE TABLE lift_gate_osm.city
(
    city_id      BIGINT PRIMARY KEY,
    city_name    VARCHAR(255),
    country_id BIGINT,
    FOREIGN KEY (country_id) REFERENCES lift_gate_osm.country (country_id)
);

CREATE TABLE lift_gate_osm.local_places_lift_gates
(
    gates_id BIGINT PRIMARY KEY,
    lon      DOUBLE PRECISION,
    lat      DOUBLE PRECISION,
    name     VARCHAR(100),
    city_id BIGINT,
    FOREIGN KEY (city_id) REFERENCES lift_gate_osm.city (city_id)
);

CREATE TABLE lift_gate_osm.users
(
    id        BIGINT PRIMARY KEY,
    username  VARCHAR(50),
    city      VARCHAR(50),
    region    VARCHAR(50),
    country   VARCHAR(50),
    latitude  DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    password  VARCHAR(100)
);

CREATE TABLE lift_gate_osm.roles
(
    id        BIGINT PRIMARY KEY,
    name VARCHAR(10)
);


CREATE TABLE lift_gate_osm.user_roles
(
    user_id BIGINT REFERENCES lift_gate_osm.users (id),
    role_id BIGINT REFERENCES lift_gate_osm.roles (id)
);