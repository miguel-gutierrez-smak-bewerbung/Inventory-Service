-- liquibase formatted sql

-- =========================================================
-- changeset miguel:000_schema
-- Purpose: Create schema local (if not exists)
-- =========================================================
CREATE SCHEMA IF NOT EXISTS local;

-- =========================================================
-- changeset miguel:001_article
-- validCheckSum: 9:dbfc959c96d3b15d5121e49fce091e3c
-- Purpose: Article master data
-- =========================================================
CREATE TABLE IF NOT EXISTS local.article (
  id                 varchar(36) PRIMARY KEY,
  article_number     varchar(255) NOT NULL,
  unit               varchar(64)  NOT NULL,
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

-- =========================================================
-- changeset miguel:002_warehouse
-- Purpose: Warehouse master data
-- =========================================================
CREATE TABLE IF NOT EXISTS local.warehouse (
    id                   varchar(36) PRIMARY KEY,
    name                 varchar(255) NOT NULL,
    street               varchar(255),
    zip                  varchar(32),
    city                 varchar(255),
    country              varchar(255),
    created_at           timestamptz  NOT NULL DEFAULT now(),
    updated_at           timestamptz  NOT NULL DEFAULT now(),
    CONSTRAINT unique_warehouse_name UNIQUE (name)
);

-- =========================================================
-- changeset miguel:003_article_stock
-- Purpose: Stock per article and warehouse
-- =========================================================
CREATE TABLE IF NOT EXISTS local.article_stock (
    article_id           varchar(36) NOT NULL,
    warehouse_id         varchar(36) NOT NULL,
    quantity_on_hand     bigint      NOT NULL DEFAULT 0,
    quantity_reserved    bigint      NOT NULL DEFAULT 0,
    quantity_confirmed   bigint      NOT NULL DEFAULT 0,
    quantity_delivered   bigint      NOT NULL DEFAULT 0,
    quantity_available   bigint GENERATED ALWAYS AS(quantity_on_hand - quantity_reserved - quantity_confirmed) STORED,
    is_available         boolean GENERATED ALWAYS AS((quantity_on_hand - quantity_reserved - quantity_confirmed) > 0) STORED,
    version              bigint      NOT NULL DEFAULT 0,
    updated_at           timestamptz NOT NULL DEFAULT now(),

    CONSTRAINT pk_article_stock PRIMARY KEY (article_id, warehouse_id),

    CONSTRAINT fk_article_stock_article
    FOREIGN KEY (article_id)
    REFERENCES local.article(id)
    ON DELETE CASCADE,

    CONSTRAINT fk_article_stock_warehouse
    FOREIGN KEY (warehouse_id)
    REFERENCES local.warehouse(id)
    ON DELETE CASCADE,

    CONSTRAINT check_quantities_non_negative CHECK (
          quantity_on_hand    >= 0 AND
          quantity_reserved   >= 0 AND
          quantity_confirmed  >= 0 AND
          quantity_delivered  >= 0
          )
    );

CREATE INDEX IF NOT EXISTS index_article_stock_quantity_available
    ON local.article_stock (quantity_available);

-- =========================================================
-- changeset miguel:004_article_availability_view
-- validCheckSum: 9:fad2bfd35643dfc068ce36b1b97f8c62
-- Purpose: Read-friendly view on availability
-- =========================================================
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

-- =========================================================
-- changeset miguel:005_article_add_category
-- validCheckSum: 9:feb92ad18c7e0513ed4134cb294f052b
-- Purpose: add category to article
-- =========================================================
ALTER TABLE local.article
    ADD COLUMN IF NOT EXISTS category varchar(255);

CREATE INDEX IF NOT EXISTS index_article_category
    ON local.article (category);

-- =========================================================
-- changeset miguel:006_article_add_product_id
-- validCheckSum: 9:fc86a92df58177101f314b3245520808
-- Purpose: add product_id to article for relation to product aggregate
-- =========================================================
ALTER TABLE local.article
    ADD COLUMN IF NOT EXISTS product_id varchar(36);

CREATE INDEX IF NOT EXISTS index_article_product_id
    ON local.article (product_id);


-- =========================================================
-- changeset miguel:007_article_add_quantity
-- validCheckSum: 9:0fb97f907ed25cedf2ca05a43e072896
-- Purpose: ensure column quantity exists as required by ArticleEntity
-- =========================================================
ALTER TABLE local.article ADD COLUMN IF NOT EXISTS quantity integer NOT NULL DEFAULT 0;

-- =========================================================
-- changeset miguel:008_create_reservation
-- validCheckSum: 9:82c831acfc9225ba1591e8ca414106f9
-- Purpose: create reservation table required by JPA entity
-- =========================================================
CREATE TABLE IF NOT EXISTS local.reservation (
    id                 varchar(36) PRIMARY KEY,
    article_id         varchar(36),
    quantity           bigint NOT NULL DEFAULT 0,
    created_at         timestamptz NOT NULL DEFAULT now(),
    updated_at         timestamptz NOT NULL DEFAULT now()
);

ALTER TABLE local.reservation
    ADD CONSTRAINT fk_reservation_article
        FOREIGN KEY (article_id)
        REFERENCES local.article(id)
        ON DELETE CASCADE;

CREATE INDEX IF NOT EXISTS index_reservation_article_id
    ON local.reservation (article_id);

-- =========================================================
-- changeset miguel:009_reservation_add_event_id
-- validCheckSum: 9:66d0e0dcdd3db8cdc2648fbb7ae94963
-- Purpose: add event_id column to reservation
-- =========================================================
ALTER TABLE local.reservation
    ADD COLUMN IF NOT EXISTS event_id varchar(36);

CREATE INDEX IF NOT EXISTS index_reservation_event_id
    ON local.reservation (event_id);

-- =========================================================
-- changeset miguel:010_reservation_add_reserved_quantity
-- validCheckSum: 9:9464c3bb1e6c512c8044931bc59235f9
-- Purpose: add reserved_quantity column required by JPA
-- =========================================================
ALTER TABLE local.reservation
    ADD COLUMN IF NOT EXISTS reserved_quantity bigint NOT NULL DEFAULT 0;

-- =========================================================
-- changeset miguel:011_reservation_add_status
-- validCheckSum: 9:59b6ab06ec3f4cc04ff4e95bc6a3f501
-- Purpose: add status column to reservation
-- =========================================================
ALTER TABLE local.reservation
    ADD COLUMN IF NOT EXISTS status varchar(32);

-- =========================================================
-- changeset miguel:013_reservation_add_warehouse_id
-- validCheckSum: 9:b1f681d61c213d142bf1690540f30e9a
-- Purpose: add warehouse_id to reservation incl. FK and index
-- =========================================================
ALTER TABLE local.reservation
    ADD COLUMN IF NOT EXISTS warehouse_id varchar(36);

CREATE INDEX IF NOT EXISTS index_reservation_warehouse_id
    ON local.reservation (warehouse_id);

ALTER TABLE local.reservation
    ADD CONSTRAINT fk_reservation_warehouse
        FOREIGN KEY (warehouse_id)
        REFERENCES local.warehouse(id)
        ON DELETE CASCADE;

-- =========================================================
-- changeset miguel:014_create_stock
-- Purpose: create stock table required by StockEntity
-- =========================================================
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
        FOREIGN KEY (article_id)
        REFERENCES local.article(id)
        ON DELETE CASCADE;

ALTER TABLE local.stock
    ADD CONSTRAINT fk_stock_warehouse
        FOREIGN KEY (warehouse_id)
        REFERENCES local.warehouse(id)
        ON DELETE CASCADE;

CREATE INDEX IF NOT EXISTS index_stock_article_id
    ON local.stock (article_id);

CREATE INDEX IF NOT EXISTS index_stock_warehouse_id
    ON local.stock (warehouse_id);