package com.brunovelcl.pricetracker.database.services.implementations;

import com.brunovelcl.pricetracker.database.entities.BrandNameProduct;
import com.brunovelcl.pricetracker.database.repositories.BrandNameProductsRepository;
import com.brunovelcl.pricetracker.database.services.interfaces.BrandNameProductsService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BrandNameProductsServiceImpl implements BrandNameProductsService {

    private final BrandNameProductsRepository brandNameProductsRepository;

    public BrandNameProductsServiceImpl(BrandNameProductsRepository brandNameProductsRepository) {
        this.brandNameProductsRepository = brandNameProductsRepository;
    }

    public BrandNameProduct save(BrandNameProduct newBrandNameProduct){
        return brandNameProductsRepository.save(newBrandNameProduct);
    }

    @Override
    public Optional<BrandNameProduct> findByBarcode(Long barcode) {
        return brandNameProductsRepository.findByBarcode(barcode);
    }
}
