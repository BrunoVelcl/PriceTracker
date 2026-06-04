package com.brunovelcl.pricetracker.database.repositories;

import com.brunovelcl.pricetracker.database.entities.BrandNameProduct;
import com.brunovelcl.pricetracker.database.entities.PricePoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface PricePointRepository extends JpaRepository<PricePoint, Long> {
    List<PricePoint> findByBrandNameProduct(BrandNameProduct brandNameProduct);
    Optional<PricePoint> findByPriceAndBrandNameProduct(BigDecimal price, BrandNameProduct product);
}
