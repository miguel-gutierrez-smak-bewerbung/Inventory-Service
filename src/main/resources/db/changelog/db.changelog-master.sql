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