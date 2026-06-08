package com.brunovelcl.pricetracker.DataWriter.dtos;

import lombok.Data;
import java.util.HashMap;
import java.util.Map;

@Data
public class ProductDTO {
    private Long productId;
    private Long barcode;
    private Map<Long, PricePointDTO> pricePointDTOS;

    public ProductDTO(Long productId, Long barcode) {
        this.productId = productId;
        this.barcode = barcode;
        this.pricePointDTOS = new HashMap<>();
    }
}
