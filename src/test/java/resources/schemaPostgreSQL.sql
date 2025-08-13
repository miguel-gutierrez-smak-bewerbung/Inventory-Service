CREATE SCHEMA IF NOT EXISTS local;
SET SCHEMA local;

-- article
CREATE TABLE IF NOT EXISTS local.article (
  id               varchar(36) PRIMARY KEY,
  article_number   varchar(255) NOT NULL,
  unit             varchar(64)  NOT NULL,
  quantity         integer      NOT NULL DEFAULT 1,
  name             varchar(255) NOT NULL,
  description      varchar(2048),
  created_at       TIMESTAMP    NOT NULL DEFAULT now(),
  updated_at       TIMESTAMP    NOT NULL DEFAULT now(),
  CONSTRAINT unique_article_number_and_unit UNIQUE (article_number, unit),
  CONSTRAINT check_quantity_is_positive CHECK (quantity > 0)
);
CREATE INDEX IF NOT EXISTS index_article_article_number ON local.article (article_number);

-- warehouse
CREATE TABLE IF NOT EXISTS local.warehouse (
  id         varchar(36) PRIMARY KEY,
  name       varchar(255) NOT NULL,
  street     varchar(255),
  zip        varchar(32),
  city       varchar(255),
  country    varchar(255),
  created_at TIMESTAMP NOT NULL DEFAULT now(),
  updated_at TIMESTAMP NOT NULL DEFAULT now(),
  CONSTRAINT unique_warehouse_name UNIQUE (name)
);

-- article_stock
CREATE TABLE IF NOT EXISTS local.article_stock (
  article_id           varchar(36) NOT NULL,
  warehouse_id         varchar(36) NOT NULL,
  quantity_on_hand     bigint      NOT NULL DEFAULT 0,
  quantity_reserved    bigint      NOT NULL DEFAULT 0,
  quantity_confirmed   bigint      NOT NULL DEFAULT 0,
  quantity_delivered   bigint      NOT NULL DEFAULT 0,
  quantity_available   bigint      NOT NULL DEFAULT 0,
  is_available         boolean     NOT NULL DEFAULT false,
  version              bigint      NOT NULL DEFAULT 0,
  updated_at           TIMESTAMP   NOT NULL DEFAULT now(),

  CONSTRAINT pk_article_stock PRIMARY KEY (article_id, warehouse_id),

  CONSTRAINT fk_article_stock_article
    FOREIGN KEY (article_id) REFERENCES local.article(id) ON DELETE CASCADE,

  CONSTRAINT fk_article_stock_warehouse
    FOREIGN KEY (warehouse_id) REFERENCES local.warehouse(id) ON DELETE CASCADE,

  CONSTRAINT check_quantities_non_negative CHECK (
    quantity_on_hand   >= 0 AND
    quantity_reserved  >= 0 AND
    quantity_confirmed >= 0 AND
    quantity_delivered >= 0
  )
);

CREATE INDEX IF NOT EXISTS index_article_stock_quantity_available
  ON local.article_stock (quantity_available);

-- View
CREATE OR REPLACE VIEW local.article_availability_view AS
SELECT
  stock.article_id,
  stock.warehouse_id,
  stock.quantity_on_hand,
  stock.quantity_reserved,
  stock.quantity_confirmed,
  stock.quantity_delivered,
  (stock.quantity_on_hand - stock.quantity_reserved - stock.quantity_confirmed) AS quantity_available,
  ((stock.quantity_on_hand - stock.quantity_reserved - stock.quantity_confirmed) > 0)               AS is_available,
  stock.updated_at
FROM local.article_stock stock;

--
ALTER TABLE local.article
  ADD COLUMN IF NOT EXISTS category varchar(255);
CREATE INDEX IF NOT EXISTS index_article_category ON local.article (category);