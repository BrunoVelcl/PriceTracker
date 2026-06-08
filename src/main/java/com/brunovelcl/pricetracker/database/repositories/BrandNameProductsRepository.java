package com.brunovelcl.pricetracker.database.repositories;

import com.brunovelcl.pricetracker.DataWriter.dtos.ProductIdView;
import com.brunovelcl.pricetracker.database.entities.BrandNameProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BrandNameProductsRepository extends JpaRepository<BrandNameProduct, Long> {
    Optional<BrandNameProduct> findByBarcode(Long barcode);

    @Query(
            value = "SELECT id, barcode FROM brand_name_products",
            nativeQuery = true
    )
    List<ProductIdView> findAllIds();
}
