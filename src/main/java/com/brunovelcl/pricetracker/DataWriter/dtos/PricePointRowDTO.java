package com.brunovelcl.pricetracker.DataWriter.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class PricePointRowDTO {
    private Long id;
    private BigDecimal price;
    private Long productId;
}
