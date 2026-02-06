package com.brunovelcl.pricetracker.services;

import com.brunovelcl.pricetracker.DTO.request.PriceQueryDTO;
import com.brunovelcl.pricetracker.DTO.response.PriceDataDTO;
import com.brunovelcl.pricetracker.DTO.response.ProductNameBarcodeDTO;
import com.brunovelcl.pricetracker.ProductManager.ProductManager;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ProductServiceImpl implements ProductService{

    private final ProductManager productManager;

    public ProductServiceImpl(ProductManager productManager) {
        this.productManager = productManager;
    }

    public List<ProductNameBarcodeDTO> findByName(String name){
        return productManager.findByProductName(name);
    }

    public List<PriceDataDTO> getPrices(PriceQueryDTO dto){
        return this.productManager.getPrices(dto);
    }
}
