package com.brunovelcl.pricetracker.DTO.request;

import lombok.Data;

import java.util.List;

@Data
public class PriceQueryDTO {
    private final List<Short> storeCodes;
    private final List<Long> productCodes;
}
