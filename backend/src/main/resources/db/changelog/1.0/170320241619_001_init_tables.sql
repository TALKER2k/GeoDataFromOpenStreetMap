CREATE TABLE local_places_lift_gates
(
    gates_id BIGINT PRIMARY KEY,
    lon      DOUBLE PRECISION,
    lat      DOUBLE PRECISION,
    name     VARCHAR(100)
);

CREATE TABLE users
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

CREATE TABLE roles
(
    id        BIGINT PRIMARY KEY,
    name VARCHAR(10)
);


CREATE TABLE user_roles
(
    user_id BIGINT REFERENCES users (id),
    role_id BIGINT REFERENCES roles (id)
);