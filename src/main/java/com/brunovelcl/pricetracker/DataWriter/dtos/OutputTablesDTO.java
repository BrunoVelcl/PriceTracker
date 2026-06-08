package com.brunovelcl.pricetracker.DataWriter.dtos;

import java.util.List;

public record OutputTablesDTO(List<PricePointRowDTO> pricePoints, List<PricePointStoreDTO> pricePointStore) {

    public static final String pricePointTableName = "price_points";
    public static final String pricePointStoreTableName = "price_point_store";

}
