package ProductManager.Entities.Builders;

import ProductManager.Entities.PricePoint;
import ProductManager.Entities.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductBuilder {
    private String name;
    private Long barcode;
    private String brand;
    private String unit_quantity;
    private String unit;

    private List<PricePoint> prices;

    public ProductBuilder() {
        this.prices = new ArrayList<>();
    }

    public void name(String name) {
        this.name = name;
    }

    public void barcode(Long barcode) {
        this.barcode = barcode;
    }

    public void brand(String brand) {
        this.brand = brand;
    }

    public void unitQuantity(String unit_quantity) {
        this.unit_quantity = unit_quantity;
    }

    public void unit(String unit) {
        this.unit = unit;
    }

    public void addPricePoint(PricePoint pricePoint) {
        this.prices.add(pricePoint);
    }

    // Used to avoid creating a Product before adding it to a hashmap
    public Long getBarcode() {
        return barcode;
    }

    public void reset(){
        this.name = null;
        this.barcode = null;
        this.brand = null;
        this.unit_quantity = null;
        this.unit = null;
        this.prices = new ArrayList<>();
    }

    public Product consume(){
        Product product =  new Product(this.name, this.barcode, this.brand, this.unit_quantity, this.unit, this.prices);
        this.reset();
        return product;
    }
}


