package com.brunovelcl.pricetracker.DataWriter.dtos;

import java.math.BigDecimal;

public interface PricePointView {
    Long getId();
    BigDecimal getPriceEuros();
    Long getBrandNameProductId();
}
