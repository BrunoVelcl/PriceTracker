package com.brunovelcl.pricetracker.services;

import com.brunovelcl.pricetracker.DataParser.entities.Store;
import com.brunovelcl.pricetracker.DataParser.repositories.StoreRepo;
import com.brunovelcl.pricetracker.ProductManager.ProductManager;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StoreServiceImpl implements StoreService {

    private final ProductManager productManager;

    public StoreServiceImpl(ProductManager productManager) {
        this.productManager = productManager;
    }

    public List<Store> getStores(){
        return this.productManager.getStores();
    }
}
