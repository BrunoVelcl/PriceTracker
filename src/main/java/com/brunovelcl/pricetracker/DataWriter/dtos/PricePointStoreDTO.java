package com.brunovelcl.pricetracker.DataWriter.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class PricePointStoreDTO {
    private Long pricePointId;
    private Integer storeId;
    private Instant lastUpdated;
}
