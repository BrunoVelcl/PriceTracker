package com.brunovelcl.pricetracker.controller;

import com.brunovelcl.pricetracker.DTO.request.PriceQueryDTO;
import com.brunovelcl.pricetracker.DTO.response.PriceDataDTO;
import com.brunovelcl.pricetracker.DTO.response.ProductNameBarcodeDTO;
import com.brunovelcl.pricetracker.DataParser.entities.Store;
import com.brunovelcl.pricetracker.ProductManager.Entities.Product;
import com.brunovelcl.pricetracker.services.ProductService;
import com.brunovelcl.pricetracker.services.StoreService;
import com.brunovelcl.pricetracker.services.StoreServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class Controller {

    private final StoreService storeService;
    private final ProductService productService;


    public Controller(StoreService storeService, ProductService productService) {
        this.storeService = storeService;
        this.productService = productService;
    }

    @GetMapping("/stores")
    public ResponseEntity<List<Store>> getAllStores(){
        var rv = this.storeService.getStores();
        return (rv.isEmpty()) ? ResponseEntity.noContent().build() : ResponseEntity.ok(rv);
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductNameBarcodeDTO>> getProductsByName(@RequestParam String name){
        var rv = this.productService.findByName(name);
        return (rv.isEmpty()) ? ResponseEntity.noContent().build() : ResponseEntity.ok(rv);
    }

    @GetMapping("/prices")
    public ResponseEntity<List<PriceDataDTO>> getPrices(@RequestBody @Validated PriceQueryDTO priceQueryDTO){
        var rv = this.productService.getPrices(priceQueryDTO);
        return (rv.isEmpty()) ? ResponseEntity.noContent().build() : ResponseEntity.ok(rv);
    }
}
