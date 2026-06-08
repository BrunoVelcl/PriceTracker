package com.brunovelcl.pricetracker.database.repositories;

import com.brunovelcl.pricetracker.DataWriter.dtos.PricePointRowDTO;
import com.brunovelcl.pricetracker.DataWriter.dtos.PricePointView;
import com.brunovelcl.pricetracker.database.entities.BrandNameProduct;
import com.brunovelcl.pricetracker.database.entities.PricePoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface PricePointRepository extends JpaRepository<PricePoint, Long>, PricePointRepositoryCustom {

    List<PricePoint> findByBrandNameProduct(BrandNameProduct brandNameProduct);
    Optional<PricePoint> findByPriceAndBrandNameProduct(BigDecimal price, BrandNameProduct product);

    @Query(
            value = """
                    SELECT id, price_euros as priceEuros, brand_name_product_id as brandNameProductId
                    FROM price_points;
                    """,
            nativeQuery = true
    )
    List<PricePointView> findAllCustom();

}
