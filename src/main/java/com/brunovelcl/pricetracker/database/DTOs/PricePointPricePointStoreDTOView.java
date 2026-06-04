package com.brunovelcl.pricetracker.database.DTOs;

import java.math.BigDecimal;
import java.time.Instant;

public interface PricePointPricePointStoreDTOView {
    Long getPricePointId();
    BigDecimal getPrice();
    Long getBrandNameProductId();
    Long getPricePointStoreId();
    Integer getStoreId();
    Instant getLastUpdated();
}
