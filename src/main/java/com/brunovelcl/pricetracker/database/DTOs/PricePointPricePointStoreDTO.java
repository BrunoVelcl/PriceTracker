package com.brunovelcl.pricetracker.database.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@AllArgsConstructor
public class PricePointPricePointStoreDTO {

    private final Long pricePointId;
    private final BigDecimal price;
    private final Long brandNameProductId;
    private final Long pricePointStoreId;
    private final Integer storeId;
    private final Instant lastUpdated;
}
