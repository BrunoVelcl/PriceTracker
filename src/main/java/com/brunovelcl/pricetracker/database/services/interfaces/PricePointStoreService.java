package com.brunovelcl.pricetracker.database.services.interfaces;

import com.brunovelcl.pricetracker.DataWriter.dtos.PricePointStoreDTO;
import com.brunovelcl.pricetracker.database.entities.PricePointStore;
import java.util.List;


public interface PricePointStoreService {
    PricePointStore save(PricePointStore pricePointStore);
    void delete(PricePointStore pricePointStore);
    List<PricePointStore> findAll();
    List<PricePointStoreDTO> findAllCustom();
    void ingestTable(List<PricePointStoreDTO> list, String tableName) throws Exception;
}
