package com.brunovelcl.pricetracker.DataWriter.dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class PricePointDTO {

    private Long id;

    private BigDecimal price;

    private Long productId;

    List<PricePointStoreDTO> pricePointStoreDTOS;

    public PricePointDTO(Long id, BigDecimal price, Long productId) {
        this.id = id;
        this.price = price;
        this.productId = productId;
        this.pricePointStoreDTOS = new ArrayList<>();
    }
}
