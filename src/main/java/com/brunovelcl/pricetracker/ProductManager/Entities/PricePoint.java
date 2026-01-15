package com.brunovelcl.pricetracker.ProductManager.Entities;

import com.brunovelcl.pricetracker.DataParser.entities.Store;

import java.util.ArrayList;
import java.util.List;

public class PricePoint {
    private final Double price;
    private final List<Store> stores;

    public PricePoint(Double price) {
        this.price = price;
        this.stores = new ArrayList<>();
    }

    public PricePoint(Double price, Store store) {
        this.price = price;
        this.stores = new ArrayList<>();
        this.stores.add(store);
    }

    public Double getPrice() {
        return price;
    }

    public List<Store> getStores() {
        return stores;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        PricePoint that = (PricePoint) o;
        return price.equals(that.price);
    }

    @Override
    public int hashCode() {
        return price.hashCode();
    }

}
