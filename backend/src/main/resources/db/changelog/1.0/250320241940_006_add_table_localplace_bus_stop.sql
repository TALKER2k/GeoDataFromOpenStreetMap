CREATE TABLE lift_gate_osm.local_places_bus_stop
(
    bus_stop_id BIGINT PRIMARY KEY,
    lon      DOUBLE PRECISION,
    lat      DOUBLE PRECISION,
    name     VARCHAR(100)
);