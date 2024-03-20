INSERT INTO users VALUES
(1,'user1', 'Haarlem', 'North Holland', 'NL', 52.3803, 4.6422, '$2a$10$WeVR/Cd68PiLvodrehbqgO67mNKS7JquORFUTT/1cKcGXYQgUnHCK');

INSERT INTO roles VALUES (1, 'ROLE_ADMIN'),
                         (2, 'ROLE_USER');

INSERT INTO user_roles VALUES (1, 1);
INSERT INTO user_roles VALUES (1, 2);