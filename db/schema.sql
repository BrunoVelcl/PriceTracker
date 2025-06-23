CREATE TABLE `chains`(
    `id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(50) NOT NULL,
    `url` VARCHAR(100)
    
);

CREATE TABLE `stores`(
    `id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `address` VARCHAR(100) NOT NULL,
    `chain` VARCHAR(50) NOT NULL,
    FOREIGN KEY (`chain`) REFERENCES `chains`.(`id`)
);

CREATE TABLE `products`(
    `id` INT UNSIGNED PRIMARY KEY, -- id by barcode
    `name` VARCHAR(100) NOT NULL,
    `brand` VARCHAR(50),
    `price` NUMERIC(6,2) NOT NULL,
    `quantity` VARCHAR (10),
    `quantity_unit` unit,
    FOREIGN KEY (`store_id`) REFERENCES `stores`.(`id`)
    
);


CREATE TYPE unit AS ENUM (`KOM`, `KG`);