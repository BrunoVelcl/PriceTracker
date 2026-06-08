package com.brunovelcl.pricetracker.database.repositories;

import com.brunovelcl.pricetracker.DataWriter.dtos.PricePointRowDTO;

import java.util.List;

public interface PricePointRepositoryCustom {
    void ingestTable(List<PricePointRowDTO> list, String tableName) throws Exception;
}
