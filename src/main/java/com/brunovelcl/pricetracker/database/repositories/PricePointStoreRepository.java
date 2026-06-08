package com.brunovelcl.pricetracker.database.repositories;

import com.brunovelcl.pricetracker.DataWriter.dtos.PricePointStoreView;
import com.brunovelcl.pricetracker.database.entities.PricePointStore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PricePointStoreRepository extends JpaRepository<PricePointStore, Long>, PricePointStoreRepositoryCustom {

    @Query(
            value = """
                    SELECT price_point_id as pricePointId, store_id as storeId, last_updated as lastUpdated
                    FROM price_point_store
                    """,
            nativeQuery = true
    )
    List<PricePointStoreView> findAllRows();
}
