package com.brunovelcl.pricetracker.database.repositories;

import com.brunovelcl.pricetracker.database.entities.BrandNameProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BrandNameProductsRepository extends JpaRepository<BrandNameProduct, Long> {
    Optional<BrandNameProduct> findByBarcode(Long barcode);
}
