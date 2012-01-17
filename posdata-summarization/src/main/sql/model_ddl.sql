DROP TABLE IF EXISTS ITEM;
DROP TABLE IF EXISTS STORE;
DROP TABLE IF EXISTS CATEGORY;

CREATE TABLE ITEM (
  GTIN BIGINT,
  ITEM_NAME VARCHAR(255),
  APPLY_START_DATE DATE,
  APPLY_END_DATE DATE,
  PRIMARY KEY (GTIN)
);

CREATE TABLE STORE (
  STORE_CODE INT,
  STORE_NAME VARCHAR(255),
  APPLY_START_DATE DATE,
  APPLY_END_DATE DATE,
  PRIMARY KEY (STORE_CODE)
);

CREATE TABLE CATEGORY (
  JAN_CODE BIGINT,
  ITEM_NAME VARCHAR(255),
  SECTION_CODE INT,
  SECTION_NAME VARCHAR(255),
  CATEGORY_CODE_1 INT,
  CATEGORY_NAME_1 VARCHAR(255),
  CATEGORY_CODE_2 INT,
  CATEGORY_NAME_2 VARCHAR(255),
  PRIMARY KEY (JAN_CODE)
);