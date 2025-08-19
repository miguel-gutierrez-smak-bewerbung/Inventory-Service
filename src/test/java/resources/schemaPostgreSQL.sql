
CREATE SCHEMA IF NOT EXISTS local;

CREATE TABLE IF NOT EXISTS local.article (
   id                 varchar(36) PRIMARY KEY,
    product_id         varchar(36),
    article_number     varchar(255) NOT NULL,
    unit               varchar(64)  NOT NULL,
    category           varchar(255),
    quantity           integer      NOT NULL DEFAULT 1,
    name               varchar(255) NOT NULL,
    description        varchar(2048),
    created_at         timestamptz  NOT NULL DEFAULT now(),
    updated_at         timestamptz  NOT NULL DEFAULT now(),
    CONSTRAINT unique_article_number_and_unit UNIQUE (article_number, unit),
    CONSTRAINT check_quantity_is_positive CHECK (quantity > 0)
    );

CREATE INDEX IF NOT EXISTS index_article_article_number
    ON local.article (article_number);

CREATE INDEX IF NOT EXISTS index_article_category
    ON local.article (category);

CREATE INDEX IF NOT EXISTS index_article_product_id
    ON local.article (product_id);

CREATE TABLE IF NOT EXISTS local.warehouse (
    varchar(36) PRIMARY KEY,
    name                 varchar(255) NOT NULL,
    street               varchar(255),
    zip                  varchar(32),
    city                 varchar(255),
    country              varchar(255),
    created_at           timestamptz  NOT NULL DEFAULT now(),
    updated_at           timestamptz  NOT NULL DEFAULT now(),
    CONSTRAINT unique_warehouse_name UNIQUE (name)
    );

CREATE TABLE IF NOT EXISTS local.article_stock (
    article_id           varchar(36) NOT NULL,
    warehouse_id         varchar(36) NOT NULL,
    quantity_on_hand     bigint      NOT NULL DEFAULT 0,
    quantity_reserved    bigint      NOT NULL DEFAULT 0,
    quantity_confirmed   bigint      NOT NULL DEFAULT 0,
    quantity_delivered   bigint      NOT NULL DEFAULT 0,
    quantity_available   bigint GENERATED ALWAYS AS (quantity_on_hand - quantity_reserved - quantity_confirmed) STORED,
    is_available         boolean GENERATED ALWAYS AS ((quantity_on_hand - quantity_reserved - quantity_confirmed) > 0) STORED,
    version              bigint      NOT NULL DEFAULT 0,
    updated_at           timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT pk_article_stock PRIMARY KEY (article_id, warehouse_id),
    CONSTRAINT fk_article_stock_article
    FOREIGN KEY (article_id) REFERENCES local.article(id) ON DELETE CASCADE,
    CONSTRAINT fk_article_stock_warehouse
    FOREIGN KEY (warehouse_id) REFERENCES local.warehouse(id) ON DELETE CASCADE,
    CONSTRAINT check_quantities_non_negative CHECK (
        quantity_on_hand    >= 0 AND
        quantity_reserved   >= 0 AND
        quantity_confirmed  >= 0 AND
        quantity_delivered  >= 0
    ));

CREATE INDEX IF NOT EXISTS index_article_stock_quantity_available
    ON local.article_stock (quantity_available);

CREATE OR REPLACE VIEW local.article_availability_view AS
SELECT
    stock.article_id,
    stock.warehouse_id,
    stock.quantity_on_hand,
    stock.quantity_reserved,
    stock.quantity_confirmed,
    stock.quantity_delivered,
    stock.quantity_available,
    stock.is_available,
    stock.updated_at
FROM local.article_stock stock;

CREATE TABLE IF NOT EXISTS local.reservation (
    id                 varchar(36) PRIMARY KEY,
    article_id         varchar(36),
    warehouse_id       varchar(36),
    event_id           varchar(36),
    quantity           bigint       NOT NULL DEFAULT 0,
    reserved_quantity  bigint       NOT NULL DEFAULT 0,
    status             varchar(32),
    created_at         timestamptz  NOT NULL DEFAULT now(),
    updated_at         timestamptz  NOT NULL DEFAULT now()
    );

ALTER TABLE local.reservation
    ADD CONSTRAINT fk_reservation_article
        FOREIGN KEY (article_id) REFERENCES local.article(id) ON DELETE CASCADE;

ALTER TABLE local.reservation
    ADD CONSTRAINT fk_reservation_warehouse
        FOREIGN KEY (warehouse_id) REFERENCES local.warehouse(id) ON DELETE CASCADE;

CREATE INDEX IF NOT EXISTS index_reservation_article_id
    ON local.reservation (article_id);

CREATE INDEX IF NOT EXISTS index_reservation_warehouse_id
    ON local.reservation (warehouse_id);

CREATE INDEX IF NOT EXISTS index_reservation_event_id
    ON local.reservation (event_id);

CREATE TABLE IF NOT EXISTS local.stock (
    id           varchar(36) PRIMARY KEY,
    article_id   varchar(36)  NOT NULL,
    warehouse_id varchar(36)  NOT NULL,
    quantity     bigint       NOT NULL DEFAULT 0,
    created_at   timestamptz  NOT NULL DEFAULT now(),
    updated_at   timestamptz  NOT NULL DEFAULT now(),
    CONSTRAINT unique_stock_article_and_warehouse UNIQUE (article_id, warehouse_id)
    );

ALTER TABLE local.stock
    ADD CONSTRAINT fk_stock_article
        FOREIGN KEY (article_id) REFERENCES local.article(id) ON DELETE CASCADE;

ALTER TABLE local.stock
    ADD CONSTRAINT fk_stock_warehouse
        FOREIGN KEY (warehouse_id) REFERENCES local.warehouse(id) ON DELETE CASCADE;

CREATE INDEX IF NOT EXISTS index_stock_article_id
    ON local.stock (article_id);

CREATE INDEX IF NOT EXISTS index_stock_warehouse_id
    ON local.stock (warehouse_id);