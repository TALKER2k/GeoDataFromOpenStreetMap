INSERT INTO lift_gate_osm.users VALUES
(1,'user1', 'Haarlem', 'North Holland', 'NL', 52.3803, 4.6422, '$2a$10$WeVR/Cd68PiLvodrehbqgO67mNKS7JquORFUTT/1cKcGXYQgUnHCK');

INSERT INTO lift_gate_osm.roles VALUES (1, 'ADMIN'),
                         (2, 'USER');

INSERT INTO lift_gate_osm.user_roles VALUES (1, 1);
INSERT INTO lift_gate_osm.user_roles VALUES (1, 2);