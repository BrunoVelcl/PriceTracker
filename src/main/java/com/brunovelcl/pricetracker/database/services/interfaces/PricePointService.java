package com.brunovelcl.pricetracker.database.services.interfaces;

import com.brunovelcl.pricetracker.database.entities.BrandNameProduct;
import com.brunovelcl.pricetracker.database.entities.PricePoint;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public interface PricePointService {
    PricePoint save(PricePoint pricePoint);

    Optional<PricePoint> findByPriceAndBrandNameProduct(BigDecimal price, BrandNameProduct product);

    List<PricePoint> findByBrandNameProduct(BrandNameProduct brandNameProduct);
}
