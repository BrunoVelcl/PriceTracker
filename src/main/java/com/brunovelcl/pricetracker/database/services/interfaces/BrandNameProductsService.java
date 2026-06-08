package com.brunovelcl.pricetracker.database.services.interfaces;

import com.brunovelcl.pricetracker.DataWriter.dtos.ProductDTO;
import com.brunovelcl.pricetracker.database.entities.BrandNameProduct;

import java.util.List;
import java.util.Optional;

public interface BrandNameProductsService {
    BrandNameProduct save(BrandNameProduct newBrandNameProduct);
    Optional<BrandNameProduct> findByBarcode(Long barcode);
    public List<ProductDTO> findAllProductIds();
}
