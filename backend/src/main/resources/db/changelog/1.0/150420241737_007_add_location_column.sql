ALTER TABLE local_places_lift_gates
    ADD COLUMN city_location varchar(200);

CREATE TABLE country
(
    country_id       BIGINT PRIMARY KEY,
    country_name     VARCHAR(255),
    ISO3166_1        VARCHAR(255),
    ISO3166_1_alpha2 VARCHAR(255)
);

CREATE TABLE city
(
    city_id      BIGINT PRIMARY KEY,
    city_name    VARCHAR(255),
    country_name BIGINT,
    FOREIGN KEY (country_name) REFERENCES country (country_id)
);

ALTER TABLE local_places_lift_gates
    ADD COLUMN FOREIGN KEY (city_name) REFERENCES city(city_id);