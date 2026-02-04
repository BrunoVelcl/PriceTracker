package com.brunovelcl.pricetracker.controller;

import com.brunovelcl.pricetracker.DataParser.entities.Store;
import com.brunovelcl.pricetracker.services.StoreService;
import com.brunovelcl.pricetracker.services.StoreServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class Controller {

    StoreService storeService;

    public Controller(StoreService storeService) {
        this.storeService = storeService;
    }

    @GetMapping("/stores")
    public ResponseEntity<List<Store>> getAllStores(){
        var rv = this.storeService.getStores();
        return (rv.isEmpty()) ? ResponseEntity.noContent().build() : ResponseEntity.ok(rv);
    }
}
