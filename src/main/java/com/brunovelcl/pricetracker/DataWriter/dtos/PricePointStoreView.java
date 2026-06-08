package com.brunovelcl.pricetracker.DataWriter.dtos;

import java.time.Instant;

public interface PricePointStoreView {
    Long getPricePointId();
    Integer getStoreId();
    Instant getLastUpdated();
}
