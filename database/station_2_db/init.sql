CREATE DATABASE stationdb;
\c stationdb;

CREATE TABLE IF NOT EXISTS charge (
   id SERIAL PRIMARY KEY,
   kwh REAL NOT NULL,
   customer_id INTEGER NOT NULL,
   invoice_id VARCHAR,
   price REAL NOT NULL
);

INSERT INTO charge(id, kwh, customer_id, price)
VALUES 
   (1, '48.2', 3, '0.249'),
   (2, '40.5', 3, '0.249'),
   (3, '32.9', 1, '0.249'),
   (4, '35.3', 3, '0.249'),
   (5, '12.6', 3, '0.249'),
   (6, '41.3', 1, '0.249'),
   (7, '24.4', 2, '0.249'),
   (8, '10.4', 3, '0.249'),
   (9, '20.5', 2, '0.249'),
   (10, '29', 3, '0.249'),
   (11, '43.5', 1, '0.249'),
   (12, '38.9', 2, '0.249'),
   (13, '18.6', 2, '0.249'),
   (14, '31.7', 1, '0.249'),
   (15, '32.1', 2, '0.249'),
   (16, '13.8', 1, '0.249'),
   (17, '19.8', 2, '0.249'),
   (18, '19.1', 1, '0.249'),
   (19, '45.4', 2, '0.249'),
   (20, '38.1', 2, '0.249');