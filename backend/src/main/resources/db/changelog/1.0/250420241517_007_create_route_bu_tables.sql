CREATE TABLE route
(
    id                       BIGINT PRIMARY KEY NOT NULL,
    name                     VARCHAR(255),
    from_location            VARCHAR(255),
    to_location              VARCHAR(255),
    network                  VARCHAR(255),
    operator                 VARCHAR(255),
    public_transport_version INT,
    ref                      VARCHAR(255),
    route                    VARCHAR(255)
);

CREATE TABLE stop
(
    id   BIGINT PRIMARY KEY NOT NULL,
    name VARCHAR(255),
    lat  FLOAT,
    lon  FLOAT
);

CREATE TABLE route_stop
(
    id       UUID PRIMARY KEY NOT NULL,
    route_id BIGINT           NOT NULL,
    stop_id  BIGINT           NOT NULL,
    sequence INT,
    FOREIGN KEY (route_id) REFERENCES route (id),
    FOREIGN KEY (stop_id) REFERENCES stop (id)
);

CREATE TABLE line_string
(
    id_line  UUID PRIMARY KEY NOT NULL,
    id       BIGINT NOT NULL,
    route_id BIGINT,
    geom     GEOMETRY(LineString, 3857),
    FOREIGN KEY (route_id) REFERENCES route (id)
);

CREATE TABLE point
(
    id      UUID PRIMARY KEY NOT NULL,
    stop_id BIGINT,
    point    GEOMETRY(point, 3857),
    FOREIGN KEY (stop_id) REFERENCES stop (id)
);

