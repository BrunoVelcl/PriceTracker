package com.brunovelcl.pricetracker.database.repositories;

import com.brunovelcl.pricetracker.DataWriter.dtos.PricePointStoreDTO;

import java.util.List;

public interface PricePointStoreRepositoryCustom {
    void ingestTable(List<PricePointStoreDTO> list, String tableName) throws Exception;
}
