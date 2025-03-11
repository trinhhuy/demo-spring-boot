CREATE TABLE users
(
    id       BIGINT AUTO_INCREMENT NOT NULL,
    username VARCHAR(255) NULL,
    password VARCHAR(255) NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);
-- CREATE TABLE books
-- (
--     id      BIGINT AUTO_INCREMENT NOT NULL,
--     title   VARCHAR(255)          NULL,
--     author  VARCHAR(255)          NULL,
--     user_id BIGINT                NOT NULL,
--     CONSTRAINT pk_books PRIMARY KEY (id)
-- );
--
-- ALTER TABLE books
--     ADD CONSTRAINT FK_BOOKS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

CREATE TABLE books
(
    id      BIGINT AUTO_INCREMENT NOT NULL,
    title   VARCHAR(255)          NULL,
    author  VARCHAR(255)          NULL,
    user_id BIGINT                NOT NULL,
    CONSTRAINT pk_books PRIMARY KEY (id),
    CONSTRAINT fk_books_on_user FOREIGN KEY (user_id) REFERENCES users (id)
);


