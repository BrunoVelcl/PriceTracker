package com.brunovelcl.pricetracker.database.entities;

import com.brunovelcl.pricetracker.DataParser.entities.ParsedValues;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Entity
@Data
@Getter
@Table (name = "brand_name_products")
public class BrandNameProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private Long barcode;

    @Column(nullable = false)
    private String productName;

    @Column
    private String brand;

    @Column
    private String unitQuantity;

    @Column
    private String unit;

    public BrandNameProduct(Long barcode, String productName, String brand, String unit_quantity, String unit) {
        this.barcode = barcode;
        this.productName = productName;
        this.brand = brand;
        this.unitQuantity = unit_quantity;
        this.unit = unit;
    }

    public static BrandNameProduct mapFromParsedValue(ParsedValues parsedValues){
        return new BrandNameProduct(
                parsedValues.getBarcode(),
                parsedValues.getProductName(),
                parsedValues.getBrand(),
                parsedValues.getUnit_quantity(),
                parsedValues.getUnit()
        );
    }
}
