CREATE TABLE chains(
    id SMALLINT UNIQUE PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    url VARCHAR(100)
);

CREATE TABLE stores(
    id SERIAL PRIMARY KEY,
    address VARCHAR(100) NOT NULL,
    chain_id INT NOT NULL,
    FOREIGN KEY (chain_id) REFERENCES chains(id)
);

CREATE TABLE products(
    id BIGINT UNIQUE PRIMARY KEY, -- id by barcode
    name VARCHAR(100) NOT NULL,
    brand VARCHAR(50),
    unit_quantity VARCHAR (50),
    unit VARCHAR (50)
);

CREATE TABLE prices(
    id BIGSERIAL PRIMARY KEY,
    price NUMERIC(8,2),
    store_id INT NOT NULL,
    product_id BIGINT NOT NULL,
    updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (store_id) REFERENCES stores(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

