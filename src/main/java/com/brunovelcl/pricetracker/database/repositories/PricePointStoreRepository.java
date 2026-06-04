package com.brunovelcl.pricetracker.database.repositories;

import com.brunovelcl.pricetracker.database.DTOs.PricePointPricePointStoreDTO;
import com.brunovelcl.pricetracker.database.DTOs.PricePointPricePointStoreDTOView;
import com.brunovelcl.pricetracker.database.entities.BrandNameProduct;
import com.brunovelcl.pricetracker.database.entities.PricePoint;
import com.brunovelcl.pricetracker.database.entities.PricePointStore;
import com.brunovelcl.pricetracker.database.entities.Stores;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PricePointStoreRepository extends JpaRepository<PricePointStore, Long> {
    
    @Query(
            value = """
                    SELECT pp.id as pricePointId, pp.price_euros as price, pp.brand_name_product_id as brandNameProductId,
                    pps.id as pricePointStoreId, pps.store_id as storeId, pps.last_updated as lastUpdated
                    FROM price_points pp
                    JOIN price_point_store pps ON pps.price_point_id = pp.id
                    WHERE pp.brand_name_product_id = :product_id AND pps.store_id = :store_id
                    LIMIT 1;
                    """,
            nativeQuery = true
    )
    List<PricePointPricePointStoreDTOView> findByBrandNameProductAndStore(@Param("product_id") Long productId, @Param("store_id")Integer storeId);
}
