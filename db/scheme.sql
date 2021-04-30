CREATE DATABASE todolist;

CREATE TABLE categories (
        id SERIAL PRIMARY KEY,
        name VARCHAR NOT NULL
);

INSERT INTO categories(name)
VALUES ('Urgent'),
       ('Not urgent'),
       ('Important'),
       ('Not important'),
       ('Projects'),
       ('Routine tasks'),
       ('Features'),
       ('Bugs'),
       ('Daily'),
       ('Weekly'),
       ('Monthly')
;
