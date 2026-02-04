package com.brunovelcl.pricetracker.DataParser.repositories;

import com.brunovelcl.pricetracker.DataParser.entities.Store;

import java.util.List;

public interface StoreRepo {
    List<Store> getStores();
    void loadFromFile();
    void appendStoreToFile(Store store);
}
