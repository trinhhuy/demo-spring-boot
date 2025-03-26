
CREATE TABLE orders
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    product_id BIGINT                NULL,
    amount     DOUBLE                NULL,
    status     SMALLINT              NULL,
    CONSTRAINT pk_orders PRIMARY KEY (id)
);