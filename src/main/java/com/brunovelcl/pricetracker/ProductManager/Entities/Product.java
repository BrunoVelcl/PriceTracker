package com.brunovelcl.pricetracker.ProductManager.Entities;

import com.brunovelcl.pricetracker.DataParser.entities.ParsedValues;
import com.brunovelcl.pricetracker.DataParser.entities.Store;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Product {
    private final String name;
    private final Long barcode;
    private final String brand;
    private final String unit_quantity;
    private final String unit;

    private final List<PricePoint> prices;

    public Product(ParsedValues parsedValue){
        this.name = parsedValue.getProductName();
        this.barcode = parsedValue.getBarcode();
        this.brand = parsedValue.getBrand();
        this.unit_quantity = parsedValue.getUnit_quantity();
        this.unit = parsedValue.getUnit();
        this.prices = new ArrayList<>();
        this.prices.add(new PricePoint(
                parsedValue.getPrice(),
                parsedValue.getStoreInfo()
        ));
    }

    public Product(String name, Long barcode, String brand, String unit_quantity, String unit, List<PricePoint> prices) {
        this.name = name;
        this.barcode = barcode;
        this.brand = brand;
        this.unit_quantity = unit_quantity;
        this.unit = unit;
        this.prices = prices;
    }

    public String getName() {
        return name;
    }

    public Long getBarcode() {
        return barcode;
    }

    public String getBrand() {
        return brand;
    }

    public String getUnit_quantity() {
        return unit_quantity;
    }

    public String getUnit() {
        return unit;
    }

    public List<PricePoint> getPrices() {
        return prices;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Product product = (Product) o;
        return Objects.equals(barcode, product.barcode);
    }

    @Override
    public int hashCode() {
        return Long.hashCode(barcode);
    }

    public Optional<PricePoint> findPricePoint(Double price){
        for(PricePoint pp : this.prices){
            if(pp.getPrice().equals(price)){
                return Optional.of(pp);
            }
        }
        return Optional.empty();
    }

    public void updatePrice(Double price, Store store){
        var optPricePoint = this.findStore(store);
        if(optPricePoint.isPresent()){
            var storeList = optPricePoint.get().getStores();
            storeList.remove(store);
            if(storeList.isEmpty()){
                this.prices.remove(optPricePoint.get());
            }
        }

        optPricePoint = this.findPricePoint(price);
        if(optPricePoint.isPresent()){
            optPricePoint.get().getStores().add(store);
        }else {
            this.prices.add(new PricePoint(price, store));
        }
    }

    private Optional<PricePoint> findStore(Store store){
        for(PricePoint pp : this.prices){
            if(pp.getStores().contains(store)){
                return Optional.of(pp);
            }
        }
        return Optional.empty();
    }
}
