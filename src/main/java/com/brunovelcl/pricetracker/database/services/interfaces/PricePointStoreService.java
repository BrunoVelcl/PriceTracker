package com.brunovelcl.pricetracker.database.services.interfaces;

import com.brunovelcl.pricetracker.database.DTOs.PricePointPricePointStoreDTO;
import com.brunovelcl.pricetracker.database.entities.PricePoint;
import com.brunovelcl.pricetracker.database.entities.PricePointStore;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PricePointStoreService {
    PricePointStore save(PricePointStore pricePointStore);
    Optional<PricePointPricePointStoreDTO> findByBrandNameProductAndStore(Long productId, Integer storeId);
    void delete(PricePointStore pricePointStore);
}
