package com.brunovelcl.pricetracker.DataParser.entities;
import com.brunovelcl.pricetracker.database.entities.Stores;
import lombok.Getter;

import java.math.BigDecimal;

import static com.brunovelcl.pricetracker.Text.Text.Constants.LEVEL_1_DELIMITER;

@Getter
public class ParsedValues {

    private final Long barcode;
    private final BigDecimal price;
    private final String productName;
    private final String brand;
    private final String unit_quantity;
    private final String unit;
    private final Stores store;

    public ParsedValues(Long barcode, BigDecimal price, String productName, String brand, String unit_quantity, String unit, Stores store) {
        this.barcode = barcode;
        this.price = price;
        this.productName = productName;
        this.brand = brand;
        this.unit_quantity = unit_quantity;
        this.unit = unit;
        this.store = store;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        ParsedValues that = (ParsedValues) o;
        return barcode.equals(that.barcode) && store.equals(that.store);
    }

    @Override
    public int hashCode() {
        int result = barcode.hashCode();
        result = 31 * result + store.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ParsedValues{" +
                "barcode=" + barcode +
                ", price=" + price +
                ", productName='" + productName + '\'' +
                ", brand='" + brand + '\'' +
                ", unit_quantity='" + unit_quantity + '\'' +
                ", unit='" + unit + '\'' +
                ", store=" + store +
                '}';
    }

    public String toLine(){
        return this.barcode + LEVEL_1_DELIMITER +
                this.productName + LEVEL_1_DELIMITER +
                this.brand + LEVEL_1_DELIMITER +
                this.unit_quantity + LEVEL_1_DELIMITER +
                this.unit + LEVEL_1_DELIMITER +
                this.store.getId() + LEVEL_1_DELIMITER +
                this.price;
    }

}

