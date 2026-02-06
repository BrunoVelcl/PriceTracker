package com.brunovelcl.pricetracker.services;

import com.brunovelcl.pricetracker.DTO.request.PriceQueryDTO;
import com.brunovelcl.pricetracker.DTO.response.PriceDataDTO;
import com.brunovelcl.pricetracker.DTO.response.ProductNameBarcodeDTO;

import java.util.List;

public interface ProductService {
    List<ProductNameBarcodeDTO> findByName(String name);
    List<PriceDataDTO> getPrices(PriceQueryDTO dto);
}
